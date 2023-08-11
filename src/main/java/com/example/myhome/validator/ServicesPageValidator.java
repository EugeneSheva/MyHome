package com.example.myhome.validator;

import com.example.myhome.model.pages.AboutPage;
import com.example.myhome.model.pages.ServicesPage;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
@Slf4j
public class ServicesPageValidator implements Validator {

    public static List<String> contentTypes = List.of("image/png", "image/jpeg", "image/jpg");

    @Override
    public boolean supports(Class<?> clazz) {
        return ServicesPage.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ServicesPage page = (ServicesPage) target;

        String seo_title = page.getSeo_title();
        if(seo_title == null || seo_title.isEmpty()) {
            errors.rejectValue("seo_title", "seo_title.empty", "Title can't be empty");
        } else if(seo_title.length() < 2 || seo_title.length() > 100) {
            errors.rejectValue("seo_title", "seo_title.length", "Title length: 2-200");
        }

        String seo_desc = page.getSeo_description();
        if(seo_desc == null || seo_desc.isEmpty()) {
            errors.rejectValue("seo_description", "seo_desc.empty", "Desc can't be empty");
        } else if(seo_desc.length() < 2 || seo_desc.length() > 250) {
            errors.rejectValue("seo_description", "seo_desc.length", "Desc length: 2-250");
        }

        String seo_keywords = page.getSeo_keywords();
        if(seo_keywords == null || seo_keywords.isEmpty()) {
            errors.rejectValue("seo_keywords", "seo_keywords.empty", "Keywords can't be empty");
        } else if(seo_keywords.length() < 2 || seo_keywords.length() > 200) {
            errors.rejectValue("seo_keywords", "seo_keywords.length", "Keywords length: 2-200");
        }

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
