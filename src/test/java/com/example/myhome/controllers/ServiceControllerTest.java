package com.example.myhome.controllers;

import com.example.myhome.config.TestConfig;
import com.example.myhome.controller.ServiceController;
import com.example.myhome.exception.NotFoundException;
import com.example.myhome.model.Admin;
import com.example.myhome.model.Service;
import com.example.myhome.model.ServiceForm;
import com.example.myhome.model.Unit;
import com.example.myhome.repository.ServiceRepository;
import com.example.myhome.service.ServiceService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.ConstraintViolation;
import javax.xml.validation.Validator;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = TestConfig.class)
@AutoConfigureMockMvc
@WithUserDetails("test")
public class ServiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Admin testUser;

    @MockBean private MessageSource messageSource;

    @Autowired
    private ServiceController controller;

    @MockBean
    private ServiceService service;

    @Autowired
    private LocalValidatorFactoryBean validator;

    @Autowired
    private ServiceRepository repository;

    static Service testService;
    static Unit testUnit;
    static String unitJSONString;

    static ObjectMapper jsonMapper;

    @BeforeAll
    static void setupObjects() throws JsonProcessingException {
        jsonMapper = new ObjectMapper();

        testService = new Service();
        testService.setId(1L);
        testService.setName("test");

        testUnit = new Unit();
        testUnit.setId(1L);
        testUnit.setName("test");
        unitJSONString = jsonMapper.writeValueAsString(testUnit);

        testService.setUnit(testUnit);

    }

    @BeforeEach
    void setupMocks(){
        when(service.findServiceById(anyLong())).thenReturn(testService);
    }

    @Test
    void contextLoads() {
        assertThat(controller).isNotNull();
        assertThat(repository).isNotNull();
        assertThat(mockMvc).isNotNull();

        assertThat(testUser).isNotNull();
    }

    @Test
    void showServicesPageTest() throws Exception {
        this.mockMvc.perform(get("/admin/services").flashAttr("auth_admin", testUser)).andExpect(status().isOk());
    }

    @Test
    void saveServicesTest() throws Exception {
        ServiceForm testForm = new ServiceForm();
        testForm.setServiceList(List.of(testService));
        testForm.setUnitList(List.of(testUnit));
        this.mockMvc.perform(post("/admin/services")
                        .param("new_service_names", new String[]{"test"})
                        .param("new_service_unit_names", new String[]{"test"})
                        .param("new_service_show_in_meters", new String[]{"test"})
                        .param("new_unit_names", new String[]{"test"})
                        .with(csrf())
                        .flashAttr("auth_admin",testUser)
                        .flashAttr("serviceForm", testForm))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/services"));
    }

    @Test
    void saveServices_WithIncorrectParameters_Test() throws Exception {
        this.mockMvc.perform(post("/admin/services")
                        .with(csrf())
                        .flashAttr("auth_admin",testUser)
                        .flashAttr("serviceForm", new ServiceForm()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void doesntSaveEmptyServicesTest() throws Exception {
        Set<ConstraintViolation<ServiceForm>> violations = validator.validate(new ServiceForm());
        assertThat(violations.size()).isEqualTo(2);

        this.mockMvc.perform(post("/admin/services")
                        .param("new_service_names", new String[]{"test"})
                        .param("new_service_unit_names", new String[]{"test"})
                        .param("new_service_show_in_meters", new String[]{"test"})
                        .param("new_unit_names", new String[]{"test"})
                        .with(csrf())
                        .flashAttr("auth_admin",testUser)
                        .flashAttr("serviceForm", new ServiceForm()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("validation", "failed"));
    }

    @Test
    void deleteServiceTest() throws Exception {
        this.mockMvc.perform(get("/admin/services/delete/" + testService.getId()).with(csrf()).flashAttr("auth_admin",testUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/services"));
    }

    @Test
    void deleteService_Exception_Test() throws Exception {
        doThrow(new NotFoundException()).when(service).deleteServiceById(anyLong());
        this.mockMvc.perform(get("/admin/services/delete/" + testService.getId()).with(csrf()).flashAttr("auth_admin",testUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/services"));
    }

    @Test
    void deleteUnitTest() throws Exception {
        this.mockMvc.perform(get("/admin/services/delete-unit/"+testUnit.getId()).with(csrf()).flashAttr("auth_admin", testUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/services"));
    }

    @Test
    void deleteUnit_Exception_Test() throws Exception {
        doThrow(new NotFoundException()).when(service).deleteUnitById(anyLong());
        this.mockMvc.perform(get("/admin/services/delete-unit/" + testUnit.getId()).with(csrf()).flashAttr("auth_admin",testUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/services"));
    }

    @Test
    void getUnitFromServiceAJAXTest() throws Exception {
        this.mockMvc.perform(get("/admin/services/get-unit?id="+testService.getId()).with(csrf()).flashAttr("auth_admin", testUser))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(unitJSONString));
    }

    @Test
    void getUnitFromServiceAJAX_WrongParameter_Test() throws Exception {
        this.mockMvc.perform(get("/admin/services/get-unit").with(csrf()).flashAttr("auth_admin", testUser))
                .andExpect(status().isBadRequest());
    }



}
