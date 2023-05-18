package com.example.myhome.service;

import com.example.myhome.model.IncomeExpenseItems;

import java.util.List;

public interface IncomeExpenseItemService {

    IncomeExpenseItems findById(Long id);
    List<IncomeExpenseItems> findAll();
    IncomeExpenseItems save(IncomeExpenseItems item);
    void deleteById(Long id);

}
