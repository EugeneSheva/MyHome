package com.example.myhome.controllers;

import com.example.myhome.controller.BuildingController;
import com.example.myhome.dto.BuildingDTO;
import com.example.myhome.mapper.BuildingDTOMapper;
import com.example.myhome.model.Admin;
import com.example.myhome.model.Building;
import com.example.myhome.repository.BuildingRepository;
import com.example.myhome.service.AdminService;
import com.example.myhome.service.BuildingService;
import com.example.myhome.validator.BuildingValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@WithUserDetails("test")
public class BuildingControllerTest {

    @Autowired
    private BuildingValidator validator;

    @Autowired
    private Admin testUser;

    @MockBean private BuildingService buildingService;
    @MockBean private AdminService adminService;
    @MockBean private MessageSource messageSource;

    @Autowired
    private BuildingController buildingController;

    @Autowired
    private BuildingDTOMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    static Building testBuilding;
    static BuildingDTO testDTO;

    static ObjectMapper jsonMapper;


    @BeforeAll
    static void setupObjects() {

    }

    @BeforeEach
    void setupMocks() {
        when(buildingService.findById(anyLong())).thenReturn(testBuilding);
        when(buildingService.findBuildingDTObyId(anyLong())).thenReturn(testDTO);
    }

    @Test
    void contextLoads() {
        assertThat(buildingController).isNotNull();
    }

    @Test
    void canLoadAllBuildingsPage() throws Exception {
        this.mockMvc.perform(get("/admin/buildings").with(csrf()).flashAttr("auth_admin",testUser))
                .andExpect(status().isOk());
    }

    @Test
    void canLoadBuildingInfoPage() throws Exception {
        this.mockMvc.perform(get("/admin/buildings/"+testBuilding.getId()).with(csrf()).flashAttr("auth_admin",testUser))
                .andExpect(status().isOk());
    }

    @Test
    void canLoadBuildingCreatePage() throws Exception {
        this.mockMvc.perform(get("/admin/buildings/new").with(csrf()).flashAttr("auth_admin",testUser))
                .andExpect(status().isOk());
    }

    @Test
    void canLoadBuildingUpdatePage() throws Exception {
        this.mockMvc.perform(get("/admin/buildings/edit/"+testBuilding.getId()).with(csrf()).flashAttr("auth_admin",testUser))
                .andExpect(status().isOk());
    }

    @Test
    void canSaveBuilding() throws Exception {
        //...
    }

    @Test
    void canDeleteBuilding() throws Exception {
        this.mockMvc.perform(get("/admin/buildings/delete/"+testBuilding.getId()).with(csrf()).flashAttr("auth_admin",testUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/buildings"));
    }

}
