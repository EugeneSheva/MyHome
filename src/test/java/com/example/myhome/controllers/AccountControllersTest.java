//package com.example.myhome.controllers;
//
//import com.example.myhome.home.controller.AccountController;
//import com.example.myhome.home.repository.AccountRepository;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.hamcrest.Matchers.containsString;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//public class AccountControllersTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private AccountController controller;
//
//    @Autowired
//    private AccountRepository repository;
//
//    @Test
//    void contextLoads() {
//        assertThat(controller).isNotNull();
//        assertThat(mockMvc).isNotNull();
//    }
//
//    @Test
//    void showAccountsPageTest() throws Exception {
//        this.mockMvc.perform(get("/admin/accounts").with(anonymous())).andExpect(status().isOk());
//    }
//
//    @Test
//    void showAccountInfoPageTest() throws Exception {
//        Long id = repository.getMaxId().orElse(0L);
//        this.mockMvc.perform(get("/admin/accounts/" + id)).andExpect(status().isOk());
//    }
//
//    @Test
//    void showAccountCreatePageTest() throws Exception {
//        this.mockMvc.perform(get("/admin/accounts/create")).andExpect(status().isOk());
//    }
//
//    @Test
//    void showAccountUpdatePageTest() throws Exception {
//        Long id = repository.getMaxId().orElse(0L);
//        this.mockMvc.perform(get("/admin/accounts/update/" + id)).andExpect(status().isOk());
//    }
//}
