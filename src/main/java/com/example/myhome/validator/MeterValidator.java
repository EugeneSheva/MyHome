package com.example.myhome.validator;

import com.example.myhome.dto.MeterDataDTO;
import com.example.myhome.model.MeterData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDate;
import java.util.Locale;

@Component
public class MeterValidator implements Validator {

    @Autowired private MessageSource messageSource;

    @Override
    public boolean supports(Class<?> clazz) {
        return MeterData.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors e) {
        MeterDataDTO meter = (MeterDataDTO) target;

        Locale locale = LocaleContextHolder.getLocale();

        if(meter.getDate() == null || meter.getDate().isAfter(LocalDate.now())) {
            e.rejectValue("date", "date.incorrect", messageSource.getMessage("meters.date.incorrect", null, locale));
        }

        if(meter.getBuildingID() == null) {
            System.out.println("Error found(building)");
            e.rejectValue("buildingID", "building.empty", messageSource.getMessage("meters.building.empty", null, locale));
        } else if(meter.getSection() == null || meter.getSection().equals("")) {
            System.out.println("Error found(section)");
            e.rejectValue("section", "section.empty", messageSource.getMessage("meters.section.empty", null, locale));
        } else if(meter.getApartmentID() == null) {
            System.out.println("Error found(apartment)");
            e.rejectValue("apartmentID", "apartment.empty", messageSource.getMessage("meters.apartment.empty", null, locale));
        }

        if(meter.getServiceID() == null) {
            System.out.println("Error found(service)");
            e.rejectValue("serviceID", "service.empty", messageSource.getMessage("meters.service.empty", null, locale));
        }
        if(meter.getStatus() == null) {
            System.out.println("Error found(status)");
            e.rejectValue("status", "status.empty", messageSource.getMessage("meters.status.empty", null, locale));
        }
        if(meter.getReadings() == null) {
            System.out.println("Error found(readings)");
            e.rejectValue("readings", "currentReadings.empty", messageSource.getMessage("meters.currentReadings.empty", null, locale));
        }
        else if(meter.getReadings() < 0) {
            e.rejectValue("readings", "currentReadings.negative", messageSource.getMessage("meters.currentReadings.negative", null, locale));
        }
    }
}
