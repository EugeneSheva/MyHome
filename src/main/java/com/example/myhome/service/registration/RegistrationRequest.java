package com.example.myhome.service.registration;

import lombok.Data;

@Data
public class RegistrationRequest {

    private String first_name;
    private String last_name;
    private String fathers_name;
    private String email;
    private String password;
    private String confirm_password;

    private Boolean agreeToTOS;

}
