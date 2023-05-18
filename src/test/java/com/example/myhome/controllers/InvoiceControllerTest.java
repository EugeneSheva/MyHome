package com.example.myhome.controllers;

import com.example.myhome.config.TestConfig;
import com.example.myhome.controller.InvoiceController;
import com.example.myhome.controller.socket.WebsocketController;
import com.example.myhome.dto.InvoiceDTO;
import com.example.myhome.dto.OwnerDTO;
import com.example.myhome.model.Admin;
import com.example.myhome.model.InvoiceStatus;
import com.example.myhome.repository.InvoiceRepository;
import com.example.myhome.service.*;
import com.example.myhome.util.FileDownloadUtil;
import com.example.myhome.util.FileUploadUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TestConfig.class)
@AutoConfigureMockMvc
public class InvoiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Admin testUser;

    @MockBean private InvoiceService invoiceService;
    @MockBean private CashBoxService cashBoxService;
    @MockBean private BuildingService buildingService;
    @MockBean private ServiceService serviceService;
    @MockBean private TariffService tariffService;
    @MockBean private MeterDataService meterDataService;
    @MockBean private FileUploadUtil fileUploadUtil;
    @MockBean private FileDownloadUtil fileDownloadUtil;
    @MockBean private WebsocketController websocketController;
    @MockBean private OwnerService ownerService;

    @Autowired
    private InvoiceController controller;

    @Autowired
    private InvoiceRepository repository;

    InvoiceDTO dto;

    @BeforeEach
    void createInvoice(){
        dto = new InvoiceDTO();
        dto.setId(1L);
        dto.setCompleted(true);
        dto.setStatus(InvoiceStatus.PAID);
        dto.setComponents(List.of());

        when(invoiceService.findInvoiceDTOById(anyLong())).thenReturn(dto);
        when(ownerService.findAllDTO()).thenReturn(List.of(new OwnerDTO()));
        when(cashBoxService.calculateBalance()).thenReturn(0.0);
//        when(accountService.getSumOfAccountBalances()).thenReturn(0.0);
//        when(accountService.getSumOfAccountDebts()).thenReturn(0.0);
    }

    @Test
    void contextLoads() {
        assertThat(controller).isNotNull();
        assertThat(repository).isNotNull();
        assertThat(mockMvc).isNotNull();
        assertThat(invoiceService).isNotNull();

        assertThat(testUser).isNotNull();
    }

    @Test
    @WithUserDetails("test")
    void showInvoicesPageTest() throws Exception {
        this.mockMvc.perform(get("/admin/invoices").flashAttr("auth_admin", testUser)).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("test")
    void showInvoiceInfoPageTest() throws Exception {
        Long id = testUser.getId();
        Map<String, Object> map = new HashMap<>();
        map.put("auth_admin", testUser);
        map.put("invoice", dto);
        this.mockMvc.perform(get("/admin/invoices/" + id).flashAttrs(map)).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("test")
    void showInvoiceCreatePageTest() throws Exception {
        this.mockMvc.perform(get("/admin/invoices/create").flashAttr("auth_admin", testUser)).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("test")
    void showInvoiceUpdatePageTest() throws Exception {
        Long id = testUser.getId();
        this.mockMvc.perform(get("/admin/invoices/update/" + id).flashAttr("auth_admin", testUser)).andExpect(status().isOk());
    }
}
