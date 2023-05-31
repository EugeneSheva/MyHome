package com.example.myhome.controllers;

import com.example.myhome.controller.socket.WebsocketController;
import com.example.myhome.mapper.AccountDTOMapper;
import com.example.myhome.model.Admin;
import com.example.myhome.repository.AccountRepository;
import com.example.myhome.repository.CashBoxRepository;
import com.example.myhome.repository.IncomeExpenseRepository;
import com.example.myhome.repository.OwnerRepository;
import com.example.myhome.service.AccountService;
import com.example.myhome.service.AdminService;
import com.example.myhome.service.CashBoxService;
import com.example.myhome.service.OwnerService;
import com.example.myhome.service.impl.IncomeExpenseItemServiceImpl;
import com.example.myhome.validator.CashBoxtValidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@WithUserDetails("test")
public class CashboxControllerTest {

    @MockBean private CashBoxService cashBoxService;
    @MockBean private OwnerService ownerService;
    @MockBean private OwnerRepository ownerRepository;
    @MockBean private AdminService adminService;
    @MockBean private AccountService accountService;
    @MockBean private IncomeExpenseItemServiceImpl incomeExpenseItemService;
    @MockBean private IncomeExpenseRepository incomeExpenseRepository;
    @MockBean private AccountRepository accountRepository;
    @MockBean private CashBoxRepository cashBoxRepository;
    @Autowired private CashBoxtValidator cashBoxtValidator;

    @MockBean private WebsocketController websocketController;

    @Autowired private AccountDTOMapper accountDTOMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Admin testUser;

    @Test
    void contextLoads() {

    }

    @Test
    void openAllCashboxesPageTest() throws Exception {
        this.mockMvc.perform(get("/admin/cashbox").with(csrf()).flashAttr("auth_admin",testUser))
                .andExpect(status().isOk());
    }

    @Test
    void openShowIncomePageTest() throws Exception {
        this.mockMvc.perform(get("/admin/cashbox/show-incomes")
                        .with(csrf())
                        .param("account_id", "1")
                        .flashAttr("auth_admin",testUser))
                .andExpect(status().isOk());
    }

    @Test
    void openCashboxPageTest() throws Exception {
        this.mockMvc.perform(get("/admin/cashbox/1").with(csrf()).flashAttr("auth_admin",testUser))
                .andExpect(status().isOk());
    }

    @Test
    void openNewIncomePageTest() throws Exception {
        this.mockMvc.perform(get("/admin/cashbox/newIncome").with(csrf()).flashAttr("auth_admin",testUser))
                .andExpect(status().isOk());
    }

    @Test
    void openNewExpensePageTest() throws Exception {
        this.mockMvc.perform(get("/admin/cashbox/newExpense").with(csrf()).flashAttr("auth_admin",testUser))
                .andExpect(status().isOk());
    }

    @Test
    void openCashboxEditPageTest() throws Exception {
        this.mockMvc.perform(get("/admin/cashbox/edit/1").with(csrf()).flashAttr("auth_admin",testUser))
                .andExpect(status().isOk());
    }

    @Test
    void openCashboxCopyPageTest() throws Exception {
        this.mockMvc.perform(get("/admin/cashbox/copy/1").with(csrf()).flashAttr("auth_admin",testUser))
                .andExpect(status().isOk());
    }

    @Test
    void saveCashboxTest() throws Exception {

    }

    @Test
    void saveNewIncomeTest() throws Exception {

    }

    @Test
    void saveNewExpenseTest() throws Exception {

    }

    @Test
    void saveEditedCashboxTest() throws Exception {

    }

    @Test
    void deleteCashboxTest() throws Exception {
        this.mockMvc.perform(get("/admin/cashbox/delete/1").with(csrf()).flashAttr("auth_admin",testUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/cashbox"));
    }

    @Test
    void getUsersAJAXTest() throws Exception {
        this.mockMvc.perform(get("/admin/cashbox/getUsers")
                        .with(csrf())
                        .flashAttr("auth_admin",testUser))
                .andExpect(status().isOk());
    }

    @Test
    void getCashboxAJAXTest() throws Exception {
        this.mockMvc.perform(get("/admin/cashbox/get-cashbox-page")
                        .with(csrf())
                        .param("page","1")
                        .param("size","1")
                        .param("filters", (String) null)
                        .flashAttr("auth_admin",testUser))
                .andExpect(status().isOk());
    }

    @Test
    void getCashboxBalanceAJAXTest() throws Exception {
        MvcResult result = this.mockMvc.perform(get("/admin/cashbox/get-cashbox-balance").with(csrf()).flashAttr("auth_admin",testUser))
                .andReturn();
        assertThat(result.getResponse().getContentAsString()).isEqualTo("100.00");
    }

    @Test
    void getAccountBalanceAJAXTest() throws Exception {
        MvcResult result = this.mockMvc.perform(get("/admin/cashbox/get-account-balance").with(csrf()).flashAttr("auth_admin",testUser))
                .andReturn();
        assertThat(result.getResponse().getContentAsString()).isEqualTo("100.00");
    }

    @Test
    void getAccountDebtsAJAXTest() throws Exception {
        MvcResult result = this.mockMvc.perform(get("/admin/cashbox/get-account-debts").with(csrf()).flashAttr("auth_admin",testUser))
                .andReturn();
        assertThat(result.getResponse().getContentAsString()).isEqualTo("100.00");
    }
}
