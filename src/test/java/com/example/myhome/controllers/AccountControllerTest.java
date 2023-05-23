package com.example.myhome.controllers;

import com.example.myhome.config.TestConfig;
import com.example.myhome.controller.AccountController;
import com.example.myhome.controller.socket.WebsocketController;
import com.example.myhome.dto.ApartmentAccountDTO;
import com.example.myhome.dto.BuildingDTO;
import com.example.myhome.mapper.AccountDTOMapper;
import com.example.myhome.model.ApartmentAccount;
import com.example.myhome.model.Building;
import com.example.myhome.model.Owner;
import com.example.myhome.model.filter.FilterForm;
import com.example.myhome.repository.AccountRepository;
import com.example.myhome.service.AccountService;
import com.example.myhome.service.BuildingService;
import com.example.myhome.validator.AccountValidator;
import lombok.With;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TestConfig.class)
@AutoConfigureMockMvc
@WithUserDetails("test")
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserDetails testUser;

    @Autowired
    private AccountController controller;

    @Autowired
    private AccountRepository repository;

    @MockBean
    BuildingService buildingService;
    @MockBean
    AccountService accountService;
    @MockBean
    WebsocketController websocketController;

    @MockBean
    AccountValidator validator;

    ApartmentAccountDTO testDTO;
    ApartmentAccount testAccount;
    AccountDTOMapper mapper = new AccountDTOMapper();

    @BeforeEach
    void setupAccount() {
        testAccount = new ApartmentAccount();
        testAccount.setId(1L);
        testAccount.setBuilding(new Building());
        testAccount.setOwner(new Owner());
        testAccount.setBalance(1000.0);
        testAccount.setSection("test");
        testAccount.setIsActive(true);
        testDTO = mapper.fromAccountToDTO(testAccount);

        when(buildingService.findBuildingDTObyId(anyLong())).thenReturn(new BuildingDTO());
        when(accountService.getMaxAccountId()).thenReturn(0L);
        when(buildingService.findAllDTO()).thenReturn(List.of(new BuildingDTO()));
        when(accountService.saveAccount(testDTO)).thenReturn(testAccount);
        when(accountService.getAccountNumberFromFlat(anyLong())).thenReturn(testAccount);
        when(accountService.findAllAccountsByFiltersAndPage(any(FilterForm.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(testDTO), PageRequest.of(1,1),1));
        when(accountService.findAccountDTOById(anyLong())).thenReturn(testDTO);
        when(accountService.findAccountById(anyLong())).thenReturn(testAccount);
    }

    @Test
    public void contextLoads() {
        assertThat(controller).isNotNull();
        assertThat(repository).isNotNull();
        assertThat(mockMvc).isNotNull();

        assertThat(testUser).isNotNull();
    }

    @Test
    void showAccountsPageTest() throws Exception {
        this.mockMvc.perform(get("/admin/accounts").flashAttr("auth_admin", testUser)).andExpect(status().isOk());
    }

    @Test
    void showAccountInfoPageTest() throws Exception {
        Long id = repository.getMaxId().orElse(0L);
        this.mockMvc.perform(get("/admin/accounts/" + id).flashAttr("auth_admin", testUser)).andExpect(status().isOk());
    }

    @Test
    void showAccountCreatePageTest() throws Exception {
        this.mockMvc.perform(get("/admin/accounts/create").flashAttr("auth_admin", testUser)).andExpect(status().isOk());
    }

    @Test
    void showAccountUpdatePageTest() throws Exception {
        Long id = repository.getMaxId().orElse(0L);
        this.mockMvc.perform(get("/admin/accounts/update/" + id).flashAttr("auth_admin", testUser)).andExpect(status().isOk());
    }

    @Test
    void createAccountTest() throws Exception {
        MvcResult result = this.mockMvc.perform(post("/admin/accounts/create").with(csrf().asHeader()).flashAttr("auth_admin", testUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/accounts"))
                .andReturn();
    }

    @Test
    void updateAccountTest() throws Exception {
        MvcResult result = this.mockMvc.perform(post("/admin/accounts/update/" + testAccount.getId()).with(csrf().asHeader()).flashAttr("auth_admin", testUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/accounts"))
                .andReturn();
    }

    @Test
    void deleteAccountTest() throws Exception {
        MvcResult result = this.mockMvc.perform(get("/admin/accounts/delete/" + testAccount.getId()).with(csrf().asHeader()).flashAttr("auth_admin", testUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/accounts"))
                .andReturn();
    }

    @Test
    void getAccountNumberFromFlatIdTest() throws Exception {
        long flat_id = new Random().nextLong();
        MvcResult result = this.mockMvc.perform(get("/admin/accounts/get-flat-account?flat_id=" + flat_id).flashAttr("auth_admin", testUser))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(result.getResponse().getContentAsString()).isEqualTo(String.valueOf(testAccount.getId()));
    }

    @Test
    void getAccountsAJAX_WithoutParameters_Test() throws Exception {
        this.mockMvc.perform(get("/admin/accounts/get-accounts").flashAttr("auth_admin", testUser))
                .andExpect(status().isBadRequest());
        this.mockMvc.perform(get("/admin/accounts/get-accounts?page=1").flashAttr("auth_admin", testUser))
                .andExpect(status().isBadRequest());
        this.mockMvc.perform(get("/admin/accounts/get-accounts?page=1&size=1").flashAttr("auth_admin", testUser))
                .andExpect(status().isBadRequest());
        this.mockMvc.perform(get("/admin/accounts/get-accounts?size=1&filters=").flashAttr("auth_admin", testUser))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAccountsAJAX_WithIncorrectFilters_Test() throws Exception {
        this.mockMvc.perform(get("/admin/accounts/get-accounts?page=1&size=1&filters=ADads").flashAttr("auth_admin", testUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/500"));
    }

    @Test
    void getAccountsAJAX_WithCorrectParameters_Test() throws Exception {
        MvcResult result = this.mockMvc.perform(get("/admin/accounts/get-accounts?page=1&size=1&filters=null").flashAttr("auth_admin", testUser))
                .andExpect(status().isOk())
                .andReturn();
        System.out.println(result.getResponse().getContentAsString());
    }
}
