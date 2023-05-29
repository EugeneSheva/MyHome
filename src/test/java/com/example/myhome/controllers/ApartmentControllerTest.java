package com.example.myhome.controllers;

import com.example.myhome.controller.ApartmentController;
import com.example.myhome.dto.ApartmentDTO;
import com.example.myhome.dto.OwnerDTO;
import com.example.myhome.mapper.AccountDTOMapper;
import com.example.myhome.mapper.ApartmentDTOMapper;
import com.example.myhome.model.Admin;
import com.example.myhome.model.Apartment;
import com.example.myhome.model.MeterData;
import com.example.myhome.model.Owner;
import com.example.myhome.service.*;
import com.example.myhome.validator.ApartmentValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@WithUserDetails("test")
public class ApartmentControllerTest {

    @Autowired
    private Admin testUser;

    @MockBean private ApartmentService apartmentService;
    @MockBean private AccountService accountService;
    @MockBean private BuildingService buildingService;
    @MockBean private InvoiceService invoiceService;
    @MockBean private OwnerService ownerService;
    @MockBean private TariffService tariffService;
    @MockBean private CashBoxService cashBoxService;
    @MockBean private IncomeExpenseItemService incomeExpenseItemService;
    @MockBean private ApartmentValidator apartmentValidator;
    @MockBean private AdminService adminService;
    @MockBean private MeterDataService meterDataService;

    @MockBean private MessageSource messageSource;

    @Autowired private AccountDTOMapper accountDTOMapper;
    @Autowired private ApartmentDTOMapper apartmentDTOMapper;

    @Autowired private ApartmentController apartmentController;

    @Autowired
    private MockMvc mockMvc;

    static Apartment testApartment = new Apartment();
    static ApartmentDTO testDTO = new ApartmentDTO();
    static Page<ApartmentDTO> apartmentPage;
    static Page<OwnerDTO> ownerPage;
    static Owner testOwner;
    static List<MeterData> meterDataList;

    static ObjectMapper jsonMapper = new ObjectMapper();

    @BeforeAll
    void setupObjects() {
        // testApartment.setId(...);
        // ....

        // testDTO.setId(...);
        // ....
    }

    @BeforeEach
    void setupMocks() {
        when(apartmentService.findApartmentDto(anyLong())).thenReturn(testDTO);
        when(apartmentService.findById(anyLong())).thenReturn(testApartment);
    }

    @Test
    void contextLoads(){
        assertThat(apartmentController).isNotNull();
    }

    @Test
    void canLoadAllApartmentsPage() throws Exception {
        this.mockMvc.perform(get("/admin/apartments").with(csrf()).flashAttr("auth_admin",testUser))
                .andExpect(status().isOk());
    }

    @Test
    void canLoadApartmentInfoPage() throws Exception {
        this.mockMvc.perform(get("/admin/apartments/"+testApartment.getId()).with(csrf()).flashAttr("auth_admin",testUser))
                .andExpect(status().isOk());
    }

    @Test
    void canLoadApartmentCreatePage() throws Exception {
        this.mockMvc.perform(get("/admin/apartments/new").with(csrf()).flashAttr("auth_admin",testUser))
                .andExpect(status().isOk());
    }

    @Test
    void canLoadApartmentEditPage() throws Exception {
        this.mockMvc.perform(get("/admin/apartments/edit/"+testApartment.getId()).with(csrf()).flashAttr("auth_admin", testUser))
                .andExpect(status().isOk());
    }

    @Test
    void canSaveApartment() throws Exception {
        // ...
    }

    @Test
    void canSaveApartmentAndOpenNew() throws Exception {
        // ...
    }

    @Test
    void canDeleteApartment() throws Exception {
        this.mockMvc.perform(get("/admin/apartments/delete/"+testApartment.getId()).with(csrf()).flashAttr("auth_admin",testUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/apartments"));
    }

    @Test
    void canLoadIncomesByAccountPage() throws Exception {
        this.mockMvc.perform(get("/admin/apartments/incomesByApartmentAccount/"+testApartment.getId()).with(csrf()).flashAttr("auth_admin",testUser))
                .andExpect(status().isOk());
    }

    @Test
    void canLoadInvoicesByApartmentPage() throws Exception {
        this.mockMvc.perform(get("/admin/apartments/invoicesByApartment/"+testApartment.getId()).with(csrf()).flashAttr("auth_admin",testUser))
                .andExpect(status().isOk());
    }

    @Test
    void canLoadMetersDataByApartmentPage() throws Exception {
        this.mockMvc.perform(get("/admin/apartments/metersDataByApartment/"+testApartment.getId()).with(csrf()).flashAttr("auth_admin",testUser))
                .andExpect(status().isOk());
    }

    @Test
    void canLoadNewIncomesByApartmentPage() throws Exception {
        this.mockMvc.perform(get("/admin/apartments/NewIncomesByApartment/"+testApartment.getId()).with(csrf()).flashAttr("auth_admin",testUser))
                .andExpect(status().isOk());
    }

    @Test
    void canLoadNewInvoiceByApartmentPage() throws Exception {
        this.mockMvc.perform(get("/admin/apartments/NewInvoiceByApartment/"+testApartment.getId()).with(csrf()).flashAttr("auth_admin",testUser))
                .andExpect(status().isOk());
    }

    // ...

    @Test
    void canGetSectionAJAX() throws Exception {
        MvcResult result = this.mockMvc.perform(get("/admin/apartments/get-section?id="+testApartment.getId()).with(csrf()).flashAttr("auth_admin",testUser))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void canGetSingleApartmentAJAX() throws Exception {
        MvcResult result = this.mockMvc.perform(get("/admin/apartments/get-apartment?id="+testApartment.getId()).with(csrf()).flashAttr("auth_admin",testUser))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(result.getResponse().getContentAsString()).isEqualTo(jsonMapper.writeValueAsString(testDTO));
    }

    @Test
    void canGetOwnerAJAX() throws Exception {
        MvcResult result = this.mockMvc.perform(get("/admin/apartments/get-owner?id="+testApartment.getId()).with(csrf()).flashAttr("auth_admin",testUser))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(result.getResponse().getContentAsString()).isEqualTo(jsonMapper.writeValueAsString(testOwner));
    }

    @Test
    void canGetMetersAJAX() throws Exception {
        MvcResult result = this.mockMvc.perform(get("/admin/apartments/get-meters?id="+testApartment.getId()).with(csrf()).flashAttr("auth_admin",testUser))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(result.getResponse().getContentAsString()).isEqualTo(jsonMapper.writeValueAsString(meterDataList));

    }


}
