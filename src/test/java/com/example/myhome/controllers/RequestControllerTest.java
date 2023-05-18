package com.example.myhome.controllers;

import com.example.myhome.config.TestConfig;
import com.example.myhome.controller.RequestController;
import com.example.myhome.model.Admin;
import com.example.myhome.repository.RepairRequestRepository;
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
public class RequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Admin testUser;

    @Autowired
    private RequestController controller;

    @Autowired
    private RepairRequestRepository repository;

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
}
