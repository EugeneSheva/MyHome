package com.example.myhome.mapper;

import com.example.myhome.dto.CashBoxDTO;
import com.example.myhome.model.CashBox;
import org.springframework.stereotype.Component;

@Component
public class CashboxDTOMapper {

    public CashBox fromDTOToCashBox(CashBoxDTO dto) {
        if(dto == null) return null;

        CashBox cb = new CashBox();
        cb.setId(dto.getId());

        return cb;
    }

    public CashBoxDTO fromCashboxToDTO(CashBox cb) {
        if(cb == null) return null;

        CashBoxDTO dto = new CashBoxDTO();
        dto.setId(cb.getId());
        dto.setDate(cb.getDate());
        dto.setCompleted(cb.getCompleted());
        dto.setIncomeExpenseItems((cb.getIncomeExpenseItems() != null) ? cb.getIncomeExpenseItems().getName() : null);
        dto.setOwnerFullName((cb.getOwner() != null) ? cb.getOwner().getFullName() : null);
        dto.setApartmentAccount((cb.getApartmentAccount() != null) ? cb.getApartmentAccount().getId() : null);
        dto.setIncomeExpenseType(cb.getIncomeExpenseType().getName());
        dto.setAmount(cb.getAmount());
        dto.setDescription(cb.getDescription());

        return dto;
    }

}
