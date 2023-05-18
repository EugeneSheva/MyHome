package com.example.myhome.controllers;

import com.example.myhome.config.TestConfig;
import com.example.myhome.controller.WebsiteController;
import com.example.myhome.model.Admin;
import com.example.myhome.repository.ServiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TestConfig.class)
@AutoConfigureMockMvc
public class WebsiteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Admin testUser;

    @Autowired
    private WebsiteController controller;

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
    void showMainPageEditTest() throws Exception {
        this.mockMvc.perform(get("/admin/website/home").flashAttr("auth_admin", testUser)).andExpect(status().isOk());
    }

    @Test
    @Transactional
    @WithUserDetails("test")
    void showAboutPageEditTest() throws Exception {
        this.mockMvc.perform(get("/admin/website/about").flashAttr("auth_admin", testUser)).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("test")
    void showServicesPageEditTest() throws Exception {
        this.mockMvc.perform(get("/admin/website/services").flashAttr("auth_admin", testUser)).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("test")
    void showContactsPageEditTest() throws Exception {
        this.mockMvc.perform(get("/admin/website/contacts").flashAttr("auth_admin", testUser)).andExpect(status().isOk());
    }


}
