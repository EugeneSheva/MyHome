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
    public void validateServicesPage(Object target, Errors e) {
        ServicesPage page = (ServicesPage) target;

        List<ServicesPage.ServiceDescription> list = page.getServiceDescriptions();

        for (int i = 0; i < list.size(); i++) {
            ServicesPage.ServiceDescription desc = list.get(i);
            if(desc.getTitle() == null || desc.getTitle().isEmpty()) {
                e.rejectValue("serviceDescriptions[" + i + "].title", "service.title.empty", "Service title can't be empty");
            } else if(desc.getTitle().length() < 2 || desc.getTitle().length() > 200) {
                e.rejectValue("serviceDescriptions[" + i + "].title", "service.title.length", "Service title length: 2-200");
            }

            if(desc.getDescription() == null || desc.getDescription().isEmpty()) {
                e.rejectValue("serviceDescriptions[" + i + "].description", "service.description.empty", "Service description can't be empty");
            } else if(desc.getDescription().length() < 2 || desc.getDescription().length() > 500) {
                e.rejectValue("serviceDescriptions[" + i + "].description", "service.description.length", "Service description length: 2-500");
            }

            if(desc.getFile() != null && desc.getFile().getSize() > 0) {
                if(!contentTypes.contains(desc.getFile().getContentType())) {
                    e.rejectValue("serviceDescriptions["+i+"].file", "document.file", "Wrong file format (.jpg, .jpeg, .png)");
                } else if (desc.getFile().getSize() > 19_999_999) {
                    e.rejectValue("serviceDescriptions["+i+"].file", "document.file", "Max file size = 20 MB");
                }
            } else if(desc.getPhoto() == null || desc.getPhoto().isEmpty()) {
                e.rejectValue("serviceDescriptions["+i+"].file", "document.file", "Upload file to new description!");
            }

        }

        validate(page, e);
    }
}
