package com.example.myhome.validator;

import com.example.myhome.model.Service;
import com.example.myhome.model.Tariff;
import com.example.myhome.repository.TariffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Component
public class TariffValidator implements Validator {

    @Autowired private MessageSource messageSource;
    @Autowired private TariffRepository tariffRepository;

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
        else if(tariffRepository.existsByName(tariff.getName()) && tariff.getId() == null) {
            e.rejectValue("name", "name.wrong-length", "Tariff with this name already exists");
        }
        if(tariff.getDescription() == null || tariff.getDescription().isEmpty())
            e.rejectValue("description", "description.empty", messageSource.getMessage("settings.system.tariffs.description.empty", null, locale));
        else if(tariff.getDescription().length() < 2 || tariff.getDescription().length() > 255) {
            e.rejectValue("description", "description.wrong-length", "Описание должно быть размером 2-255 символов");
        }

        if(tariff.getComponents() == null || tariff.getComponents().size() == 0) {
            e.rejectValue("components", "components.empty", messageSource.getMessage("settings.system.tariffs.components.empty", null, locale));
        }
//        tariff.getComponents().values().forEach(price -> {
//            if(price > 10000.0) {
//                e.rejectValue("components", "components.empty", "Цена услуги в тарифе не может быть больше 10000.0");
//            }
//            else if(price <= 0.0)
//                e.rejectValue("components", "components.empty", "Цена услуги в тарифе должна быть больше 0.0");
//        });

        List<Double> components = new ArrayList<>(tariff.getComponents().values());
        boolean val_failed;
        for(int i = 0; i < components.size(); i++) {
            val_failed = false;
            if(components.get(i) > 10000.0) {
                e.rejectValue("components", "components.empty", "Цена услуги в тарифе не может быть больше 10000.0");
                val_failed = true;
            }
            else if(components.get(i) <= 0.0) {
                e.rejectValue("components", "components.empty", "Цена услуги в тарифе должна быть больше 0.0");
                val_failed = true;
            }
            if(val_failed) break;
        }
    }
}
