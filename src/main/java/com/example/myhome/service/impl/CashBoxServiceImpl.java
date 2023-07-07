package com.example.myhome.service.impl;

import com.example.myhome.dto.CashBoxDTO;
import com.example.myhome.exception.NotFoundException;
import com.example.myhome.mapper.CashboxDTOMapper;
import com.example.myhome.model.filter.FilterForm;
import com.example.myhome.repository.CashBoxRepository;

import com.example.myhome.service.CashBoxService;
import com.example.myhome.model.CashBox;
import com.example.myhome.model.IncomeExpenseType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CashBoxServiceImpl implements CashBoxService {
    private final CashBoxRepository cashBoxRepository;
    private final CashboxDTOMapper mapper;

    @Override
    public CashBox findById(Long id) { return cashBoxRepository.findById(id).orElseThrow(NotFoundException::new);}
    @Override
    public CashBoxDTO findDTOById(Long id) {
        return mapper.fromCashboxToDTO(cashBoxRepository.findById(id).orElseThrow(NotFoundException::new));
    }
    @Override
    public List<CashBox> findAll() { return cashBoxRepository.findAll(); }
    @Override
    public Page<CashBox> findAll(Pageable pageable) { return cashBoxRepository.findAll(pageable); }
    @Override
    public CashBox save(CashBox cashBox) { return cashBoxRepository.save(cashBox); }

@Override
public Page<CashBoxDTO> findAllBySpecification2(FilterForm filters, Integer page, Integer size) throws IllegalAccessException {
    Pageable pageable = PageRequest.of(page-1, size);
    List<CashBoxDTO> listDTO = new ArrayList<>();

    Page<CashBox> cashBoxList;

    System.out.println("filters" +filters);
    System.out.println("date" + filters.getDate());
    LocalDate startDate = null;
    LocalDate endDate = null;
    if (filters.getDate() != null) {
        String[] dateArray = filters.getDate().split(" - ");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        startDate = LocalDate.parse(dateArray[0], formatter);
        endDate = LocalDate.parse(dateArray[1], formatter);
        System.out.println("startDate " + startDate);
        System.out.println("endDate " + endDate);
    }

    if(!filters.filtersPresent()) cashBoxList = cashBoxRepository.findAll(pageable);
    else cashBoxList = cashBoxRepository.findByFilters(filters.getId(),startDate, endDate, getIsCompleteFromString(filters.getIsCompleted()), filters.getIncomeExpenseItem(), filters.getOwner(), filters.getAccountId(), getIncomeExpenseTypeFromString(filters.getIncomeExpenseType()), pageable);

    cashBoxList.getContent().forEach(item -> listDTO.add(mapper.fromCashboxToDTO(item)));
    System.out.println(listDTO);

    return new PageImpl<>(listDTO, pageable, cashBoxList.getTotalElements());
}
    @Override
    public void deleteById(Long id) { cashBoxRepository.deleteById(id); }

    @Override
    public Long getMaxId() {return cashBoxRepository.getMaxId().orElse(0L);}

    @Override
    public Double calculateBalance() {return cashBoxRepository.sumAmount().orElse(0.0);}

    @Override
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

    @Override
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

    @Override
    public List<String> getListOfMonthName() {
        List<String>doubleList = new ArrayList<>();
        LocalDate now = LocalDate.now();
        LocalDate begin = now.minusMonths(11);
        for (int i = 0; i < 12; i++) {
            String tmp = begin.getMonth().getDisplayName(
                    TextStyle.FULL, LocaleContextHolder.getLocale()
            ) + " " + begin.getYear();
            doubleList.add(tmp);
            begin = begin.plusMonths(1);
        }
        return doubleList;
    }

    @Override
    public IncomeExpenseType getIncomeExpenseTypeFromString(String incomeExpenseTypeString) {
        if (incomeExpenseTypeString != null) {
            IncomeExpenseType incomeExpenseType = null;
            if (incomeExpenseTypeString.equalsIgnoreCase("income")) {
                incomeExpenseType = IncomeExpenseType.INCOME;
            } else if (incomeExpenseTypeString.equalsIgnoreCase("expense")) {
                incomeExpenseType = IncomeExpenseType.EXPENSE;
            }
            return incomeExpenseType;
        }
        return null;
    }

    public Boolean getIsCompleteFromString(String isCopmlete) {
        Boolean isCom = null;
        if (isCopmlete.equalsIgnoreCase("сompleted")) {
            isCom = true;
        } else if (isCopmlete.equalsIgnoreCase("notComplete")) {
            isCom = false;
        }
        return isCom;
    }

    @Override
    public Double getSumAmount() {
        return cashBoxRepository.sumAmount().orElse(0.0);
    }
}
