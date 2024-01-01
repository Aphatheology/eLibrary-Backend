package com.aphatheology.elibrarybackend.event.listener;

import com.aphatheology.elibrarybackend.entity.Users;
import com.aphatheology.elibrarybackend.event.RegistrationCompleteEvent;
import com.aphatheology.elibrarybackend.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent> {

    private final AuthService authService;

    public RegistrationCompleteEventListener(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {

        Users user = event.getUser();
        String token = UUID.randomUUID().toString();

        this.authService.saveToken(user, token, "VERIFICATION");

        String url = event.getApplicationUrl() + "/auth/verify?token=" + token;

        log.info("Click the url to verify your account: " + url);
    }
}
