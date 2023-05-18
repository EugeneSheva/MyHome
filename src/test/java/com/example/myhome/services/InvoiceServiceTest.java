package com.example.myhome.services;

import com.example.myhome.model.Invoice;
import com.example.myhome.repository.InvoiceRepository;
import com.example.myhome.service.InvoiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class InvoiceServiceTest {

    @MockBean private InvoiceRepository invoiceRepository;
    @Autowired private InvoiceService invoiceService;

    Invoice invoice;

    @BeforeEach
    void createInvoice() {
        invoice = new Invoice();
    }

    @Test
    void sanityCheck() {
        assertThat(invoiceRepository).isNotNull();
        assertThat(invoiceService).isNotNull();
        assertThat(invoice).isNotNull();
    }
}
