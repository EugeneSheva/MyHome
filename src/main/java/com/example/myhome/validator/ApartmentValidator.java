package com.example.myhome.validator;

import com.example.myhome.dto.ApartmentDTO;
import com.example.myhome.mapper.AccountDTOMapper;
import com.example.myhome.model.Apartment;
import com.example.myhome.service.AccountService;
import com.example.myhome.service.ApartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ApartmentValidator implements Validator {

    private final AccountService accountService;
    private final ApartmentService apartmentService;
    private final AccountDTOMapper accountDTOMapper;

    public boolean supports(Class clazz) {
            return ApartmentDTO.class.equals(clazz);
        }

    @Override
    public void validate(Object obj, Errors e) {
        ApartmentDTO apartment = (ApartmentDTO) obj;
        if (apartment.getNumber() == null) {
            e.rejectValue("number", "number.empty", "Заполните поле");
        } else if (apartment.getNumber() > 100000) {
            e.rejectValue("number", "number.empty", "Знаение слишком велико");
        }
        if (apartment.getSquare() == null) {
            e.rejectValue("square", "square.empty", "Заполните поле");
        } else if (apartment.getSquare() > 10000) {
            e.rejectValue("square", "square.empty", "Знаение слишком велико");
        }

        if (apartment.getBuilding() == null || apartment.getBuilding().getId() == null) {
            e.rejectValue("building", "building.empty", "Заполните поле");
        }
        if (apartment.getFloor() == null || apartment.getFloor().isEmpty()) {
            e.rejectValue("floor", "floor.empty", "Заполните поле");
        }
        if (apartment.getSection() == null || apartment.getSection().isEmpty()) {
            e.rejectValue("section", "section.empty", "Заполните поле");
        }
        if (apartment.getOwner() == null || apartment.getOwner().getId() == null) {
            e.rejectValue("owner", "owner.empty", "Заполните поле");
        }
//        if(apartment.getAccount() == null || apartment.getAccount().getId() == null) {
//            e.rejectValue("account", "account.empty", "Выберите лицевой счет из списка или создайте новый");
//        }
        if(apartment.getAccount() != null && apartment.getAccount().getId() != null) {
            if(apartmentService.accountHasApartment(apartment.getAccount().getId()) &&
            !Objects.equals(accountService.findAccountById(apartment.getAccount().getId()).getApartment().getId(),apartment.getId()))
                e.rejectValue("account", "account.taken", "К этому лицевому счёту уже привязана квартира!");
        }
    }
}
