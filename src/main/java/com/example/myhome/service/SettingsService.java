package com.example.myhome.service;

import com.example.myhome.model.IncomeExpenseItems;
import com.example.myhome.model.PageRoleDisplay;
import com.example.myhome.model.PaymentDetails;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface SettingsService {

    PaymentDetails getPaymentDetails();
    PaymentDetails savePaymentDetails(PaymentDetails paymentDetails);

    List<IncomeExpenseItems> getAllTransactionItems();
    List<IncomeExpenseItems> getAllTransactionItems(Sort sort);

    IncomeExpenseItems saveTransactionItem(IncomeExpenseItems transaction);

    IncomeExpenseItems getTransactionItem(Long transaction_id);

    void deleteTransactionItem(Long transaction_id);

    List<PageRoleDisplay> getAllPagePermissions();
    List<PageRoleDisplay> savePagePermissions(List<PageRoleDisplay> list);
}
