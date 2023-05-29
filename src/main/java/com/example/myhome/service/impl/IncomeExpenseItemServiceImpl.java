package com.example.myhome.service.impl;
import com.example.myhome.exception.NotFoundException;
import com.example.myhome.model.IncomeExpenseItems;
import com.example.myhome.model.IncomeExpenseType;
import com.example.myhome.repository.IncomeExpenseRepository;
import com.example.myhome.service.IncomeExpenseItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
@RequiredArgsConstructor
@Log
public class IncomeExpenseItemServiceImpl implements IncomeExpenseItemService {

    private final IncomeExpenseRepository incomeExpenseRepository;

    public IncomeExpenseItems findById (Long id) {
        log.info("Searching for income/expense item with ID: " + id);
        try {
            IncomeExpenseItems item = incomeExpenseRepository.findById(id).orElseThrow(NotFoundException::new);
            log.info("Item found! " + item);
            return item;
        } catch (NotFoundException e) {
            log.severe("Item not found!");
            log.severe(e.getMessage());
            return null;
        }
    }
    public List<IncomeExpenseItems> findAll() {
        log.info("Getting all income/expense items!");
        List<IncomeExpenseItems> list = incomeExpenseRepository.findAll();
        log.info("Found " + list.size() + " elements");
        return list;
    }
    public IncomeExpenseItems save(IncomeExpenseItems incomeExpenseItems) {
        log.info("Trying to save item...");
        try {
            IncomeExpenseItems savedItem = incomeExpenseRepository.save(incomeExpenseItems);
            log.info("Item saved! " + savedItem);
            return savedItem;
        } catch (Exception e) {
            log.severe("Something went wrong during saving");
            log.severe(e.getMessage());
            return null;
        }
    }
    public void deleteById(Long id) {
        log.info("Trying to delete item with ID: " + id);
        try {
            incomeExpenseRepository.deleteById(id);
            log.info("Item successfully deleted");
        } catch (Exception e) {
            log.severe("Something went wrong during deletion");
            log.severe(e.getMessage());
        }
    }

    @Override
    public List<IncomeExpenseItems> findAllIncomeItems() {
        return incomeExpenseRepository.findAllByIncomeExpenseType(IncomeExpenseType.INCOME);
    }

    @Override
    public List<IncomeExpenseItems> findAllExpenseItems() {
        return incomeExpenseRepository.findAllByIncomeExpenseType(IncomeExpenseType.EXPENSE);
    }


}
