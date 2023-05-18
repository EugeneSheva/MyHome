package com.example.myhome.controllers;

import com.example.myhome.config.TestConfig;
import com.example.myhome.controller.SettingsController;
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
public class SettingsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Admin testUser;

    @Autowired
    private SettingsController controller;

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
    void showStatsPageTest() throws Exception {
        this.mockMvc.perform(get("/").flashAttr("auth_admin", testUser)).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("test")
    void showTransactionsPageTest() throws Exception {
        this.mockMvc.perform(get("/admin/income-expense/").flashAttr("auth_admin", testUser)).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("test")
    void showTransactionCreatePageTest() throws Exception {
        this.mockMvc.perform(get("/admin/income-expense/create").flashAttr("auth_admin", testUser)).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("test")
    void showTransactionUpdatePageTest() throws Exception {
        Long id = testUser.getId();
        this.mockMvc.perform(get("/admin/income-expense/update/" + id).flashAttr("auth_admin", testUser)).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("test")
    void showPaymentDetailsPageTest() throws Exception {
        this.mockMvc.perform(get("/admin/payment-details").flashAttr("auth_admin", testUser)).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("test")
    void showRolesPageTest() throws Exception {
        this.mockMvc.perform(get("/admin/roles").flashAttr("auth_admin", testUser)).andExpect(status().isOk());
    }
}
