package com.example.myhome.model.filter;

import com.example.myhome.dto.OwnerDTO;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.lang.reflect.Field;
import java.time.LocalDate;

@Data
public class FilterForm {

    private Integer page;

    private Long building;
    private Long service;
    private Long apartment;
    private String section;

    private Long id;
    private String date;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate localDate;
    private String month;
    private String datetime;
    private String description;

    private Long master_type;
    private Long master;

    private Long owner;
    private OwnerDTO ownerEntity;
    private String ownerName;
    private String phone;

    private String status;

    private Boolean active;
    private Boolean debt;
    private Boolean completed;

    private String name;
    private String role;
    private String email;

    private String address;

    private Long number;
    private String floor;
    private String buildingName;
    private String debtSting;
    private String isCompleted;
    private String incomeExpenseItem;
    private String incomeExpenseType;

    private String account;
    private Long accountId;

    public FilterForm() {
    }

    public boolean filtersPresent() throws IllegalAccessException {
        for (Field field : this.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if(field.get(this) != null) {
                field.setAccessible(false);
                return true;
            }
            field.setAccessible(false);
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (Field field : this.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                if(field.get(this) != null) {
                    str.append(field.getName()).append(": ").append(field.get(this)).append(", ");
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            field.setAccessible(false);
        }
        return str.toString();
    }
}
