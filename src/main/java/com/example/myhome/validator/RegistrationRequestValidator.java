package com.example.myhome.validator;

import com.example.myhome.service.registration.RegistrationRequest;
import com.example.myhome.repository.OwnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class RegistrationRequestValidator implements Validator {

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    @Autowired private OwnerRepository ownerRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return RegistrationRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors e) {
        RegistrationRequest request = (RegistrationRequest) target;
        if(request.getFirst_name().isEmpty()) {
            e.rejectValue("first_name", "first_name.empty", "Нужно указать имя!");
        }
        if(request.getLast_name().isEmpty()) {
            e.rejectValue("last_name", "last_name.empty", "Нужно указать фамилию!");
        }
        if(request.getFathers_name().isEmpty()) {
            e.rejectValue("fathers_name", "fathers_name.empty", "Нужно указать отчество!");
        }
        if(request.getEmail().isEmpty()) {
            e.rejectValue("email", "email.empty", "Нужно указать электронную почту!");
        } else {
            Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(request.getEmail());
            if(!matcher.matches()) e.rejectValue("email", "email.incorrect_format", "Неправильный формат электронной почты!");
            else if(ownerRepository.existsByEmail(request.getEmail())) e.rejectValue("email", "email.exists", "Пользователь с такой почтой уже существует!");
        }
        if(request.getPassword().isEmpty()) {
            e.rejectValue("password", "password.empty", "Нужно указать пароль!");
        } else if(request.getPassword().length() < 8) {
            e.rejectValue("password", "password.short", "Пароль должен быть длиной минимум 8 символов!");
        } else if(request.getConfirm_password().isEmpty()) {
            e.rejectValue("confirm_password", "confirm_password.empty", "Нужно подтвердить пароль!");
        } else if(!request.getConfirm_password().equalsIgnoreCase(request.getPassword())) {
            e.rejectValue("confirm_password", "confirm_password.incorrect", "Пароли должны совпадать!");
        }

        if(!request.getAgreeToTOS()) {
            e.rejectValue("agreeToTOS", "agreement.necessary", "Подтвердите свое согласие");
        }

    }
}
