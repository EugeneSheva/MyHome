package com.example.myhome.validator;

import com.example.myhome.model.Service;
import com.example.myhome.model.Tariff;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Locale;

@Component
public class TariffValidator implements Validator {

    @Autowired private MessageSource messageSource;

    @Override
    public boolean supports(Class<?> clazz) {
        return Tariff.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors e) {
        Tariff tariff = (Tariff) target;
        System.out.println(tariff);
        Locale locale = LocaleContextHolder.getLocale();
        if(tariff.getName() == null || tariff.getName().isEmpty())
            e.rejectValue("name", "name.empty", messageSource.getMessage("settings.system.tariffs.name.empty", null, locale));
        else if(tariff.getName().length() < 2 || tariff.getName().length() > 100) {
            e.rejectValue("name", "name.wrong-length", "Имя должно быть размером 2-100 символов");
        }
        if(tariff.getDescription() == null || tariff.getDescription().isEmpty())
            e.rejectValue("description", "description.empty", messageSource.getMessage("settings.system.tariffs.description.empty", null, locale));
        else if(tariff.getDescription().length() < 2 || tariff.getDescription().length() > 255) {
            e.rejectValue("description", "description.wrong-length", "Описание должно быть размером 2-255 символов");
        }

        if(tariff.getComponents() == null || tariff.getComponents().size() == 0) {
            e.rejectValue("components", "components.empty", messageSource.getMessage("settings.system.tariffs.components.empty", null, locale));
        }
        tariff.getComponents().values().forEach(price -> {
            if(price > 10000.0)
                e.rejectValue("components", "components.empty", "Цена услуги в тарифе не может быть больше 10000.0");
            else if(price <= 0.0)
                e.rejectValue("components", "components.empty", "Цена услуги в тарифе должна быть больше 0.0");
        });
    }
}
