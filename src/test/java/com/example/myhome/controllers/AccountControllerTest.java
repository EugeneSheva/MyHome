package com.example.myhome.controllers;

import com.example.myhome.config.TestConfig;
import com.example.myhome.controller.AccountController;
import com.example.myhome.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TestConfig.class)
@AutoConfigureMockMvc
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserDetails testUser;

    @Autowired
    private AccountController controller;

    @Autowired
    private AccountRepository repository;

    @Test
    void contextLoads() {
        assertThat(controller).isNotNull();
        assertThat(repository).isNotNull();
        assertThat(mockMvc).isNotNull();

        assertThat(testUser).isNotNull();
    }

    @Test
    @WithUserDetails("test")
    void showAccountsPageTest() throws Exception {
        this.mockMvc.perform(get("/admin/accounts").flashAttr("auth_admin", testUser)).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("test")
    void showAccountInfoPageTest() throws Exception {
        Long id = repository.getMaxId().orElse(0L);
        this.mockMvc.perform(get("/admin/accounts/" + id).flashAttr("auth_admin", testUser)).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("test")
    void showAccountCreatePageTest() throws Exception {
        this.mockMvc.perform(get("/admin/accounts/create").flashAttr("auth_admin", testUser)).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("test")
    void showAccountUpdatePageTest() throws Exception {
        Long id = repository.getMaxId().orElse(0L);
        this.mockMvc.perform(get("/admin/accounts/update/" + id).flashAttr("auth_admin", testUser)).andExpect(status().isOk());
    }
}
