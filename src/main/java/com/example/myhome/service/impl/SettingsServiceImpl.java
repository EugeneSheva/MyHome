package com.example.myhome.service.impl;

import com.example.myhome.exception.NotFoundException;
import com.example.myhome.model.IncomeExpenseItems;
import com.example.myhome.model.PageRoleDisplay;
import com.example.myhome.model.PaymentDetails;
import com.example.myhome.repository.IncomeExpenseRepository;
import com.example.myhome.repository.PageRoleDisplayRepository;
import com.example.myhome.repository.PaymentDetailsRepository;
import com.example.myhome.service.SettingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log
public class SettingsServiceImpl implements SettingsService {

    private final IncomeExpenseRepository incomeExpenseRepository;
    private final PaymentDetailsRepository paymentDetailsRepository;
    private final PageRoleDisplayRepository pageRoleDisplayRepository;

    @Override
    public PaymentDetails getPaymentDetails() {
        return paymentDetailsRepository.findById(1L).orElseGet(PaymentDetails::new);
    }

    @Override
    public PaymentDetails savePaymentDetails(PaymentDetails paymentDetails) {
        return paymentDetailsRepository.save(paymentDetails);
    }

    @Override
    public List<IncomeExpenseItems> getAllTransactionItems() {
        return incomeExpenseRepository.findAll();
    }

    @Override
    public List<IncomeExpenseItems> getAllTransactionItems(Sort sort) {
        return incomeExpenseRepository.findAll(sort);
    }

    @Override
    public IncomeExpenseItems saveTransactionItem(IncomeExpenseItems transaction) {
        if(!incomeExpenseRepository.existsByName(transaction.getName())) return null;
        else return incomeExpenseRepository.save(new IncomeExpenseItems(transaction.getName(), transaction.getIncomeExpenseType()));
    }

    @Override
    public IncomeExpenseItems getTransactionItem(Long transaction_id) {
        return incomeExpenseRepository.findById(transaction_id).orElseThrow(NotFoundException::new);
    }

    @Override
    public void deleteTransactionItem(Long transaction_id) {
        incomeExpenseRepository.deleteById(transaction_id);
    }

    @Override
    public List<PageRoleDisplay> getAllPagePermissions() {
        return pageRoleDisplayRepository.findAll();
    }

    @Override
    public List<PageRoleDisplay> savePagePermissions(List<PageRoleDisplay> pages) {
        return pageRoleDisplayRepository.saveAll(pages);
    }
}
