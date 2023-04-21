package com.example.client.event;

import com.example.client.entity.UserEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class RegistrationCompleteEvent  extends ApplicationEvent {


    private UserEntity user;
    private String applicationUrl;
    public RegistrationCompleteEvent(UserEntity user, String applicationUrl) {
        super(user);
        this.user = user;
        this.applicationUrl = applicationUrl;
    }
}
