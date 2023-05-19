package com.example.myhome.controllers;

import com.example.myhome.config.TestConfig;
import com.example.myhome.controller.InvoiceController;
import com.example.myhome.controller.socket.WebsocketController;
import com.example.myhome.dto.ApartmentDTO;
import com.example.myhome.dto.BuildingDTO;
import com.example.myhome.dto.InvoiceDTO;
import com.example.myhome.dto.OwnerDTO;
import com.example.myhome.model.*;
import com.example.myhome.repository.InvoiceRepository;
import com.example.myhome.service.*;
import com.example.myhome.util.FileDownloadUtil;
import com.example.myhome.util.FileUploadUtil;
import com.example.myhome.validator.InvoiceValidator;
import lombok.With;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    @Autowired
    private InvoiceValidator validator;

    InvoiceDTO dto;
    InvoiceDTO emptyDTO;

    @BeforeEach
    void createInvoice() throws IOException {
        dto = new InvoiceDTO();
        dto.setId(1L);
        dto.setCompleted(true);
        dto.setStatus(InvoiceStatus.PAID);
        dto.setComponents(List.of(new InvoiceComponents()));
        dto.setDate(LocalDate.now());
        dto.setTotal_price(100);
        BuildingDTO bdto = new BuildingDTO();
        bdto.setId(1L);
        dto.setBuilding(bdto);
        dto.setSection("test");
        ApartmentDTO adto = new ApartmentDTO();
        adto.setId(1L);
        dto.setApartment(adto);
        dto.setDateFrom(LocalDate.now().minusMonths(3));
        dto.setDateTo(LocalDate.now().plusMonths(1));

        emptyDTO = new InvoiceDTO();
        emptyDTO.setId(10L);

        InvoiceTemplate testTemplate = new InvoiceTemplate();

        when(invoiceService.findInvoiceDTOById(anyLong())).thenReturn(dto);
        when(ownerService.findAllDTO()).thenReturn(List.of(new OwnerDTO()));
        when(cashBoxService.calculateBalance()).thenReturn(0.0);
        when(invoiceService.buildInvoice(any(), any(), any(String[].class), any(String[].class), any(String[].class)))
                .thenReturn(emptyDTO);
        when(invoiceService.buildInvoice(same(dto), any(), any(String[].class), any(String[].class), any(String[].class)))
                .thenReturn(dto);
        when(invoiceService.findTemplateById(anyLong())).thenReturn(testTemplate);
        when(invoiceService.turnInvoiceIntoExcel(any(Invoice.class), any(InvoiceTemplate.class))).thenReturn("test");
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

    @Test
    @WithUserDetails("test")
    void createInvoice_WithoutParameters_Test() throws Exception {
        this.mockMvc.perform(post("/admin/invoices/create").with(csrf()).flashAttr("auth_admin", testUser))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithUserDetails("test")
    void createInvoice_NotValidated_Test() throws Exception {
        this.mockMvc.perform(post("/admin/invoices/create")
                        .param("invoiceDTO", new InvoiceDTO().toString())
                        .param("date", LocalDate.now().toString())
                        .param("services", "brr")
                        .param("unit_prices", "brr")
                        .param("unit_amounts", "brr")
                        .with(csrf()).flashAttr("auth_admin", testUser))
                .andExpect(status().isOk())
                .andExpect(model().attribute("validation", equalTo("failed")));
    }

    @Test
    @WithUserDetails("test")
    void createInvoice_Validated_Test() throws Exception {
        Model model = new ConcurrentModel();
        String result = controller.createInvoice(dto, new BeanPropertyBindingResult(dto, "invoiceDTO"), LocalDate.now().toString(), new String[]{}, new String[]{}, new String[]{}, model);
        assertThat(model.containsAttribute("validation")).isFalse();
        assertThat(result).isEqualTo("redirect:/admin/invoices");
    }

    @Test
    @WithUserDetails("test")
    void updateInvoice_NotValidated_Test() throws Exception {
        this.mockMvc.perform(post("/admin/invoices/update/"+dto.getId())
                        .param("date", LocalDate.now().toString())
                        .param("services", "brr")
                        .param("unit_prices", "brr")
                        .param("unit_amounts", "brr")
                        .with(csrf()).flashAttr("auth_admin", testUser))
                .andExpect(status().isOk())
                .andExpect(model().attribute("validation", equalTo("failed")));
    }

    @Test
    @WithUserDetails("test")
    void updateInvoice_Validated_Test() throws Exception {
        Model model = new ConcurrentModel();
        String result = controller.updateInvoice(dto.getId(), dto, new BeanPropertyBindingResult(dto, "invoiceDTO"), LocalDate.now().toString(), new String[]{}, new String[]{}, new String[]{}, model);
        assertThat(model.containsAttribute("validation")).isFalse();
        assertThat(result).isEqualTo("redirect:/admin/invoices");
    }

    @Test
    @WithUserDetails("test")
    void deleteInvoiceTest() throws Exception {
        this.mockMvc.perform(get("/admin/invoices/delete/"+dto.getId()).with(csrf()).flashAttr("auth_admin", testUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/invoices"));
    }

    @Test
    @WithUserDetails("test")
    void deleteInvoiceAJAXTest() throws Exception {
        MvcResult result = this.mockMvc.perform(get("/admin/invoices/delete-invoice?id="+dto.getId()).with(csrf()).flashAttr("auth_admin", testUser))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(result.getResponse().getContentAsString()).isEqualTo("Удалил квитанцию с ID " + dto.getId());
    }

    @Test
    @WithUserDetails("test")
    void openPrintPageTest() throws Exception {
        this.mockMvc.perform(get("/admin/invoices/print/"+dto.getId()).with(csrf()).flashAttr("auth_admin", testUser))
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("test")
    void openPrintTemplatePageTest() throws Exception {
        this.mockMvc.perform(get("/admin/invoices/template").flashAttr("auth_admin", testUser))
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("test")
    void downloadFileTest() throws Exception {
        this.mockMvc.perform(get("/download/test").flashAttr("auth_admin", testUser))
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("test")
    void printInvoiceTest() throws Exception {
        this.mockMvc.perform(post("/print/"+dto.getId()).with(csrf()).flashAttr("auth_admin", testUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/invoices/download/test"));
    }

    @Test
    @WithUserDetails("test")
    void printInvoice_IOException_Test() throws Exception {
        when(invoiceService.turnInvoiceIntoExcel(any(), any())).thenThrow(IOException.class);

        this.mockMvc.perform(post("/print/"+dto.getId()).with(csrf()).flashAttr("auth_admin", testUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/invoices/print/"+dto.getId()));
    }

    @Test
    @WithUserDetails("test")
    void saveTemplateTest() throws Exception {
        this.mockMvc.perform(post("/admin/invoices/template").with(csrf()).flashAttr("auth_admin", testUser))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithUserDetails("test")
    void getInvoicesAJAX_WithoutParameters_Test() throws Exception {
        this.mockMvc.perform(get("/admin/invoices/get-invoices").with(csrf()).flashAttr("auth_admin", testUser))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithUserDetails("test")
    void getInvoicesAJAX_WithIncorrectParameters_Test() throws Exception {
        this.mockMvc.perform(get("/admin/invoices/get-invoices?page=1").with(csrf()).flashAttr("auth_admin", testUser))
                .andExpect(status().isBadRequest());
        this.mockMvc.perform(get("/admin/invoices/get-invoices?page=1&size=1").with(csrf()).flashAttr("auth_admin", testUser))
                .andExpect(status().isBadRequest());
        this.mockMvc.perform(get("/admin/invoices/get-invoices?size=1&filters=").with(csrf()).flashAttr("auth_admin", testUser))
                .andExpect(status().isBadRequest());
        this.mockMvc.perform(get("/admin/invoices/get-invoices?filters=").with(csrf()).flashAttr("auth_admin", testUser))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithUserDetails("test")
    void getInvoicesAJAX_WithCorrectParameters_Test() throws Exception {
    }



}
