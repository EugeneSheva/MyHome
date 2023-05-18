package com.example.myhome.validator;

import com.example.myhome.dto.RepairRequestDTO;
import com.example.myhome.model.RepairRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
public class RequestValidator implements Validator {

    @Autowired private MessageSource messageSource;

    @Override
    public boolean supports(Class<?> clazz) {
        return RepairRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors e) {
        RepairRequestDTO request = (RepairRequestDTO) target;

        Locale locale = LocaleContextHolder.getLocale();

        if(request.getOwnerID() == null) {
            e.rejectValue("ownerID", "ownerId.empty", messageSource.getMessage("requests.owner.id.empty", null, locale));
        } else if(request.getApartmentID() == null) {
            e.rejectValue("apartmentID", "apartmentId.empty", messageSource.getMessage("requests.apartment.id.empty", null, locale));
        }

        if(request.getDescription() == null || request.getDescription().equalsIgnoreCase("")) {
            e.rejectValue("description", "description.empty", messageSource.getMessage("requests.description.empty", null, locale));
        }

        if(request.getMasterID() == null || request.getMasterID() < 0) {
            e.rejectValue("masterID", "masterId.empty", messageSource.getMessage("requests.master.id.empty", null, locale));
        }

        if(request.getStatus() == null) {
            e.rejectValue("status", "status.empty", messageSource.getMessage("requests.status.empty", null, locale));
        }

        if(request.getBest_time() != null) {
            LocalDateTime best_time_request = LocalDateTime.parse(request.getBest_time(), DateTimeFormatter.ofPattern("yyyy-MM-dd - HH:mm"));
            if(best_time_request.isBefore(LocalDateTime.now())) e.rejectValue("best_time", "best_time.incorrect", messageSource.getMessage("requests.best_time.incorrect", null, locale));
        }
    }
}
