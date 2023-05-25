package com.example.myhome.controllers;

import com.example.myhome.config.TestConfig;
import com.example.myhome.controller.MeterController;
import com.example.myhome.dto.BuildingDTO;
import com.example.myhome.dto.MeterDataDTO;
import com.example.myhome.mapper.MeterDTOMapper;
import com.example.myhome.model.*;
import com.example.myhome.model.filter.FilterForm;
import com.example.myhome.repository.MeterDataRepository;
import com.example.myhome.service.ApartmentService;
import com.example.myhome.service.BuildingService;
import com.example.myhome.service.MeterDataService;
import com.example.myhome.service.ServiceService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
@WithUserDetails("test")
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
    @MockBean
    private ApartmentService apartmentService;

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
        Service service1 = new Service();
        service1.setId(1L);
        service1.setName("test");
        Unit unit = new Unit();
        unit.setId(1L);
        unit.setName("test");
        service1.setUnit(unit);
        testMeter.setService(service1);
        Apartment apartment = new Apartment();
        apartment.setId(1L);
        apartment.setNumber(10L);
        Owner owner = new Owner();
        owner.setId(1L);
        apartment.setOwner(owner);
        testMeter.setApartment(apartment);
        Building building = new Building();
        building.setId(1L);
        building.setName("test");
        testMeter.setBuilding(building);
        testMeter.setStatus(MeterPaymentStatus.PAID);
        testMeter.setSection("test");
        testMeter.setCurrentReadings(100.0);

        jsonMapper.registerModule(new JavaTimeModule());

        testDTO = mapper.fromMeterToDTO(testMeter);
        testDTO.setDate(LocalDate.now().minusMonths(1));
        List<MeterData> meterList = List.of(testMeter);
        List<MeterDataDTO> dtoList = List.of(testDTO);
        Page<MeterDataDTO> testPage = new PageImpl<>(dtoList, PageRequest.of(1,1), 1);
        jsonPageString = jsonMapper.writeValueAsString(testPage);
        when(service.findAllByFiltersAndPage(any(), any())).thenReturn(testPage);
        when(service.findAllBySpecification(any(), anyInt(), anyInt())).thenReturn(testPage);
        when(service.findSingleMeterData(any(FilterForm.class), any(Pageable.class))).thenReturn(testPage);
        when(service.findSingleMeterData(anyLong(), anyLong())).thenReturn(meterList);
        when(service.saveMeterData(any(MeterDataDTO.class))).thenReturn(testMeter);
        when(service.findMeterDataDTOById(anyLong())).thenReturn(testDTO);
        when(service.saveMeterDataAJAX(any(),any(),any(),any(),any(),any(),any(),any())).thenReturn(testMeter);

        when(buildingService.findBuildingDTObyId(anyLong())).thenReturn(new BuildingDTO());
        when(buildingService.findAllDTO()).thenReturn(List.of(new BuildingDTO()));
        when(serviceService.findAllServices()).thenReturn(List.of(new Service()));
        when(apartmentService.getNumberById(anyLong())).thenReturn(1L);
    }

    @Test
    void contextLoads() {
        assertThat(controller).isNotNull();
        assertThat(repository).isNotNull();
        assertThat(mockMvc).isNotNull();

        assertThat(testUser).isNotNull();
    }

    @Test
    void showMetersPageTest() throws Exception {
        this.mockMvc.perform(get("/admin/meters").flashAttr("auth_admin", testUser)).andExpect(status().isOk());
    }

    @Test
    void showMeterInfoPageTest() throws Exception {
        Long id = testUser.getId();
        this.mockMvc.perform(get("/admin/meters/info/" + id).flashAttr("auth_admin", testUser)).andExpect(status().isOk());
    }

    @Test
    void showMeterData_WithoutParameters_PageTest() throws Exception {
        this.mockMvc.perform(get("/admin/meters/data").with(csrf()).flashAttr("auth_admin", testUser))
                .andExpect(status().isOk());
    }

    @Test
    void showMeterData_WithParameters_PageTest() throws Exception {
        this.mockMvc.perform(get("/admin/meters/data?flat_id=1&service_id=1").with(csrf()).flashAttr("auth_admin", testUser))
                .andExpect(status().isOk());
    }

    @Test
    void showMeterCreatePageTest() throws Exception {
        this.mockMvc.perform(get("/admin/meters/create").flashAttr("auth_admin", testUser)).andExpect(status().isOk());
    }

    @Test
    void showMeterCreateAdditionalPage_WithoutParameters_Test() throws Exception {
        this.mockMvc.perform(get("/admin/meters/create-add").with(csrf()).flashAttr("auth_admin", testUser))
                .andExpect(status().isBadRequest());
    }

    @Test
    void showMeterCreateAdditionalPage_WithParameters_Test() throws Exception {
        this.mockMvc.perform(get("/admin/meters/create-add?flat_id=1&service_id=1").with(csrf()).flashAttr("auth_admin", testUser))
                .andExpect(status().isOk());
    }

    @Test
    void showMeterUpdatePageTest() throws Exception {
        Long id = testUser.getId();
        this.mockMvc.perform(get("/admin/meters/update/" + id).flashAttr("auth_admin", testUser)).andExpect(status().isOk());
    }

    //todo post

    @Test
    void createMeterTest_NotValidated_Test() throws Exception {
        this.mockMvc.perform(post("/admin/meters/create").with(csrf()).flashAttr("auth_admin", testUser))
                .andExpect(status().isOk())
                .andExpect(model().attribute("validation", "failed"));
    }

    @Test
    void createMeterTest_Validated_Test() throws Exception {
        this.mockMvc.perform(post("/admin/meters/create").flashAttr("meterDataDTO", testDTO).with(csrf()).flashAttr("auth_admin", testUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(model().attributeDoesNotExist("validation"))
                .andExpect(redirectedUrl("/admin/meters/data?flat_id="+testDTO.getApartmentID()+"&service_id="+testDTO.getServiceID()));
    }

    @Test
    void createAdditionalMeterTest_NotValidated_Test() throws Exception {
        this.mockMvc.perform(post("/admin/meters/create-add").with(csrf()).flashAttr("auth_admin", testUser))
                .andExpect(status().isOk())
                .andExpect(model().attribute("validation", "failed"));
    }

    @Test
    void createAdditionalMeterTest_Validated_Test() throws Exception {
        this.mockMvc.perform(post("/admin/meters/create-add").flashAttr("meterDataDTO", testDTO).with(csrf()).flashAttr("auth_admin", testUser))
                .andExpect(status().is3xxRedirection()).andExpect(model().attributeDoesNotExist("validation"))
                .andExpect(redirectedUrl("/admin/meters/data?flat_id="+testDTO.getApartmentID()+"&service_id="+testDTO.getServiceID()));
    }

    @Test
    void updateMeterTest_NotValidated_Test() throws Exception {
        this.mockMvc.perform(post("/admin/meters/update/"+testMeter.getId()).with(csrf()).flashAttr("auth_admin", testUser))
                .andExpect(status().isOk())
                .andExpect(model().attribute("validation", "failed"));
    }

    @Test
    void updateMeterTest_Validated_Test() throws Exception {
        this.mockMvc.perform(post("/admin/meters/update/"+testMeter.getId()).flashAttr("meterDataDTO", testDTO).with(csrf()).flashAttr("auth_admin", testUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(model().attributeDoesNotExist("validation"))
                .andExpect(redirectedUrl("/admin/meters/data?flat_id="+testDTO.getApartmentID()+"&service_id="+testDTO.getServiceID()));
    }

    @Test
    void deleteMeterTest() throws Exception {
        this.mockMvc.perform(get("/admin/meters/delete/" + testMeter.getId()).with(csrf()).flashAttr("auth_admin", testUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/meters"));
    }

    @Test
    void getMetersAJAX_WithoutParameters_Test() throws Exception {
        this.mockMvc.perform(get("/admin/meters/get-meters").with(csrf()).flashAttr("auth_admin",testUser))
                .andExpect(status().isBadRequest());
    }

    @Test
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
    void getMetersAJAX_WithCorrectParameters_Test() throws Exception {
        this.mockMvc.perform(get("/admin/meters/get-meters?page=1&size=1&filters=null").with(csrf()).flashAttr("auth_admin", testUser))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonPageString));
    }

    @Test
    void getMeterDataAJAX_WithCorrectParameters_Test() throws Exception {
        this.mockMvc.perform(get("/admin/meters/get-meter-data?page=1&size=1&filters=null").with(csrf()).flashAttr("auth_admin", testUser))
                .andExpect(status().is(200))
                .andExpect(content().string(jsonPageString));
    }

    @Test
    void saveMeterAJAXTest() throws Exception {
        this.mockMvc.perform(post("/admin/meters/save-meter")
                .with(csrf())
                .param("building","1")
                .param("section","test")
                .param("apartment","1")
                .param("currentReadings","1")
                .param("status","PAID")
                .param("service","1")
                .param("date", "2022-11-11")
                .flashAttr("auth_admin",testUser))
                .andExpect(status().isOk());
    }

    @Test
    void saveMeterAJAX_NotValidated_Test() throws Exception {
        when(service.saveMeterDataAJAX(any(),any(),any(),any(),any(),any(),any(),any())).thenReturn(new MeterData());
        this.mockMvc.perform(post("/admin/meters/save-meter")
                        .with(csrf())
                        .param("building","1")
                        .param("section","test")
                        .param("apartment","1")
                        .param("currentReadings","1")
                        .param("status","PAID")
                        .param("service","1")
                        .param("date", "2022-11-11")
                        .flashAttr("auth_admin",testUser))
                .andExpect(status().isOk());
    }
}
