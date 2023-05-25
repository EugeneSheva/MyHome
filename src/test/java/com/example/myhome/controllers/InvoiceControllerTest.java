package com.example.myhome.controllers;

import com.example.myhome.config.TestConfig;
import com.example.myhome.controller.InvoiceController;
import com.example.myhome.controller.socket.WebsocketController;
import com.example.myhome.dto.ApartmentDTO;
import com.example.myhome.dto.BuildingDTO;
import com.example.myhome.dto.InvoiceDTO;
import com.example.myhome.dto.OwnerDTO;
import com.example.myhome.mapper.InvoiceDTOMapper;
import com.example.myhome.model.*;
import com.example.myhome.model.filter.FilterForm;
import com.example.myhome.repository.InvoiceRepository;
import com.example.myhome.service.*;
import com.example.myhome.util.FileDownloadUtil;
import com.example.myhome.util.FileUploadUtil;
import com.example.myhome.validator.InvoiceValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.With;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = TestConfig.class)
@AutoConfigureMockMvc
@WithUserDetails("test")
public class InvoiceControllerTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private Admin testUser;

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
    @MockBean private ApartmentService apartmentService;

    @Autowired private InvoiceController controller;
    @Autowired private InvoiceRepository repository;
    @Autowired private InvoiceValidator validator;

    static Invoice testInvoice = new Invoice();
    static InvoiceDTO dto = new InvoiceDTO();
    static InvoiceDTO emptyDTO = new InvoiceDTO();
    static InvoiceTemplate testTemplate = new InvoiceTemplate();
    static InvoiceDTOMapper mapper = new InvoiceDTOMapper();

    static ObjectMapper jsonMapper = new ObjectMapper();

    @BeforeAll
    static void setupObject() throws Exception {
        emptyDTO.setId(10L);

        testInvoice.setId(1L);
        testInvoice.setCompleted(true);
        testInvoice.setId(1L);
        testInvoice.setCompleted(true);
        testInvoice.setStatus(InvoiceStatus.PAID);
        InvoiceComponents testComp = new InvoiceComponents();
        Service testService = new Service();
        testService.setId(1L);
        testService.setUnit(new Unit());
        testService.setName("test");
        testComp.setService(testService);
        testInvoice.setComponents(List.of(testComp));
        testInvoice.setDate(LocalDate.now());
        testInvoice.setTotal_price(100);
        Building building = new Building();
        building.setId(1L);
        testInvoice.setBuilding(building);
        testInvoice.setSection("test");
        Owner owner = new Owner();
        owner.setId(1L);
        owner.setFirst_name("test");
        owner.setFathers_name("test");
        owner.setLast_name("test");
        owner.setPhone_number("test");
        Apartment apartment = new Apartment();
        apartment.setBuilding(building);
        apartment.setOwner(owner);
        apartment.setId(1L);
        testInvoice.setApartment(apartment);
        testInvoice.setDateFrom(LocalDate.now().minusMonths(3));
        testInvoice.setDateTo(LocalDate.now().plusMonths(1));

        dto = mapper.fromInvoiceToDTO(testInvoice);
    }

    @BeforeEach
    void setupMocks() throws IOException {
        when(invoiceService.findInvoiceDTOById(anyLong())).thenReturn(dto);
        when(invoiceService.findInvoiceById(anyLong())).thenReturn(testInvoice);
        when(ownerService.findAllDTO()).thenReturn(List.of(new OwnerDTO()));
        when(cashBoxService.calculateBalance()).thenReturn(0.0);
        when(invoiceService.buildInvoice(any(), any(), any(String[].class), any(String[].class), any(String[].class)))
                .thenReturn(emptyDTO);
        when(invoiceService.buildInvoice(same(dto), any(), any(String[].class), any(String[].class), any(String[].class)))
                .thenReturn(dto);
        when(invoiceService.findTemplateById(anyLong())).thenReturn(testTemplate);
        when(invoiceService.turnInvoiceIntoExcel(any(Invoice.class), any(InvoiceTemplate.class))).thenReturn("test");
        when(invoiceService.getFilteredInvoiceCount(any())).thenReturn(10L);
        when(apartmentService.findApartmentDto(anyLong())).thenReturn(new ApartmentDTO());
        when(apartmentService.findById(anyLong())).thenReturn(new Apartment());
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
    void showInvoicesPageTest() throws Exception {
        this.mockMvc.perform(get("/admin/invoices").flashAttr("auth_admin", testUser)).andExpect(status().isOk());
    }

    @Test
    void showInvoiceInfoPageTest() throws Exception {
        Long id = testUser.getId();
        Map<String, Object> map = new HashMap<>();
        map.put("auth_admin", testUser);
        map.put("invoice", dto);
        this.mockMvc.perform(get("/admin/invoices/" + id).flashAttrs(map)).andExpect(status().isOk());
    }

    @Test
    void showInvoiceCreatePageTest() throws Exception {
        this.mockMvc.perform(get("/admin/invoices/create").flashAttr("auth_admin", testUser)).andExpect(status().isOk());
    }

    @Test
    void showInvoiceCreatePage_WithFlatID_Test() throws Exception {
        this.mockMvc.perform(get("/admin/invoices/create")
                .param("flat_id", "1")
                .flashAttr("auth_admin", testUser)).andExpect(status().isOk());
    }

    @Test
    void showInvoiceUpdatePageTest() throws Exception {
        Long id = testUser.getId();
        this.mockMvc.perform(get("/admin/invoices/update/" + id).flashAttr("auth_admin", testUser)).andExpect(status().isOk());
    }

    @Test
    void createInvoice_WithoutParameters_Test() throws Exception {
        this.mockMvc.perform(post("/admin/invoices/create").with(csrf()).flashAttr("auth_admin", testUser))
                .andExpect(status().isBadRequest());
    }

    @Test
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
    void createInvoice_Validated_Test() throws Exception {
        Model model = new ConcurrentModel();
        String result = controller.createInvoice(dto, new BeanPropertyBindingResult(dto, "invoiceDTO"), LocalDate.now().toString(), new String[]{}, new String[]{}, new String[]{}, model);
        assertThat(model.containsAttribute("validation")).isFalse();
        assertThat(result).isEqualTo("redirect:/admin/invoices");
    }

    @Test
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
    void updateInvoice_Validated_Test() throws Exception {
        Model model = new ConcurrentModel();
        String result = controller.updateInvoice(dto.getId(), dto, new BeanPropertyBindingResult(dto, "invoiceDTO"), LocalDate.now().toString(), new String[]{}, new String[]{}, new String[]{}, model);
        assertThat(model.containsAttribute("validation")).isFalse();
        assertThat(result).isEqualTo("redirect:/admin/invoices");
    }

    @Test
    void deleteInvoiceTest() throws Exception {
        this.mockMvc.perform(get("/admin/invoices/delete/"+dto.getId()).with(csrf()).flashAttr("auth_admin", testUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/invoices"));
    }

    @Test
    void deleteInvoiceAJAXTest() throws Exception {
        MvcResult result = this.mockMvc.perform(get("/admin/invoices/delete-invoice?id="+dto.getId()).with(csrf()).flashAttr("auth_admin", testUser))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(result.getResponse().getContentAsString()).isNotNull();
    }

    @Test
    void openPrintPageTest() throws Exception {
        this.mockMvc.perform(get("/admin/invoices/print/"+dto.getId()).with(csrf()).flashAttr("auth_admin", testUser))
                .andExpect(status().isOk());
    }

    @Test
    void openPrintTemplatePageTest() throws Exception {
        this.mockMvc.perform(get("/admin/invoices/template").flashAttr("auth_admin", testUser))
                .andExpect(status().isOk());
    }

    @Test
    void openPrintTemplatePage_WithParams_Test() throws Exception {
        this.mockMvc.perform(get("/admin/invoices/template?default_id=1")
                        .flashAttr("auth_admin", testUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/invoices/template"));

        this.mockMvc.perform(get("/admin/invoices/template?delete_id=1")
                        .flashAttr("auth_admin", testUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/invoices/template"));
    }

    @Test
    void saveTemplateTest() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "file.xls", "multipart/form-data", new byte[1]);
        this.mockMvc.perform(multipart("/admin/invoices/template")
                        .file(file)
                .param("name","test")
                .with(csrf())
                .flashAttr("auth_admin", testUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/invoices/template"));
    }

    @Test
    void downloadFileTest() throws Exception {
        this.mockMvc.perform(get("/admin/invoces/download/test").flashAttr("auth_admin", testUser))
                .andExpect(status().isNotFound());
    }

    @Test
    void printInvoiceTest() throws Exception {
        this.mockMvc.perform(post("/admin/invoices/print/"+dto.getId())
                        .with(csrf())
                        .param("template", "1")
                        .flashAttr("auth_admin", testUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/invoices/download/test"));
    }

    @Test
    void printInvoice_IOException_Test() throws Exception {
        when(invoiceService.turnInvoiceIntoExcel(any(), any())).thenThrow(IOException.class);

        this.mockMvc.perform(post("/admin/invoices/print/"+dto.getId())
                        .with(csrf())
                        .param("template", "1")
                        .flashAttr("auth_admin", testUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/invoices/print/"+dto.getId()));
    }


    @Test
    void getInvoicesAJAX_WithoutParameters_Test() throws Exception {
        this.mockMvc.perform(get("/admin/invoices/get-invoices")
                        .with(csrf()).flashAttr("auth_admin", testUser))
                .andExpect(status().isBadRequest());
    }

    @Test
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
    void getInvoicesAJAX_WithCorrectParameters_Test() throws Exception {
        this.mockMvc.perform(get("/admin/invoices/get-invoices?page=1&size=1&filters=null")
                .with(csrf())
                .flashAttr("auth_admin",testUser))
                .andExpect(status().isOk());
    }

    @Test
    void getFilteredInvoiceCountTest() throws Exception {
        FilterForm filterForm = new FilterForm();
        filterForm.setId(1L);
        filterForm.setName("brr");
        this.mockMvc.perform(get("/admin/invoices/get-filtered-invoice-count")
                .with(csrf())
                .param("f_string", jsonMapper.writeValueAsString(filterForm))
                .flashAttr("auth_admin", testUser))
                .andExpect(status().isOk());
    }

    @Test
    void canSearchTest() throws Exception {
        this.mockMvc.perform(get("/admin/invoices/search")
                .with(csrf())
                .param("flat_id", "1")
                .flashAttr("auth_admin", testUser))
                .andExpect(status().isOk());
    }

    @Test
    void canDownloadFileTest() throws Exception {
        String jsonString = jsonMapper.writeValueAsString(10L);
        this.mockMvc.perform(get("/admin/invoices/download/test")
                .with(csrf())
                .flashAttr("auth_admin", testUser))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(10L)));
    }





}
