package com.example.myhome.controllers;

import com.example.myhome.config.TestConfig;
import com.example.myhome.controller.ApartmentController;
import com.example.myhome.dto.ApartmentDTO;
import com.example.myhome.dto.OwnerDTO;
import com.example.myhome.mapper.AccountDTOMapper;
import com.example.myhome.mapper.ApartmentDTOMapper;
import com.example.myhome.model.*;
import com.example.myhome.model.filter.FilterForm;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(classes = TestConfig.class)
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

    static private AccountDTOMapper accountDTOMapper;
    static private ApartmentDTOMapper apartmentDTOMapper;


    @Autowired
    private MockMvc mockMvc;

    static Apartment testApartment;
    static ApartmentDTO testDTO;
    static List<Apartment> apartmentList;
    static List<Owner> ownerList;
    static List<ApartmentDTO> apartmentDTOList;
    static List<OwnerDTO> ownerDTOList;
    static Page<ApartmentDTO> apartmentPage;
    static Page<OwnerDTO> ownerPage;
    static Owner testOwner;
    static List<MeterData> meterDataList;

    static ObjectMapper jsonMapper = new ObjectMapper();

    @BeforeAll
    static void setupObjects() {
        // testApartment.setId(...);
        // ....

        testApartment = new Apartment();
        testApartment.setId(1L);
        testApartment.setSection("test");
        testApartment.setFloor("test");
        testApartment.setNumber(100L);
        testApartment.setAccount(new ApartmentAccount());
        testApartment.setBalance(100.0);
        testApartment.setSquare(100.0);
        testApartment.setTariff(new Tariff());

        Owner testOwner = new Owner();
        testOwner.setId(1L);
        testApartment.setOwner(testOwner);

        OwnerDTO ownerDTO = new OwnerDTO();
        ownerDTO.setId(1L);
        ownerDTOList = List.of(ownerDTO, ownerDTO, ownerDTO);

        apartmentDTOMapper = new ApartmentDTOMapper();

        testDTO = apartmentDTOMapper.fromApartmentToDTO(testApartment);

        apartmentList = List.of(testApartment, testApartment, testApartment);
        apartmentDTOList = List.of(testDTO, testDTO, testDTO);

        apartmentPage = new PageImpl<>(apartmentDTOList, PageRequest.of(1,1),1);
        ownerPage = new PageImpl<>(ownerDTOList, PageRequest.of(1,1),1);



    }

    @BeforeEach
    void setupMocks() {
        when(apartmentService.findApartmentDto(anyLong())).thenReturn(testDTO);
        when(apartmentService.findById(anyLong())).thenReturn(testApartment);
        when(ownerService.findByNameFragmentDTO(anyString(), any(Pageable.class))).thenReturn(ownerPage);
        when(apartmentService.findBySpecificationAndPage(anyInt(), anyInt(), any(FilterForm.class))).thenReturn(apartmentPage);
    }

    @Test
    void contextLoads(){

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
        this.mockMvc.perform(post("/admin/apartments/save")
                        .param("apartment", testDTO.toString())
                        .with(csrf())
                        .flashAttr("auth_admin",testUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/apartments"))
                .andExpect(model().attributeDoesNotExist("validation", "failed"));
    }

    @Test
    void doesntSaveApartment_NotValidated_Test() throws Exception {
        this.mockMvc.perform(post("/admin/apartments/save")
                        .param("apartment", new ApartmentDTO().toString())
                        .with(csrf())
                        .flashAttr("auth_admin",testUser))
                .andExpect(status().isOk())
                .andExpect(model().attribute("validation", "failed"));
    }

    @Test
    void canSaveApartment_AndOpenNew() throws Exception {
        this.mockMvc.perform(post("/admin/apartments/save&new")
                        .param("apartment", testDTO.toString())
                        .with(csrf())
                        .flashAttr("auth_admin",testUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/apartments"))
                .andExpect(model().attributeDoesNotExist("validation", "failed"));
    }

    @Test
    void doesntSaveApartmentAndOpenNew_NotValidated_Test() throws Exception {
        this.mockMvc.perform(post("/admin/apartments/save&new")
                        .param("apartment", new ApartmentDTO().toString())
                        .with(csrf())
                        .flashAttr("auth_admin",testUser))
                .andExpect(status().isOk())
                .andExpect(model().attribute("validation", "failed"));
    }

    @Test
    void canDeleteApartment() throws Exception {
        this.mockMvc.perform(get("/admin/apartments/delete/"+testApartment.getId()).with(csrf()).flashAttr("auth_admin",testUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/apartments"));
    }

    @Test
    void errorOnDeleteApartmentTest() throws Exception {
        doThrow(new Exception()).when(apartmentService).deleteById(anyLong());
        this.mockMvc.perform(get("/admin/apartments/delete/"+testApartment.getId()).with(csrf()).flashAttr("auth_admin",testUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/apartments"))
                .andExpect(model().attributeExists("fail"));
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

    @Test
    void canGetOwnersAJAX() throws Exception {
        MvcResult result = this.mockMvc.perform(get("/admin/apartments/getUsers").with(csrf()).flashAttr("auth_admin",testUser))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void canGetApartmentsByPageAJAX() throws Exception {
        MvcResult result = this.mockMvc.perform(get("/admin/apartments/get-apartments-page?page=1&size=1&filters=null").with(csrf()).flashAttr("auth_admin",testUser))
                .andExpect(status().isOk())
                .andReturn();
    }

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
    void canGetApartmentPageAJAX() throws Exception {

    }

    @Test
    void canGetUserPageAJAX() throws Exception {

    }

    @Test
    void canGetOwnerAJAX() throws Exception {
        MvcResult result = this.mockMvc.perform(get("/admin/apartments/get-owner?flat_id="+testApartment.getId()).with(csrf()).flashAttr("auth_admin",testUser))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(result.getResponse().getContentAsString()).isEqualTo(jsonMapper.writeValueAsString(testOwner));
    }

    @Test
    void canGetMetersAJAX() throws Exception {
        MvcResult result = this.mockMvc.perform(get("/admin/apartments/get-meters?flat_id="+testApartment.getId()).with(csrf()).flashAttr("auth_admin",testUser))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(result.getResponse().getContentAsString()).isEqualTo(jsonMapper.writeValueAsString(meterDataList));

    }


}
