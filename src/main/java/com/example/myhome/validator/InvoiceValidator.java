package com.example.myhome.validator;

import com.example.myhome.dto.InvoiceDTO;
import com.example.myhome.model.Invoice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDate;
import java.util.Locale;

@Component
public class InvoiceValidator implements Validator {

    @Autowired private MessageSource messageSource;

    @Override
    public boolean supports(Class<?> clazz) {
        return Invoice.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors e) {
        InvoiceDTO invoice = (InvoiceDTO) target;
        LocalDate now = LocalDate.now();
        Locale locale = LocaleContextHolder.getLocale();
        if(invoice.getBuilding() == null || invoice.getBuilding().getId() == null) {
            e.rejectValue("building", "building.empty", messageSource.getMessage("invoices.building.empty", null, locale));
        } else if(invoice.getSection() == null || invoice.getSection().equalsIgnoreCase("0")) {
            e.rejectValue("section", "section.empty", messageSource.getMessage("invoices.section.empty", null, locale));
        } else if(invoice.getApartment() == null || invoice.getApartment().getId() == 0) {
            e.rejectValue("apartment", "apartment.empty", messageSource.getMessage("invoices.apartment.empty", null, locale));
        }
        if(invoice.getStatus() == null) {
            e.rejectValue("status", "status.empty", messageSource.getMessage("invoices.status.empty", null, locale));
        }
        if(invoice.getDateFrom() == null || invoice.getDateTo() == null) {
            e.rejectValue("dateFrom", "period.empty", messageSource.getMessage("invoices.period.empty", null, locale));
        } else if(invoice.getDateFrom().isAfter(now)) {
            e.rejectValue("dateFrom", "date_from.incorrect", messageSource.getMessage("invoices.date_from.incorrect", null, locale));
        } else if(invoice.getDateFrom().isAfter(invoice.getDateTo())) {
            e.rejectValue("dateTo", "date_to.incorrect", messageSource.getMessage("invoices.date_to.incorrect", null, locale));
        }
        if(invoice.getComponents() == null || invoice.getComponents().size() == 0) {
            e.rejectValue("components", "components.empty", messageSource.getMessage("invoices.components.empty", null, locale));
        }
    }
}
