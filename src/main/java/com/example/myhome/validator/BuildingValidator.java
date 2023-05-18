package com.example.myhome.validator;

import com.example.myhome.model.Building;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class BuildingValidator implements Validator {


        public boolean supports(Class clazz) {
            return Building.class.equals(clazz);
        }

    @Override
    public void validate(Object obj, Errors e) {
//        ValidationUtils.rejectIfEmpty(e, "name", "name.empty","Заполните поле");
        Building building = (Building) obj;
        if (building.getName() == null ||  building.getName().isEmpty()) {
            e.rejectValue("name", "name.empty", "Заполните поле");
        } else if  (building.getName().length()<5) {
            e.rejectValue("name", "name.empty", "Поле должно быть минимум 5 символов");
        }
        if (building.getAddress() == null ||  building.getAddress().isEmpty()) {
            e.rejectValue("address", "address.empty", "Заполните поле");
        } else if  (building.getAddress().length()<10) {
            e.rejectValue("address", "address.empty", "Поле должно быть минимум 10 символов");
        }

        if (building.getSections() == null ||  building.getSections().size()<1) {
            e.rejectValue("sections", "sections.empty", "Заполните поле");
        }
        if (building.getFloors() == null ||  building.getFloors().size()<1) {
            e.rejectValue("floors", "floors.empty", "Заполните поле");
        }
    }
}
