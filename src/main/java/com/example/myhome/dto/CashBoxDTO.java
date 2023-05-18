package com.example.myhome.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CashBoxDTO {

    private Long id;
    private LocalDate date;
    private Boolean completed;
    private String incomeExpenseType;
    private String ownerFullName;
    private Long apartmentAccount;

    private String incomeExpenseItems;
    private String managerFullName;
    private Double amount;
    private String description;
}
