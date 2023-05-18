package com.example.myhome.service.registration;

import org.springframework.context.ApplicationEvent;
import org.springframework.security.core.userdetails.UserDetails;

public class OnRegistrationCompleteEvent extends ApplicationEvent {
    private String appUrl;
    private UserDetails userDetails;

    public OnRegistrationCompleteEvent(UserDetails userDetails, String appUrl) {
        super(userDetails);
        this.userDetails = userDetails;
        this.appUrl = appUrl;
    }

    public String getAppUrl() {
        return appUrl;
    }

    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }

    public UserDetails getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
    }
}


