package com.example.myhome.validator;

import com.example.myhome.model.pages.AboutPage;
import com.example.myhome.model.pages.ServicesPage;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
@Log
public class ServicesPageValidator implements Validator {

    public static List<String> contentTypes = List.of("image/png", "image/jpeg", "image/jpg");

    @Override
    public boolean supports(Class<?> clazz) {
        return ServicesPage.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ServicesPage page = (ServicesPage) target;

    }
    public void validateServicesPage(Object target, Errors e, MultipartFile[] service_images) {
        ServicesPage page = (ServicesPage) target;

        for (int i = 0; i < service_images.length; i++) {
            MultipartFile photo = service_images[i];
            if(photo != null && photo.getSize() > 0 && !contentTypes.contains(photo.getContentType())) {
                e.rejectValue("serviceDescriptions["+i+"]", ".", "Неправильное расширение файла!");
                break;
            }
        }

        validate(page, e);
    }
}
