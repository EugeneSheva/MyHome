package com.example.myhome.controllers;

import com.example.myhome.config.TestConfig;
import com.example.myhome.controller.MeterController;
import com.example.myhome.dto.BuildingDTO;
import com.example.myhome.dto.MeterDataDTO;
import com.example.myhome.mapper.MeterDTOMapper;
import com.example.myhome.model.*;
import com.example.myhome.repository.MeterDataRepository;
import com.example.myhome.service.BuildingService;
import com.example.myhome.service.MeterDataService;
import com.example.myhome.service.ServiceService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = TestConfig.class)
@AutoConfigureMockMvc
public class MeterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Admin testUser;

    @Autowired
    private MeterController controller;

    @MockBean
    private MeterDataService service;

    @MockBean
    private BuildingService buildingService;

    @MockBean
    private ServiceService serviceService;

    @Autowired
    private MeterDataRepository repository;

    MeterData testMeter = new MeterData();
    MeterDataDTO testDTO = new MeterDataDTO();
    MeterDTOMapper mapper = new MeterDTOMapper();

    ObjectMapper jsonMapper = new ObjectMapper();
    String jsonPageString = "";

    @BeforeEach
    void create() throws JsonProcessingException {
        testMeter.setId(1L);
        Building building = new Building();
        building.setId(1L);
        testMeter.setBuilding(building);
        testMeter.setSection("test");
        Service service1 = new Service();
        service1.setId(1L);
        service1.setUnit(new Unit());
        testMeter.setService(service1);
        Apartment apartment = new Apartment();
        apartment.setOwner(new Owner());
        apartment.setId(1L);
        testMeter.setApartment(apartment);
        testMeter.setStatus(MeterPaymentStatus.PAID);
        testMeter.setDate(LocalDate.now().minusMonths(1L));
        testMeter.setCurrentReadings(100.0);

        jsonMapper.registerModule(new JavaTimeModule());

        testDTO = mapper.fromMeterToDTO(testMeter);
        testDTO.setDate(LocalDate.now().minusMonths(1));
        Page<MeterDataDTO> testPage = new PageImpl<>(List.of(testDTO), PageRequest.of(1,1), 1);
        jsonPageString = jsonMapper.writeValueAsString(testPage);
        when(service.findAllBySpecification(any(), anyInt(), anyInt())).thenReturn(testPage);
        when(service.saveMeterData(any(MeterDataDTO.class))).thenReturn(testMeter);

        when(buildingService.findBuildingDTObyId(anyLong())).thenReturn(new BuildingDTO());
        when(buildingService.findAllDTO()).thenReturn(List.of(new BuildingDTO()));
        when(serviceService.findAllServices()).thenReturn(List.of(new Service()));
    }

    @Test
    void contextLoads() {
        assertThat(controller).isNotNull();
        assertThat(repository).isNotNull();
        assertThat(mockMvc).isNotNull();

        assertThat(testUser).isNotNull();
    }

    @Test
    @WithUserDetails("test")
    void showMetersPageTest() throws Exception {
        this.mockMvc.perform(get("/admin/meters").flashAttr("auth_admin", testUser)).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("test")
    void showMeterInfoPageTest() throws Exception {
        Long id = testUser.getId();
        this.mockMvc.perform(get("/admin/meters/" + id).flashAttr("auth_admin", testUser)).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("test")
    void showMeterData_WithoutParameters_PageTest() throws Exception {
        this.mockMvc.perform(get("/admin/meters/data").with(csrf()).flashAttr("auth_admin", testUser))
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("test")
    void showMeterData_WithParameters_PageTest() throws Exception {
        this.mockMvc.perform(get("/admin/meters/data?flat_id=1&service_id=1").with(csrf()).flashAttr("auth_admin", testUser))
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("test")
    void showMeterCreatePageTest() throws Exception {
        this.mockMvc.perform(get("/admin/meters/create").flashAttr("auth_admin", testUser)).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("test")
    void showMeterCreateAdditionalPage_WithoutParameters_Test() throws Exception {
        this.mockMvc.perform(get("/admin/meters/create-add").with(csrf()).flashAttr("auth_admin", testUser))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithUserDetails("test")
    void showMeterCreateAdditionalPage_WithParameters_Test() throws Exception {
        this.mockMvc.perform(get("/admin/meters/create-add?flat_id=1&service_id=1").with(csrf()).flashAttr("auth_admin", testUser))
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("test")
    void showMeterUpdatePageTest() throws Exception {
        Long id = testUser.getId();
        this.mockMvc.perform(get("/admin/meters/update/" + id).flashAttr("auth_admin", testUser)).andExpect(status().isOk());
    }

    //todo post

    @Test
    @WithUserDetails("test")
    void createMeterTest_NotValidated_Test() throws Exception {
        this.mockMvc.perform(post("/admin/meters/create").with(csrf()).flashAttr("auth_admin", testUser))
                .andExpect(status().isOk())
                .andExpect(model().attribute("validation", "failed"));
    }

    @Test
    @WithUserDetails("test")
    void createMeterTest_Validated_Test() throws Exception {
        this.mockMvc.perform(post("/admin/meters/create").flashAttr("meterDataDTO", testDTO).with(csrf()).flashAttr("auth_admin", testUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(model().attributeDoesNotExist("validation"))
                .andExpect(redirectedUrl("/admin/meters/data?flat_id="+testDTO.getApartmentID()+"&service_id="+testDTO.getServiceID()));
    }

    @Test
    @WithUserDetails("test")
    void createAdditionalMeterTest_NotValidated_Test() throws Exception {
        this.mockMvc.perform(post("/admin/meters/create-add").with(csrf()).flashAttr("auth_admin", testUser))
                .andExpect(status().isOk())
                .andExpect(model().attribute("validation", "failed"));
    }

    @Test
    @WithUserDetails("test")
    void createAdditionalMeterTest_Validated_Test() throws Exception {
        this.mockMvc.perform(post("/admin/meters/create-add").flashAttr("meterDataDTO", testDTO).with(csrf()).flashAttr("auth_admin", testUser))
                .andExpect(status().is3xxRedirection()).andExpect(model().attributeDoesNotExist("validation"))
                .andExpect(redirectedUrl("/admin/meters/data?flat_id="+testDTO.getApartmentID()+"&service_id="+testDTO.getServiceID()));
    }

    @Test
    @WithUserDetails("test")
    void updateMeterTest_NotValidated_Test() throws Exception {
        this.mockMvc.perform(post("/admin/meters/update/"+testMeter.getId()).with(csrf()).flashAttr("auth_admin", testUser))
                .andExpect(status().isOk())
                .andExpect(model().attribute("validation", "failed"));
    }

    @Test
    @WithUserDetails("test")
    void updateMeterTest_Validated_Test() throws Exception {
        this.mockMvc.perform(post("/admin/meters/update/"+testMeter.getId()).flashAttr("meterDataDTO", testDTO).with(csrf()).flashAttr("auth_admin", testUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(model().attributeDoesNotExist("validation"))
                .andExpect(redirectedUrl("/admin/meters/data?flat_id="+testDTO.getApartmentID()+"&service_id="+testDTO.getServiceID()));
    }

    @Test
    @WithUserDetails("test")
    void deleteMeterTest() throws Exception {
        this.mockMvc.perform(get("/admin/meters/delete/" + testMeter.getId()).with(csrf()).flashAttr("auth_admin", testUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/meters"));
    }

    @Test
    @WithUserDetails("test")
    void getMetersAJAX_WithoutParameters_Test() throws Exception {
        this.mockMvc.perform(get("/admin/meters/get-meters").with(csrf()).flashAttr("auth_admin",testUser))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithUserDetails("test")
    void getMetersAJAX_WithIncorrectParameters_Test() throws Exception {
        this.mockMvc.perform(get("/admin/meters/get-meters?page=1").with(csrf()).flashAttr("auth_admin", testUser))
                .andExpect(status().isBadRequest());
        this.mockMvc.perform(get("/admin/meters/get-meters?size=1").with(csrf()).flashAttr("auth_admin", testUser))
                .andExpect(status().isBadRequest());
        this.mockMvc.perform(get("/admin/meters/get-meters?page=1&size=1").with(csrf()).flashAttr("auth_admin", testUser))
                .andExpect(status().isBadRequest());
        this.mockMvc.perform(get("/admin/meters/get-meters?page=1&size=1&filters=").with(csrf()).flashAttr("auth_admin", testUser))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithUserDetails("test")
    void getMetersAJAX_WithCorrectParameters_Test() throws Exception {
        this.mockMvc.perform(get("/admin/meters/get-meters?page=1&size=1&filters=null").with(csrf()).flashAttr("auth_admin", testUser))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonPageString));
    }

    @Test
    @WithUserDetails("test")
    void getMeterDataAJAX_WithCorrectParameters_Test() throws Exception {
        this.mockMvc.perform(get("/admin/meters/get-meter-data?page=1&size=1&filters=null").with(csrf()).flashAttr("auth_admin", testUser))
                .andExpect(status().is(200))
                .andExpect(content().string(jsonPageString));
    }
}
