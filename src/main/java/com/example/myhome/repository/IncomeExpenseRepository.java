package com.example.myhome.repository;

import com.example.myhome.model.IncomeExpenseItems;
import com.example.myhome.model.IncomeExpenseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncomeExpenseRepository extends JpaRepository<IncomeExpenseItems, Long> {
    boolean existsByName(String name);
    boolean existsByIncomeExpenseType(String type);

    List<IncomeExpenseItems> findAllByOrderByIncomeExpenseTypeAsc();
    List<IncomeExpenseItems> findAllByOrderByIncomeExpenseTypeDesc();

    List<IncomeExpenseItems>findAllByIncomeExpenseType(IncomeExpenseType incomeExpenseType);
}
