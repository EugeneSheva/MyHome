package com.example.myhome.validator;

import com.example.myhome.exception.NotFoundException;
import com.example.myhome.model.Owner;
import com.example.myhome.service.OwnerService;
import com.example.myhome.service.registration.LoginRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class LoginRequestValidator implements Validator {

    @Autowired private OwnerService ownerService;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public boolean supports(Class<?> clazz) {
        return LoginRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        LoginRequest request = (LoginRequest) target;
        Owner owner;
        try {
            owner = ownerService.findByLogin(request.getUsername());
            if(!passwordEncoder.matches(request.getPassword(), owner.getPassword())) {
                errors.rejectValue("password", "password.no_match", "Wrong password!");
            }
        } catch (NotFoundException e) {
            errors.rejectValue("username", "username.nonexistent", "User with this email doesn't exist");
        }

    }
}
