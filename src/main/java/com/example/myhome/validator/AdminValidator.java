package com.example.myhome.validator;

import com.example.myhome.dto.AdminDTO;
import com.example.myhome.repository.AdminRepository;
import com.example.myhome.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class AdminValidator implements Validator {

    @Autowired private AdminService adminService;
    @Autowired private AdminRepository adminRepository;
    @Autowired private MessageSource messageSource;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("[A-z0-9_-]{3,}.@[a-z]{2,}.[a-z]{2,3}");
    private static final Pattern PHONE_PATTERN = Pattern.compile("0(50|66|99)[0-9]{7}");

    @Override
    public boolean supports(Class<?> clazz) {
        return this.getClass().equals(clazz);
    }

    @Override
    public void validate(Object target, Errors e) {
        AdminDTO dto = (AdminDTO) target;
        Locale locale = LocaleContextHolder.getLocale();
        if(dto.getFirst_name() == null || dto.getFirst_name().isEmpty()) {
            e.rejectValue("first_name", "first_name.empty", messageSource.getMessage("users.first_name.empty", null, locale));
        }
        if(dto.getLast_name() == null || dto.getLast_name().isEmpty()) {
            e.rejectValue("last_name", "last_name.empty", messageSource.getMessage("users.last_name.empty", null, locale));
        }
        if(dto.getPhone_number() == null || dto.getPhone_number().isEmpty()) {
            e.rejectValue("phone_number", "phone_number.empty", messageSource.getMessage("users.phone_number.empty", null, locale));
        } else {
            Matcher phone_matcher = PHONE_PATTERN.matcher(dto.getPhone_number());
            if(!phone_matcher.find()) e.rejectValue("phone_number", "phone_number.incorrect", messageSource.getMessage("users.phone_number.incorrect", null, locale));
        }
        if(dto.getEmail() == null || dto.getEmail().isEmpty()) {
            e.rejectValue("email", "email.empty", messageSource.getMessage("users.email.empty", null, locale));
        } else if(dto.getEmail() != null && !dto.getEmail().isEmpty()) {
            Matcher matcher = EMAIL_PATTERN.matcher(dto.getEmail());
            if(!matcher.matches()) e.rejectValue("email", "email.incorrect", messageSource.getMessage("users.email.incorrect", null, locale));
        } else if(adminRepository.existsByEmail(dto.getEmail())) {
            e.rejectValue("email", "email.taken", messageSource.getMessage("users.email.taken", null, locale));
        }
        if(dto.getPassword() == null || !dto.getPassword().isEmpty() && !dto.getPassword().equals(dto.getConfirm_password())) {
            e.rejectValue("password", "password.no-match", messageSource.getMessage("users.password.no-match", null, locale));
            e.rejectValue("confirm_password", "password.no-match", messageSource.getMessage("users.password.no-match", null, locale));
        }
    }

}
