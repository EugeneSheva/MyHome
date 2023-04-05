package com.example.myhome.home.service;

import com.example.myhome.home.exception.NotFoundException;
import com.example.myhome.home.model.CashBox;
import com.example.myhome.home.model.IncomeExpenseType;
import com.example.myhome.home.repository.CashBoxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CashBoxService {
    private final CashBoxRepository cashBoxRepository;


    public CashBox findById (Long id) { return cashBoxRepository.findById(id).orElseThrow(() -> new NotFoundException());}

    public List<CashBox> findAll() { return cashBoxRepository.findAll(); }

    public CashBox save(CashBox cashBox) { return cashBoxRepository.save(cashBox); }

    public void deleteById(Long id) { cashBoxRepository.deleteById(id); }

    public Double calculateBalance() {return cashBoxRepository.sumAmount();}

    public List<Double> getListSumIncomeByMonth() {
        List<Double>doubleList = new ArrayList<>();
        LocalDate now = LocalDate.now();
        LocalDate begin = now.minusMonths(11);
        for (int i = 0; i < 12; i++) {
           Double tmp = cashBoxRepository.getSumByMonth(begin.getMonthValue(), begin.getYear(), IncomeExpenseType.INCOME);
            if (tmp == null) tmp=0D;
            doubleList.add(tmp);
            begin = begin.plusMonths(1);
        }
        return doubleList;
    }

    public List<Double> getListSumExpenceByMonth() {
        List<Double>doubleList = new ArrayList<>();
        LocalDate now = LocalDate.now();
        LocalDate begin = now.minusMonths(11);
        for (int i = 0; i < 12; i++) {
            Double tmp = cashBoxRepository.getSumByMonth(begin.getMonthValue(), begin.getYear(), IncomeExpenseType.EXPENSE);
            if (tmp == null) tmp=0D;
            if (tmp <0) tmp= tmp*-1;
            doubleList.add(tmp);
            begin = begin.plusMonths(1);
        }
        return doubleList;
    }

    public List<String> getListOfMonthName() {
        List<String>doubleList = new ArrayList<>();
        LocalDate now = LocalDate.now();
        LocalDate begin = now.minusMonths(11);
        for (int i = 0; i < 12; i++) {
            String tmp = begin.getMonth().name() + " " + begin.getYear();
            doubleList.add(tmp);
            begin = begin.plusMonths(1);
        }
        return doubleList;
    }


}
