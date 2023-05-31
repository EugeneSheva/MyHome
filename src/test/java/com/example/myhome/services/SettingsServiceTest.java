package com.example.myhome.services;

import com.example.myhome.model.IncomeExpenseItems;
import com.example.myhome.model.PageRoleDisplay;
import com.example.myhome.model.PaymentDetails;
import com.example.myhome.repository.IncomeExpenseRepository;
import com.example.myhome.repository.PageRoleDisplayRepository;
import com.example.myhome.repository.PaymentDetailsRepository;
import com.example.myhome.service.SettingsService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
public class SettingsServiceTest {

    @Autowired
    private SettingsService service;

    @MockBean private IncomeExpenseRepository incomeExpenseRepository;
    @MockBean private PaymentDetailsRepository paymentDetailsRepository;
    @MockBean private PageRoleDisplayRepository pageRoleDisplayRepository;

    static PaymentDetails paymentDetails;
    static IncomeExpenseItems incomeExpenseItems;
    static List<IncomeExpenseItems> incomeExpenseItemsList;
    static List<PageRoleDisplay> roleDisplayList;

    @BeforeAll
    static void setupObjects() {
        paymentDetails = new PaymentDetails();
        incomeExpenseItems = new IncomeExpenseItems();
        incomeExpenseItemsList = List.of(incomeExpenseItems, incomeExpenseItems);
        roleDisplayList = List.of(new PageRoleDisplay());
    }

    @BeforeEach
    void setupMocks() {
        when(paymentDetailsRepository.save(any(PaymentDetails.class))).thenReturn(paymentDetails);
        when(paymentDetailsRepository.findById(anyLong())).thenReturn(Optional.ofNullable(paymentDetails));
        when(incomeExpenseRepository.findAll()).thenReturn(incomeExpenseItemsList);
        when(incomeExpenseRepository.findAll(any(Sort.class))).thenReturn(incomeExpenseItemsList);
        when(incomeExpenseRepository.save(any(IncomeExpenseItems.class))).thenReturn(incomeExpenseItems);
        when(incomeExpenseRepository.findById(anyLong())).thenReturn(Optional.ofNullable(incomeExpenseItems));
        when(pageRoleDisplayRepository.findAll()).thenReturn(roleDisplayList);
        when(pageRoleDisplayRepository.saveAll(roleDisplayList)).thenReturn(roleDisplayList);
    }

    @Test
    void contextLoads() {

    }

    @Test
    void getPaymentDetailsTest() {
        assertThat(service.getPaymentDetails()).isEqualTo(paymentDetails);
    }

    @Test
    void savePaymentDetailsTest(){
        assertThat(service.savePaymentDetails(paymentDetails)).isEqualTo(paymentDetails);
    }

    @Test
    void getAllTransactionItemsTest() {
        assertThat(service.getAllTransactionItems()).isEqualTo(incomeExpenseItemsList);
    }

    @Test
    void getAllTransactionItemsSortTest() {
        assertThat(service.getAllTransactionItems(Sort.by(Sort.Direction.ASC))).isEqualTo(incomeExpenseItemsList);
    }

    @Test
    void saveTransactionItemTest() {
        assertThat(service.saveTransactionItem(incomeExpenseItems)).isEqualTo(incomeExpenseItems);
    }

    @Test
    void getTransactionItemTest() {
        assertThat(service.getTransactionItem(1L)).isEqualTo(incomeExpenseItems);
    }

    @Test
    void deleteTransactionItemTest() {
        service.deleteTransactionItem(1L);
    }

    @Test
    void getAllPagePermissionsTest() {
        assertThat(service.getAllPagePermissions()).isEqualTo(roleDisplayList);
    }

    @Test
    void savePagePermissionsTest() {
        assertThat(service.savePagePermissions(roleDisplayList)).isEqualTo(roleDisplayList);
    }
}
