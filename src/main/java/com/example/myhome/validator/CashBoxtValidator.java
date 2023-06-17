package com.example.myhome.validator;
import com.example.myhome.model.CashBox;
import com.example.myhome.model.IncomeExpenseType;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class CashBoxtValidator implements Validator {


    public boolean supports(Class<?> clazz) {
        return CashBox.class.equals(clazz);
    }

    @Override
    public void validate(Object obj, Errors e) {
        CashBox cashBox = (CashBox) obj;
        if ((cashBox.getIncomeExpenseType().equals(IncomeExpenseType.INCOME)) && cashBox.getOwner() == null) {
            e.rejectValue("owner", "owner.empty", "Заполните поле");
        }
        if ((cashBox.getIncomeExpenseType().equals(IncomeExpenseType.INCOME)) && cashBox.getApartmentAccount() == null) {
            e.rejectValue("apartmentAccount", "apartmentAccount.empty", "Заполните поле");
        }
        if (cashBox.getIncomeExpenseItems() == null) {
            e.rejectValue("incomeExpenseItems", "incomeExpenseItems.empty", "Заполните поле");
        }
        if (cashBox.getAmount() == null) {
            e.rejectValue("amount", "amount.empty", "Заполните поле");
        } else if (cashBox.getAmount() > 1000000.0) {
            e.rejectValue("amount", "amount.too-big", "Значение не может быть больше 1.000.000");
        } else if (cashBox.getAmount() < 0.0) {
            e.rejectValue("amount", "amount.too-small", "Поле не может хранить отрицательные значения");
        }
        if (cashBox.getManager() == null) {
            e.rejectValue("manager", "manager.empty", "Заполните поле");
        }
    }
}
