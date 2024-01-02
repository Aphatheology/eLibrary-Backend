package com.aphatheology.elibrarybackend.service;

import com.aphatheology.elibrarybackend.dto.AuthenticationResponse;
import com.aphatheology.elibrarybackend.dto.LoginDto;
import com.aphatheology.elibrarybackend.dto.UserDto;
import com.aphatheology.elibrarybackend.entity.Role;
import com.aphatheology.elibrarybackend.entity.Tokens;
import com.aphatheology.elibrarybackend.entity.Users;
import com.aphatheology.elibrarybackend.event.RegistrationCompleteEvent;
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

import java.util.HashMap;
import java.util.Objects;
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


    public UserDto map2Dto(Users user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setFullname(user.getFullname());
        userDto.setCreatedAt(user.getCreatedAt());
        userDto.setUpdatedAt(user.getUpdatedAt());
        return userDto;
    }

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

    public HashMap<String, String> register(UserDto registerBody, HttpServletRequest request) {
        Users user = map2Entity(registerBody);
        this.userRepository.save(user);

        this.publisher.publishEvent(new RegistrationCompleteEvent(user, getApplicationUrl(request)));

//        var jwtToken = this.jwtService.generateToken(user);
//        AuthenticationResponse authenticationResponse = AuthenticationResponse.builder().token(jwtToken).build();

        var finalResponse = new HashMap<String, String>();

        finalResponse.put("message", "Registration successful, verification email sent");
        finalResponse.put("token", this.jwtService.generateToken(user));
        return finalResponse;
    }

    public void saveToken(Users user, String token, String tokenType) {
        Tokens newToken = new Tokens(user, token, tokenType);

        this.tokenRepository.save(newToken);
    }

    public String verifyToken(String token) {
        Tokens verificationToken = this.tokenRepository.findByTokenAndTokenType(token, "VERIFICATION");

        if(verificationToken == null) return "Invalid token";

        Users user = verificationToken.getUser();

        if(!Tokens.isValidToken(verificationToken.getExpirationTime())) {
            return "Expired token";
        }

        user.setIsVerified(true);
        this.userRepository.save(user);
        this.tokenRepository.delete(verificationToken);

        return "Account verified successfully";

    }

    public String resendVerificationToken(String oldToken, HttpServletRequest request) {
        String token = this.generateNewToken(oldToken, "VERIFICATION");

        if(Objects.equals(token, "Invalid token")) return "Invalid Token";

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

    public AuthenticationResponse login(LoginDto loginBody) {
        this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginBody.getEmail(), loginBody.getPassword())
        );

        var user = this.userRepository.findUserByEmail(loginBody.getEmail()).orElseThrow();

        var jwtToken = this.jwtService.generateToken(user);

        return AuthenticationResponse.builder().token(jwtToken).build();
    }
}