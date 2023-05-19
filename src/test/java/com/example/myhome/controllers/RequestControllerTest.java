package com.example.myhome.controllers;

import com.example.myhome.config.TestConfig;
import com.example.myhome.controller.RequestController;
import com.example.myhome.dto.RepairRequestDTO;
import com.example.myhome.mapper.RepairRequestDTOMapper;
import com.example.myhome.model.*;
import com.example.myhome.repository.RepairRequestRepository;
import com.example.myhome.service.RepairRequestService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeAll;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = TestConfig.class)
@AutoConfigureMockMvc
public class RequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Admin testUser;

    @Autowired
    private RequestController controller;

    @MockBean
    private RepairRequestService service;

    @Autowired
    private RepairRequestRepository repository;

    static RepairRequest testRequest = new RepairRequest();
    static RepairRequestDTO testDTO = new RepairRequestDTO();
    static RepairRequestDTOMapper mapper = new RepairRequestDTOMapper();

    static ObjectMapper jsonMapper = new ObjectMapper();
    static String jsonPageString = "";
    static Page<RepairRequestDTO> testPage;

    @BeforeAll
    static void setupObjects() throws JsonProcessingException {
        testRequest.setRequest_date(LocalDateTime.now().plusMonths(1));
        testRequest.setId(1L);
        Apartment apartment = new Apartment();
        apartment.setId(1L);
        testRequest.setApartment(apartment);
        Owner owner = new Owner();
        owner.setId(1L);
        testRequest.setOwner(owner);
        testRequest.setStatus(RepairStatus.ACCEPTED);
        testRequest.setDescription("test");
        Admin master = new Admin();
        master.setId(1L);
        testRequest.setMaster(master);
        UserRole role = new UserRole();
        role.setId(1L);
        testRequest.setMaster_type(role);
        testRequest.setComment("test");
        testRequest.setBest_time_request(LocalDateTime.now().plusDays(1));

        testDTO = mapper.fromRequestToDTO(testRequest);

        jsonMapper.registerModule(new JavaTimeModule());
        testPage = new PageImpl<>(List.of(testDTO), PageRequest.of(1,1), 1);
        jsonPageString = jsonMapper.writeValueAsString(testPage);
    }

    @BeforeEach
    void setupMocks() throws IllegalAccessException, JsonProcessingException {
        when(service.findAllBySpecification(any(), anyInt(), anyInt())).thenReturn(testPage);
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
    void showRequestsPageTest() throws Exception {
        this.mockMvc.perform(get("/admin/requests").flashAttr("auth_admin", testUser)).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("test")
    void showRequestInfoPageTest() throws Exception {
        Long id = testUser.getId();
        this.mockMvc.perform(get("/admin/requests/" + id).flashAttr("auth_admin", testUser)).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("test")
    void showRequestCreatePageTest() throws Exception {
        this.mockMvc.perform(get("/admin/requests/create").flashAttr("auth_admin", testUser)).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("test")
    void showRequestUpdatePageTest() throws Exception {
        Long id = testUser.getId();
        this.mockMvc.perform(get("/admin/requests/update/" + id).flashAttr("auth_admin", testUser)).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("test")
    void createRequest_NotValidated_Test() throws Exception {
        this.mockMvc.perform(post("/admin/requests/create").with(csrf()).flashAttr("auth_admin", testUser))
                .andExpect(status().isOk())
                .andExpect(model().attribute("validation", "failed"));
    }

    @Test
    @WithUserDetails("test")
    void createRequest_Validated_Test() throws Exception {
        this.mockMvc.perform(post("/admin/requests/create").with(csrf()).flashAttr("repairRequestDTO", testDTO).flashAttr("auth_admin", testUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(model().attributeDoesNotExist("validation"))
                .andExpect(redirectedUrl("/admin/requests"));
    }

    @Test
    @WithUserDetails("test")
    void updateRequest_NotValidated_Test() throws Exception {
        this.mockMvc.perform(post("/admin/requests/update/"+testRequest.getId()).with(csrf()).flashAttr("auth_admin", testUser))
                .andExpect(status().isOk())
                .andExpect(model().attribute("validation", "failed"));
    }

    @Test
    @WithUserDetails("test")
    void updateRequest_Validated_Test() throws Exception {
        this.mockMvc.perform(post("/admin/requests/update/"+testRequest.getId()).with(csrf()).flashAttr("repairRequestDTO", testDTO).flashAttr("auth_admin", testUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(model().attributeDoesNotExist("validation"))
                .andExpect(redirectedUrl("/admin/requests"));
    }

    @Test
    @WithUserDetails("test")
    void deleteRequestTest() throws Exception {
        this.mockMvc.perform(get("/admin/requests/delete/"+testRequest.getId()).with(csrf()).flashAttr("auth_admin", testUser))
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("test")
    void getRequestsAJAX_WithoutParameters_Test() throws Exception {
        this.mockMvc.perform(get("/admin/requests/get-requests").with(csrf()).flashAttr("auth_admin", testUser))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithUserDetails("test")
    void getRequestsAJAX_WithIncorrectParameters_Test() throws Exception {
        this.mockMvc.perform(get("/admin/requests/get-requests?page=1").with(csrf()).flashAttr("auth_admin", testUser))
                .andExpect(status().isBadRequest());
        this.mockMvc.perform(get("/admin/requests/get-requests?size=1").with(csrf()).flashAttr("auth_admin", testUser))
                .andExpect(status().isBadRequest());
        this.mockMvc.perform(get("/admin/requests/get-requests?page=1&size=1").with(csrf()).flashAttr("auth_admin", testUser))
                .andExpect(status().isBadRequest());
        this.mockMvc.perform(get("/admin/requests/get-requests?page=1&size=1&filters=").with(csrf()).flashAttr("auth_admin", testUser))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithUserDetails("test")
    void getRequestsAJAX_WithCorrectParameters_Test() throws Exception {
        this.mockMvc.perform(get("/admin/requests/get-requests?page=1&size=1&filters=null").with(csrf()).flashAttr("auth_admin", testUser))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonPageString));
    }
}
