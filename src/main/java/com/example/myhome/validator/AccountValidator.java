package com.example.myhome.validator;

import com.example.myhome.dto.ApartmentAccountDTO;
import com.example.myhome.service.AccountService;
import com.example.myhome.service.ApartmentService;
import com.example.myhome.service.impl.AccountServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Locale;
import java.util.Objects;

@Component
public class AccountValidator implements Validator {

    @Autowired private AccountService accountService;
    @Autowired private ApartmentService apartmentService;
    @Autowired private MessageSource messageSource;

    @Override
    public boolean supports(Class<?> clazz) {
        return ApartmentAccountDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        ApartmentAccountDTO account = (ApartmentAccountDTO) target;
        System.out.println("VALIDATION ");
        System.out.println(account.toString());

        Locale locale = LocaleContextHolder.getLocale();

//        if(account.getBuilding() == null || account.getBuilding().getId() == null) {
//            System.out.println("Error found(building)");
//            errors.rejectValue("building", "building.empty", messageSource.getMessage("accounts.building.empty", null, locale));
//        } else if(account.getSection() == null || account.getSection().equalsIgnoreCase("0")) {
//            System.out.println("Error found(section)");
//            errors.rejectValue("section", "section.empty", messageSource.getMessage("accounts.section.empty", null, locale));
//        } else if(account.getApartment() == null || account.getApartment().getId() == 0L) {
//            System.out.println("Error found(apartment)");
//            errors.rejectValue("apartment", "apartment.empty", messageSource.getMessage("accounts.apartment.empty", null, locale));
//        } else if(accountService.apartmentHasAccount(account.getApartment().getId())) {
//            if(account.getId() == null || (!Objects.equals(accountService.findAccountById(account.getId()).getApartment().getId(),
//               account.getApartment().getId()) && (account.getChangedState() == null || !account.getChangedState())))
//                errors.rejectValue("apartment", "apartment.has_account", messageSource.getMessage("accounts.apartment.has_account", null, locale));
//        }

        if(account.getApartment() != null && account.getApartment().getId() != null && account.getApartment().getId() != 0) {
            if(accountService.apartmentHasAccount(account.getApartment().getId()) &&
                    !Objects.equals(apartmentService.findById(account.getApartment().getId()).getAccount().getId(), account.getId()))
                errors.rejectValue("apartment", "apartment.has_account", messageSource.getMessage("accounts.apartment.has_account", null, locale));
        }

        if(account.getIsActive() == null) {
            System.out.println("Error found(active)");
            errors.rejectValue("isActive", "isActive.empty", messageSource.getMessage("accounts.isActive.empty", null, locale));
        }
    }
}
