package com.aphatheology.elibrarybackend.event;

import com.aphatheology.elibrarybackend.entity.Users;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class RegistrationCompleteEvent extends ApplicationEvent {

    private final Users user;
    private final String applicationUrl;
    public RegistrationCompleteEvent(Users user, String applicationUrl) {
        super(user);
        this.user = user;
        this.applicationUrl = applicationUrl;
    }
}
