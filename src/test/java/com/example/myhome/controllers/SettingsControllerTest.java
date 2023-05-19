package com.example.myhome.controllers;

import com.example.myhome.config.TestConfig;
import com.example.myhome.controller.SettingsController;
import com.example.myhome.model.*;
import com.example.myhome.repository.ServiceRepository;
import com.example.myhome.service.SettingsService;
import com.example.myhome.service.UserRoleService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.ConstraintViolation;
import java.util.ArrayList;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = TestConfig.class)
@AutoConfigureMockMvc
@WithUserDetails("test")
public class SettingsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Admin testUser;

    @Autowired
    private LocalValidatorFactoryBean validator;

    @Autowired
    private SettingsController controller;

    @MockBean
    private SettingsService service;

    @MockBean
    private UserRoleService userRoleService;

    @Autowired
    private ServiceRepository repository;

    static IncomeExpenseItems testItem;
    static PaymentDetails testPaymentDetails;

    @BeforeAll
    static void setupObjects() {
        testItem = new IncomeExpenseItems();
        testItem.setId(1L);
        testItem.setName("test");
        testItem.setIncomeExpenseType(IncomeExpenseType.INCOME);
        testItem.setTransactions(new ArrayList<>());

        testPaymentDetails = new PaymentDetails();
        testPaymentDetails.setId(1L);
        testPaymentDetails.setName("test");
        testPaymentDetails.setDescription("test");
    }

    @BeforeEach
    void setupMocks(){
    }

    @Test
    void contextLoads() {
        assertThat(controller).isNotNull();
        assertThat(repository).isNotNull();
        assertThat(mockMvc).isNotNull();

        assertThat(testUser).isNotNull();
    }

    @Test
    void showStatsPageTest() throws Exception {
        this.mockMvc.perform(get("/").flashAttr("auth_admin", testUser)).andExpect(status().isOk());
    }

    @Test
    void showTransactionsItemsPageTest() throws Exception {
        this.mockMvc.perform(get("/admin/income-expense/").flashAttr("auth_admin", testUser)).andExpect(status().isOk());
    }

    @Test
    void showTransactionItemCreatePageTest() throws Exception {
        this.mockMvc.perform(get("/admin/income-expense/create").flashAttr("auth_admin", testUser)).andExpect(status().isOk());
    }

    @Test
    void showTransactionItemUpdatePageTest() throws Exception {
        Long id = testUser.getId();
        this.mockMvc.perform(get("/admin/income-expense/update/" + id).flashAttr("auth_admin", testUser)).andExpect(status().isOk());
    }

    @Test
    void createTransactionItemTest() throws Exception {
        this.mockMvc.perform(post("/admin/income-expense/create")
                        .with(csrf())
                        .flashAttr("item", testItem)
                        .flashAttr("auth_admin",testUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/income-expense"));
    }

    @Test
    void validationTransactionItemTest() throws Exception {
        Set<ConstraintViolation<IncomeExpenseItems>> violations = validator.validate(new IncomeExpenseItems());
        assertThat(violations.size()).isEqualTo(1);

        this.mockMvc.perform(post("/admin/income-expense/create")
                            .with(csrf())
                            .flashAttr("item", new IncomeExpenseItems())
                            .flashAttr("auth_admin", testUser))
                .andExpect(status().isOk())
                .andExpect(model().attribute("validation", "failed"));

        this.mockMvc.perform(post("/admin/income-expense/update/"+testItem.getId())
                        .with(csrf())
                        .flashAttr("item", new IncomeExpenseItems())
                        .flashAttr("auth_admin", testUser))
                .andExpect(status().isOk())
                .andExpect(model().attribute("validation", "failed"));
    }

    @Test
    void showPaymentDetailsPageTest() throws Exception {
        this.mockMvc.perform(get("/admin/payment-details").flashAttr("auth_admin", testUser)).andExpect(status().isOk());
    }

    @Test
    void updatePaymentDetailsTest() throws Exception {
        this.mockMvc.perform(post("/admin/payment-details")
                        .with(csrf())
                        .flashAttr("details", testPaymentDetails)
                        .flashAttr("auth_admin", testUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(model().attributeDoesNotExist("validation"))
                .andExpect(redirectedUrl("/admin/payment-details"));
    }

    @Test
    void validationPaymentDetailsTest() throws Exception {
        Set<ConstraintViolation<PaymentDetails>> violations = validator.validate(new PaymentDetails());
        assertThat(violations.size()).isEqualTo(2);

        this.mockMvc.perform(post("/admin/payment-details")
                .with(csrf())
                .flashAttr("details", new PaymentDetails())
                .flashAttr("auth_admin", testUser))
                .andExpect(status().isOk())
                .andExpect(model().attribute("validation", "failed"));
    }

    @Test
    void showRolesPageTest() throws Exception {
        this.mockMvc.perform(get("/admin/roles").flashAttr("auth_admin", testUser)).andExpect(status().isOk());
    }

    @Test
    void updateRolesPageTest() throws Exception {
        this.mockMvc.perform(post("/admin/roles")
                .with(csrf())
                .flashAttr("pageForm", new PageRoleForm())
                .flashAttr("auth_admin", testUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/roles"));
    }
}
