package com.example.myhome.controllers;

import com.example.myhome.config.TestConfig;
import com.example.myhome.controller.ServiceController;
import com.example.myhome.model.Admin;
import com.example.myhome.repository.ServiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TestConfig.class)
@AutoConfigureMockMvc
public class ServiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Admin testUser;

    @Autowired
    private ServiceController controller;

    @Autowired
    private ServiceRepository repository;

    @BeforeEach
    void create(){
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
    void showServicesPageTest() throws Exception {
        this.mockMvc.perform(get("/admin/services").flashAttr("auth_admin", testUser)).andExpect(status().isOk());
    }


}
