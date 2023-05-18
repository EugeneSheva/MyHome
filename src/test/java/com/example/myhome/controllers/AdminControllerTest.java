package com.example.myhome.controllers;

import com.example.myhome.config.TestConfig;
import com.example.myhome.controller.AdminController;
import com.example.myhome.model.Admin;
import com.example.myhome.repository.AdminRepository;
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
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Admin testUser;

    @Autowired
    private AdminController controller;

    @Autowired
    private AdminRepository repository;

    @BeforeEach
    void createUser(){
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
    void showAdminsPageTest() throws Exception {
        this.mockMvc.perform(get("/admin/admins").flashAttr("auth_admin", testUser)).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("test")
    void showAdminInfoPageTest() throws Exception {
        Long id = testUser.getId();
        this.mockMvc.perform(get("/admin/admins/" + id).flashAttr("auth_admin", testUser)).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("test")
    void showAdminCreatePageTest() throws Exception {
        this.mockMvc.perform(get("/admin/admins/create").flashAttr("auth_admin", testUser)).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("test")
    void showAdminUpdatePageTest() throws Exception {
        Long id = testUser.getId();
        this.mockMvc.perform(get("/admin/admins/update/" + id).flashAttr("auth_admin", testUser)).andExpect(status().isOk());
    }
}
