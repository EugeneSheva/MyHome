package com.example.myhome.controllers;

import com.example.myhome.config.TestConfig;
import com.example.myhome.controller.BuildingController;
import com.example.myhome.dto.BuildingDTO;
import com.example.myhome.mapper.ApartmentDTOMapper;
import com.example.myhome.mapper.BuildingDTOMapper;
import com.example.myhome.model.Admin;
import com.example.myhome.model.Apartment;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = TestConfig.class)
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
    static List<Building> buildingList;
    static List<BuildingDTO> buildingDTOList;
    static Page<Building> buildingPage;
    static Page<BuildingDTO> buildingDTOPage;

    static BuildingDTOMapper buildingDTOMapper;
    static ApartmentDTOMapper apartmentDTOMapper;
    static ObjectMapper jsonMapper;


    @BeforeAll
    static void setupObjects() {
        testBuilding = new Building();
        testBuilding.setId(1L);
        testBuilding.setApartments(List.of(new Apartment(), new Apartment()));
        testBuilding.setAdmins(new ArrayList<>());
        testBuilding.setAddress("test");
        testBuilding.setFloors(List.of("test","test"));
        testBuilding.setSections(List.of("test","test"));
        testBuilding.setName("test");

        buildingDTOMapper = new BuildingDTOMapper();

        testDTO = buildingDTOMapper.fromBuildingToDTO(testBuilding);

        apartmentDTOMapper = new ApartmentDTOMapper();

        buildingList = List.of(testBuilding, testBuilding, testBuilding);
        buildingDTOList = List.of(testDTO, testDTO, testDTO);

        buildingPage = new PageImpl<>(buildingList, PageRequest.of(1,1), 1);
        buildingDTOPage = new PageImpl<>(buildingDTOList, PageRequest.of(1,1),1);
    }

    @BeforeEach
    void setupMocks() {
        when(buildingService.findById(anyLong())).thenReturn(testBuilding);
        when(buildingService.findBuildingDTObyId(anyLong())).thenReturn(testDTO);
        when(buildingService.countBuildings()).thenReturn(22L);
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
        this.mockMvc.perform(post("/admin/building/save")
                .with(csrf())
                .param("build", testBuilding.toString())
                .flashAttr("auth_admin",testUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/buildings"))
                .andExpect(model().attributeDoesNotExist("validation","failed"));
    }

    @Test
    void doesntSaveBuilding_NotValidated_Test() throws Exception {
        this.mockMvc.perform(post("/admin/building/save")
                        .with(csrf())
                        .param("build", new Building().toString())
                        .flashAttr("auth_admin",testUser))
                .andExpect(status().isOk())
                .andExpect(model().attribute("validation","failed"));
    }

    @Test
    void canDeleteBuilding() throws Exception {
        this.mockMvc.perform(get("/admin/buildings/delete/"+testBuilding.getId()).with(csrf()).flashAttr("auth_admin",testUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/buildings"));
    }

    @Test
    void canGetBuildingSectionsAJAX() throws Exception {
        this.mockMvc.perform(get("/admin/buildings/get-sections/"+testBuilding.getId())
                .with(csrf()).flashAttr("auth_admin",testUser))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void canGetBuildingFloorsAJAX() throws Exception {
        this.mockMvc.perform(get("/admin/buildings/get-floors/"+testBuilding.getId())
                        .with(csrf()).flashAttr("auth_admin",testUser))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void canGetBuildingSectionApartmentsAJAX() throws Exception {
        this.mockMvc.perform(get("/admin/buildings/get-section-apartments?id="+testBuilding.getId()+"&section_name=test")
                        .with(csrf()).flashAttr("auth_admin",testUser))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void canGetBuildingSectionApartments_FromQuery_AJAX() throws Exception {
        this.mockMvc.perform(get("/admin/buildings/get-section-apartments/"+testBuilding.getId())
                        .with(csrf()).flashAttr("auth_admin",testUser))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void canGetBuildingAJAX() throws Exception {
        this.mockMvc.perform(get("/admin/buildings/get-building?building_id="+testBuilding.getId())
                        .with(csrf()).flashAttr("auth_admin",testUser))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void canGetBuildingsAJAX() throws Exception {
        this.mockMvc.perform(get("/admin/buildings/get-buildings?search=test&page=1")
                        .with(csrf()).flashAttr("auth_admin",testUser))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void canGetBuildings_Page_AJAX() throws Exception {
        this.mockMvc.perform(get("/admin/buildings/get-buildings-page?page=1&size=1&filters=null")
                        .with(csrf()).flashAttr("auth_admin",testUser))
                .andExpect(status().isOk())
                .andReturn();
    }

}
