package com.aphatheology.elibrarybackend.service;

import com.aphatheology.elibrarybackend.dto.AuthenticationResponse;
import com.aphatheology.elibrarybackend.dto.EmailPayloadDto;
import com.aphatheology.elibrarybackend.dto.LoginDto;
import com.aphatheology.elibrarybackend.dto.UserDto;
import com.aphatheology.elibrarybackend.entity.Role;
import com.aphatheology.elibrarybackend.entity.Tokens;
import com.aphatheology.elibrarybackend.entity.Users;
import com.aphatheology.elibrarybackend.event.RegistrationCompleteEvent;
import com.aphatheology.elibrarybackend.exception.BadRequestException;
import com.aphatheology.elibrarybackend.exception.ExistingEmailException;
import com.aphatheology.elibrarybackend.exception.ResourceNotFoundException;
import com.aphatheology.elibrarybackend.repository.TokenRepository;
import com.aphatheology.elibrarybackend.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final ApplicationEventPublisher publisher;
    private final EmailService emailService;

    public Users map2Entity(UserDto userDto) {
        Users user = new Users();
        user.setEmail(userDto.getEmail());
        user.setFullname(userDto.getFullname());
        user.setRole(Role.USER);
        user.setPassword(this.passwordEncoder.encode(userDto.getPassword()));

        return user;
    }

    public String getApplicationUrl(HttpServletRequest request) {
        return "http://" +
                request.getServerName() +
                ":" +
                request.getServerPort() +
                request.getContextPath();
    }

    public AuthenticationResponse register(UserDto registerBody, HttpServletRequest request) {
        Optional<Users> existingUser = this.userRepository.findUserByEmail(registerBody.getEmail());

        if (existingUser.isPresent()) throw new ExistingEmailException("User with this email already exist");

        Users user = map2Entity(registerBody);
        this.userRepository.save(user);

        this.publisher.publishEvent(new RegistrationCompleteEvent(user, getApplicationUrl(request)));

        return AuthenticationResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullname(user.getFullname())
                .role(user.getRole())
                .isVerified(user.getIsVerified())
                .accessToken(this.jwtService.generateToken(user))
                .build();
    }

    public AuthenticationResponse login(LoginDto loginBody) {
        this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginBody.getEmail(), loginBody.getPassword())
        );

        var user = this.userRepository.findUserByEmail(loginBody.getEmail()).orElseThrow();

        var jwtToken = this.jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullname(user.getFullname())
                .role(user.getRole())
                .isVerified(user.getIsVerified())
                .accessToken(jwtToken)
                .build();
    }

    public void saveToken(Users user, String token, String tokenType) {
        Tokens newToken = new Tokens(user, token, tokenType);

        this.tokenRepository.save(newToken);
    }

    public String verifyToken(String token, Principal principal) {
        Users user = userRepository.findUserByEmail(principal.getName()).orElseThrow(() ->
                new ResourceNotFoundException("User Not Found"));

        Tokens verificationToken = this.tokenRepository.findByTokenAndTokenType(token, "VERIFICATION");

        if(verificationToken == null) throw new BadRequestException("Invalid token");

        Users userFromToken = verificationToken.getUser();

        if(!Tokens.isValidToken(verificationToken.getExpirationTime())) throw new BadRequestException("Expired token");

        if (!user.getId().equals(userFromToken.getId())) throw new BadRequestException("Unmatched User. Be logged in to your account for successful verification");

        user.setIsVerified(true);
        this.userRepository.save(user);
        this.tokenRepository.delete(verificationToken);

        return "Account verified successfully";
    }

    public String resendVerificationToken(Principal principal, HttpServletRequest request) {
        Users user = userRepository.findUserByEmail(principal.getName()).orElseThrow(() ->
                new ResourceNotFoundException("User Not Found"));

        Tokens verificationToken = this.tokenRepository.findByUserIdAndTokenType(user.getId(), "VERIFICATION");

        String token = this.generateNewToken(verificationToken.getToken(), "VERIFICATION");

        EmailPayloadDto emailPayloadDto = new EmailPayloadDto(
                user.getEmail(),
                "Account Email Verification",
                "Account Email Verification",
                "You've received this message because your email address has been registered with our site. Please click the button below to verify your email address and confirm that you are the owner of this account. <br> If you did not register with us, please disregard this email.",
                user.getFullname(),
                "email-verification",
                token);

        this.emailService.sendEmail(emailPayloadDto);
        String url = this.getApplicationUrl(request) + "/auth/verify?token=" + token;

        log.info("Click the url to verify your account: " + url);
        return "Token resend successfully";
    }

    private String generateNewToken(String token, String tokenType) {
        Tokens findToken = this.tokenRepository.findByTokenAndTokenType(token, tokenType);
        if(findToken == null) return "Invalid token";

        findToken.updateToken(UUID.randomUUID().toString(), tokenType);
        Tokens updatedToken = tokenRepository.save(findToken);
        return updatedToken.getToken();
    }

}
