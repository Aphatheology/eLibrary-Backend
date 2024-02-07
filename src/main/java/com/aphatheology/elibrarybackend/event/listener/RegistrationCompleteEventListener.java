package com.aphatheology.elibrarybackend.event.listener;

import com.aphatheology.elibrarybackend.dto.EmailPayloadDto;
import com.aphatheology.elibrarybackend.entity.Users;
import com.aphatheology.elibrarybackend.event.RegistrationCompleteEvent;
import com.aphatheology.elibrarybackend.service.AuthService;
import com.aphatheology.elibrarybackend.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class RegistrationCompleteEventListener {

    private final AuthService authService;
    private final EmailService emailService;

    @Async
    @EventListener
    public void handleRegistrationCompleteEvent(RegistrationCompleteEvent event) {

        Users user = event.getUser();
        String token = UUID.randomUUID().toString();

        this.authService.saveToken(user, token, "VERIFICATION");
        log.info(token);

        EmailPayloadDto emailPayloadDto = new EmailPayloadDto(
                user.getEmail(),
                "Account Email Verification",
                "Account Email Verification",
                "You've received this message because your email address has been registered with our site. Please click the button below to verify your email address and confirm that you are the owner of this account. <br> If you did not register with us, please disregard this email.",
                user.getFullname(),
                "email-verification",
                token);

        this.emailService.sendEmail(emailPayloadDto);

        String url = event.getApplicationUrl() + "/auth/verify?token=" + token;

        log.info("Click the url to verify your account: " + url);
    }
}
