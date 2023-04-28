package com.example.myhome.home.validator;
import com.example.myhome.home.model.CashBox;
import com.example.myhome.home.model.IncomeExpenseType;
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
            e.rejectValue("ownerId", "ownerId.empty", "Заполните поле");
        }
        if ((cashBox.getIncomeExpenseType().equals(IncomeExpenseType.INCOME)) && cashBox.getApartmentAccount() == null) {
            e.rejectValue("apartmentAccount", "apartmentAccount.empty", "Заполните поле");
        }
        if (cashBox.getIncomeExpenseItems() == null) {
            e.rejectValue("incomeExpenseItems", "incomeExpenseItems.empty", "Заполните поле");
        }
        if (cashBox.getAmount() == null) {
            e.rejectValue("amount", "amount.empty", "Заполните поле");
        }
        if (cashBox.getManager() == null) {
            e.rejectValue("manager", "manager.empty", "Заполните поле");
        }
    }
}
