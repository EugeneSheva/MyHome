package com.example.myhome.controllers;

import com.example.myhome.config.TestConfig;
import com.example.myhome.controller.OwnerController;
import com.example.myhome.dto.OwnerDTO;
import com.example.myhome.mapper.OwnerDTOMapper;
import com.example.myhome.model.Admin;
import com.example.myhome.model.Owner;
import com.example.myhome.repository.OwnerRepository;
import com.example.myhome.service.AccountService;
import com.example.myhome.service.ApartmentService;
import com.example.myhome.service.BuildingService;
import com.example.myhome.service.OwnerService;
import com.example.myhome.util.UserStatus;
import com.example.myhome.validator.OwnerValidator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = TestConfig.class)
@AutoConfigureMockMvc
@WithUserDetails("test")
public class OwnerControllerTest {

    @Autowired
    private Admin testUser;

    @MockBean private OwnerService ownerService;
    @MockBean private OwnerValidator ownerValidator;
    @MockBean private BuildingService buildingService;
    @MockBean private AccountService accountService;
    @MockBean private ApartmentService apartmentService;

    @MockBean private MessageSource messageSource;

    @Autowired private OwnerController ownerController;

    static Owner testOwner;
    static OwnerDTO testDTO;
    static OwnerDTOMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    static void setupObjects() {
        testOwner = new Owner();
        testOwner.setId(1L);
        testOwner.setFirst_name("brr");
        testOwner.setFathers_name("brr");
        testOwner.setLast_name("brr");
        testOwner.setPassword("test");
        testOwner.setEmail("test");
        testOwner.setDescription("test");
        testOwner.setAdded_at(LocalDateTime.now());
        testOwner.setEnabled(true);
        testOwner.setViber("test");
        testOwner.setBirthdate(LocalDate.now());
        testOwner.setStatus(UserStatus.NEW);

        mapper = new OwnerDTOMapper();

        testDTO = mapper.fromOwnerToDTO(testOwner);
    }

    @BeforeEach
    void setupMocks() {
        when(ownerService.findById(anyLong())).thenReturn(testOwner);
        when(ownerService.findByIdDTO(anyLong())).thenReturn(testDTO);
    }

    @Test
    void contextLoads() {
        assertThat(ownerController).isNotNull();
    }

    @Test
    void canLoadAllOwnersPage() throws Exception {
        this.mockMvc.perform(get("/admin/owners").with(csrf()).flashAttr("auth_admin",testUser))
                .andExpect(status().isOk());
    }

    @Test
    void canLoadOwnerInfoPage() throws Exception {
        this.mockMvc.perform(get("/admin/owners/"+testOwner.getId()).with(csrf()).flashAttr("auth_admin",testUser))
                .andExpect(status().isOk());
    }

    @Test
    void canLoadOwnerCreatePage() throws Exception {
        this.mockMvc.perform(get("/admin/owners/new").with(csrf()).flashAttr("auth_admin",testUser))
                .andExpect(status().isOk());
    }

    @Test
    void canLoadOwnerUpdatePage() throws Exception {
        this.mockMvc.perform(get("/admin/owners/edit/"+testOwner.getId()).with(csrf()).flashAttr("auth_admin",testUser))
                .andExpect(status().isOk());
    }

    @Test
    void canSaveOwner() throws Exception {
        //...
    }

    @Test
    void canDeleteOwner() throws Exception {
        this.mockMvc.perform(get("/admin/owners/delete/"+testOwner.getId()).with(csrf()).flashAttr("auth_admin",testUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/owners"));
    }


}
