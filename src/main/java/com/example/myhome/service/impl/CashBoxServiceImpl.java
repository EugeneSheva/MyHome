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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    public List<CashBox> findAll() { return cashBoxRepository.findAll(); }
    @Override
    public Page<CashBox> findAll(Pageable pageable) { return cashBoxRepository.findAll(pageable); }
    @Override
    public CashBox save(CashBox cashBox) { return cashBoxRepository.save(cashBox); }

//    public Page<CashBoxDTO> findAllBySpecification(FilterForm filters, Integer page, Integer size) {
//        Pageable pageable = PageRequest.of(page-1, size);
//        Page<CashBox> initialPage = cashBoxRepository.findAll(buildSpecFromFilters(filters), pageable);
//
//        List<CashBoxDTO> listDTO = initialPage.getContent().stream()
//                .map(cashbox -> {
//                    String transactionItemName = (cashbox.getIncomeExpenseItems() != null) ? cashbox.getIncomeExpenseItems().getName() : "";
//                    String ownerFullName = (cashbox.getOwner() != null) ? cashbox.getOwner().getFullName() : "";
//                    String accountNumber = (cashbox.getApartmentAccount() != null) ? String.format("%010d", cashbox.getApartmentAccount().getId()) : "";
//                    String transactionType = (cashbox.getIncomeExpenseType() != null) ? cashbox.getIncomeExpenseType().getName() : "";
//                    return CashBoxDTO.builder()
//                            .id(cashbox.getId())
//                            .date(cashbox.getDate())
//                            .completed(cashbox.getCompleted())
//                            .transactionItemName(transactionItemName)
//                            .ownerFullName(ownerFullName)
//                            .accountNumber(accountNumber)
//                            .transactionType(transactionType)
//                            .amount(cashbox.getAmount())
//                            .build();
//                })
//                .collect(Collectors.toList());
//
//        return new PageImpl<>(listDTO, pageable, initialPage.getTotalElements());
//    }

//    private Specification<CashBox> buildSpecFromFilters(FilterForm filters) {
//        Long id = filters.getId();
//        String date = filters.getDate();
//        Boolean completed = filters.getCompleted();
//        Long transactionItemID = filters.getIncomeExpenseItem();
//        Long ownerID = filters.getOwner();
//        Long accountID = (filters.getAccount() != null) ? Long.parseLong(filters.getAccount()) : null;
//        IncomeExpenseType transactionType = (filters.getIncomeExpenseType() != null) ? IncomeExpenseType.valueOf(filters.getIncomeExpenseType()) : null;
//
//        LocalDate from = null, to = null;
//
//        if(date != null && !date.isEmpty()) {
//            String date_from = date.split(" - ")[0];
//            from =
//                    LocalDate.parse(date_from);
//            String date_to = date.split(" - ")[1];
//            to =
//                    LocalDate.parse(date_to);
//        }
//
//        return Specification.where(CashBoxSpecification.hasId(id)
//                .and(CashBoxSpecification.hasDatesBetween(from, to))
//                .and(CashBoxSpecification.isCompletedContains(completed))
//                .and(CashBoxSpecification.hasTransactionItemID(transactionItemID))
//                .and(CashBoxSpecification.hasOwnerID(ownerID))
//                .and(CashBoxSpecification.hasAccountID(accountID))
//                .and(CashBoxSpecification.hasTransactionType(transactionType)));
//
//    }
@Override
public Page<CashBoxDTO> findAllBySpecification2(FilterForm filters, Integer page, Integer size) {
    Pageable pageable = PageRequest.of(page-1, size);
    List<CashBoxDTO> listDTO = new ArrayList<>();
    System.out.println("filters" +filters);
    System.out.println("date" +filters.getDate());
    LocalDate startDate = null;
    LocalDate endDate = null;
    if (filters.getDate() != null) {
        String[] dateArray = filters.getDate().split(" - ");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        startDate = LocalDate.parse(dateArray[0], formatter);
        endDate = LocalDate.parse(dateArray[1], formatter);
    }
    Page<CashBox> cashBoxList = cashBoxRepository.findByFilters(filters.getId(),startDate, endDate, getIsCompleteFromString(filters.getIsCompleted()), filters.getIncomeExpenseItem(), filters.getOwner(), filters.getAccountId(), getIncomeExpenseTypeFromString(filters.getIncomeExpenseType()), pageable);

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
            String tmp = begin.getMonth().name() + " " + begin.getYear();
            doubleList.add(tmp);
            begin = begin.plusMonths(1);
        }
        return doubleList;
    }

    @Override
    public IncomeExpenseType getIncomeExpenseTypeFromString(String incomeExpenseTypeString) {
        IncomeExpenseType incomeExpenseType = null;
        if (incomeExpenseTypeString.equalsIgnoreCase("income")) {
            incomeExpenseType = IncomeExpenseType.INCOME;
        } else if (incomeExpenseTypeString.equalsIgnoreCase("expense")) {
            incomeExpenseType = IncomeExpenseType.EXPENSE;
        }
        return incomeExpenseType;
    }

    @Override
    public Boolean getIsCompleteFromString(String isCopmlete) {
        Boolean isCom = null;
        if (isCopmlete.equalsIgnoreCase("сompleted")) {
            isCom = true;
        } else if (isCopmlete.equalsIgnoreCase("notComplete")) {
            isCom = false;
        }
        return isCom;
    }
}
