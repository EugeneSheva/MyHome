package com.example.myhome.controllers;

import com.example.myhome.config.TestConfig;
import com.example.myhome.controller.TariffController;
import com.example.myhome.model.Admin;
import com.example.myhome.model.Service;
import com.example.myhome.model.Tariff;
import com.example.myhome.model.Unit;
import com.example.myhome.repository.TariffRepository;
import com.example.myhome.service.TariffService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.With;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = TestConfig.class)
@AutoConfigureMockMvc
@WithUserDetails("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TariffControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Admin testUser;

    @Autowired
    private TariffController controller;

    @MockBean
    private TariffService service;

    @Autowired
    private TariffRepository repository;

    static Tariff testTariff = new Tariff();
    static Map<Service, Double> testMap1;
    static Map<String, Double> testMap2;

    static ObjectMapper jsonMapper;

    @BeforeAll
    static void setupObjects() throws JsonProcessingException {
        testTariff.setId(1L);
        testTariff.setDate(LocalDateTime.now().minusDays(1));
        testTariff.setName("test");
        testTariff.setDescription("test");
        Service testService = new Service();
        testService.setId(1L);
        testService.setUnit(new Unit());
        testService.setName("test");
        testMap1 = Map.of(testService, 100.0);
        testTariff.setComponents(testMap1);

        jsonMapper = new ObjectMapper();

        testMap2 = new HashMap<>();
        for(Map.Entry<Service, Double> entry : testMap1.entrySet()) {
            testMap2.put(jsonMapper.writeValueAsString(entry.getKey()), entry.getValue());
        }
    }

    @BeforeEach
    void setupMocks(){
        when(service.findTariffById(anyLong())).thenReturn(testTariff);
        when(service.buildComponentsMap(any(String[].class), any(String[].class))).thenReturn(testMap1);
    }

    @Test
    @Order(1)
    void contextLoads() {
        assertThat(controller).isNotNull();
        assertThat(repository).isNotNull();
        assertThat(mockMvc).isNotNull();

        assertThat(testUser).isNotNull();
    }

    @Test
    @Transactional
    @Order(2)
    void showTariffsPageTest() throws Exception {
        this.mockMvc.perform(get("/admin/tariffs").flashAttr("auth_admin", testUser)).andExpect(status().isOk());
    }

    @Test
    void showTariffInfoPageTest() throws Exception {
        Long id = testUser.getId();
        this.mockMvc.perform(get("/admin/tariffs/" + id).flashAttr("auth_admin", testUser)).andExpect(status().isOk());
    }

    @Test
    void showTariffCreatePageTest() throws Exception {
        this.mockMvc.perform(get("/admin/tariffs/create").flashAttr("auth_admin", testUser)).andExpect(status().isOk());
    }

    @Test
    void createTariff_NotValidated_Test() throws Exception {
        this.mockMvc.perform(post("/admin/tariffs/create")
                        .with(csrf())
                        .flashAttr("tariff", new Tariff())
                        .flashAttr("auth_admin", testUser))
                .andExpect(status().isOk())
                .andExpect(model().attribute("validation", "failed"));
    }

    @Test
    void createTariff_Validated_Test() throws Exception {
        this.mockMvc.perform(post("/admin/tariffs/create")
                .with(csrf())
                .flashAttr("tariff", testTariff)
                .flashAttr("auth_admin", testUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/tariffs"))
                .andExpect(model().attributeDoesNotExist("validation"));
    }

    @Test
    void updateTariff_NotValidated_Test() throws Exception {
        this.mockMvc.perform(post("/admin/tariffs/update/" + testTariff.getId())
                        .with(csrf())
                        .flashAttr("tariff", new Tariff())
                        .flashAttr("auth_admin", testUser))
                .andExpect(status().isOk())
                .andExpect(model().attribute("validation", "failed"));
    }

    @Test
    void updateTariff_Validated_Test() throws Exception {
        this.mockMvc.perform(post("/admin/tariffs/update/" + testTariff.getId())
                        .with(csrf())
                        .flashAttr("tariff", testTariff)
                        .flashAttr("auth_admin", testUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/tariffs"))
                .andExpect(model().attributeDoesNotExist("validation"));
    }

    @Test
    void showTariffUpdatePageTest() throws Exception {
        Long id = testUser.getId();
        this.mockMvc.perform(get("/admin/tariffs/update/" + id).flashAttr("auth_admin", testUser)).andExpect(status().isOk());
    }

    @Test
    void tariffDeleteTest() throws Exception {
        this.mockMvc.perform(get("/admin/tariffs/delete/"+testTariff.getId()).with(csrf()).flashAttr("auth_admin",testUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/tariffs"));
    }

    @Test
    void getTariffsAJAX_WithoutParameters_Test() throws Exception {
        this.mockMvc.perform(get("/admin/tariffs/get-components").with(csrf()).flashAttr("auth_admin",testUser))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getTariffsAJAX_WithIncorrectParameters_Test() throws Exception {
        this.mockMvc.perform(get("/admin/tariffs/get-components?tariff_id=ff").with(csrf()).flashAttr("auth_admin",testUser))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getTariffsAJAX_WithCorrectParameters_Test() throws Exception {
        this.mockMvc.perform(get("/admin/tariffs/get-components?tariff_id=1").with(csrf()).flashAttr("auth_admin",testUser))
                .andExpect(status().isOk())
                .andExpect(content().string(jsonMapper.writeValueAsString(testMap2)));
    }
}
