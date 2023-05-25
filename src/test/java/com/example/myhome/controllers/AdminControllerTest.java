package com.example.myhome.controllers;

import com.example.myhome.config.TestConfig;
import com.example.myhome.controller.AdminController;
import com.example.myhome.dto.AdminDTO;
import com.example.myhome.mapper.AdminDTOMapper;
import com.example.myhome.model.Admin;
import com.example.myhome.model.filter.FilterForm;
import com.example.myhome.repository.AdminRepository;
import com.example.myhome.service.AdminService;
import com.example.myhome.service.EmailService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.filter;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = TestConfig.class)
@AutoConfigureMockMvc
@WithUserDetails("test")
public class AdminControllerTest {

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Admin testUser;

    @Autowired
    private AdminController controller;

    @MockBean
    private AdminService adminService;

    @MockBean
    private EmailService emailService;

    @MockBean
    private AdminRepository repository;

    AdminDTO testDTO;
    AdminDTOMapper mapper = new AdminDTOMapper();
    ObjectMapper jsonMapper = new ObjectMapper();
    String jsonString = "";
    Map<String, Object> testMap;

    @BeforeEach
    void createUser() throws IllegalAccessException, JsonProcessingException {
        testDTO = mapper.fromAdminToDTO(testUser);
        testDTO.setPassword("test");
        testDTO.setConfirm_password("test");
        testDTO.setFirst_name("test");
        testDTO.setLast_name("test");
        testDTO.setPhone_number("+380997524927");
        testDTO.setEmail("test@gmail.com");

        List<AdminDTO> list = List.of(testDTO, testDTO);

        when(repository.existsByEmail(any(String.class))).thenReturn(true);
        when(adminService.saveAdmin(any(Admin.class))).thenReturn(testUser);
        when(adminService.findAdminDTOById(anyLong())).thenReturn(testDTO);
        when(adminService.findAdminById(anyLong())).thenReturn(testUser);
        when(adminService.findAllByFiltersAndPage(any(FilterForm.class), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(testDTO), PageRequest.of(1,1), 1));
        when(adminService.findAllBySpecification(any(FilterForm.class), anyInt(), anyInt())).thenReturn(new PageImpl<>(List.of(testDTO), PageRequest.of(1,1), 1));
        when(adminService.countAllManagers()).thenReturn(10L);
        when(adminService.countAllMasters()).thenReturn(10L);
        when(adminService.findAllMasters()).thenReturn(list);
        when(adminService.findAllMasters(anyString(), anyInt())).thenReturn(list);
        when(adminService.findAllManagers()).thenReturn(list);
        when(adminService.findAllManagers(anyString(), anyInt())).thenReturn(list);

        testMap = new HashMap<>();
        Map<String, Boolean> pagination = new HashMap<>();
        pagination.put("more", false);
        testMap.put("results", adminService.findAllMasters("search", 1));
        testMap.put("pagination", pagination);

        jsonString = jsonMapper.writeValueAsString(testMap);
    }

    @Test
    void contextLoads() {
        assertThat(controller).isNotNull();
        assertThat(repository).isNotNull();
        assertThat(mockMvc).isNotNull();

        assertThat(testUser).isNotNull();
    }

    @Test
    void showAdminsPageTest() throws Exception {
        this.mockMvc.perform(get("/admin/admins").flashAttr("auth_admin", testUser)).andExpect(status().isOk());
    }

    @Test
    void showAdminInfoPageTest() throws Exception {
        Long id = testUser.getId();
        this.mockMvc.perform(get("/admin/admins/" + id).flashAttr("auth_admin", testUser)).andExpect(status().isOk());
    }

    @Test
    void showAdminCreatePageTest() throws Exception {
        this.mockMvc.perform(get("/admin/admins/create").flashAttr("auth_admin", testUser)).andExpect(status().isOk());
    }

    @Test
    void showAdminUpdatePageTest() throws Exception {
        Long id = testUser.getId();
        this.mockMvc.perform(get("/admin/admins/update/" + id).flashAttr("auth_admin", testUser)).andExpect(status().isOk());
    }

    @Test
    void createAdmin_NotValidated_Test() throws Exception {
        this.mockMvc.perform(post("/admin/admins/create").param("dto", testDTO.toString()).with(csrf()).flashAttr("auth_admin", testUser))
                .andExpect(status().isOk())
                .andExpect(model().attribute("validation", equalTo("failed")));
    }

    @Test
    void createAdmin_Validated_Test() throws Exception {
        Model model = new ConcurrentModel();
        String result = controller.createAdmin(testDTO, new BeanPropertyBindingResult(testDTO, "testDTO"), model);
        assertThat(model.getAttribute("validation")).isEqualTo("passed");
        assertThat(result).isEqualTo("redirect:/admin/admins");
    }

    @Test
    void updateAdmin_NotValidated_Test() throws Exception {
        this.mockMvc.perform(post("/admin/admins/update/" + testDTO.getId()).param("dto", testDTO.toString()).with(csrf()).flashAttr("auth_admin", testUser))
                .andExpect(status().isOk())
                .andExpect(model().attribute("validation", equalTo("failed")));
    }

    @Test
    void updateAdmin_Validated_Test() throws Exception {
        Model model = new ConcurrentModel();
        String result = controller.updateAdmin(testDTO.getId(),testDTO, new BeanPropertyBindingResult(testDTO, "testDTO"), model);
        assertThat(model.getAttribute("validation")).isEqualTo("passed");
        assertThat(result).isEqualTo("redirect:/admin/admins");
    }

    @Test
    void deleteAdminTest() throws Exception {
        this.mockMvc.perform(get("/admin/admins/delete/" + testDTO.getId()).with(csrf()).flashAttr("auth_admin", testUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/admins"));
    }

    @Test
    void inviteAdminTest() throws Exception {
        MvcResult result =this.mockMvc.perform(get("/admin/admins/invite/" + testDTO.getId()).with(csrf()).flashAttr("auth_admin", testUser))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(result.getResponse().getContentAsString()).isEqualTo("User with ID " + testDTO.getId() + " - invited");
    }

    @Test
    void getAllMastersTest() throws Exception {
        this.mockMvc.perform(get("/admin/admins/get-all-masters")
                .with(csrf())
                    .param("search","test")
                    .param("page","1")
                .flashAttr("auth_admin", testUser))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonString));
    }

    @Test
    void getManagersTest() throws Exception {
        this.mockMvc.perform(get("/admin/admins/get-managers")
                        .with(csrf())
                        .param("search","test")
                        .param("page","1")
                        .flashAttr("auth_admin", testUser))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonString));
    }

    @Test
    void getAdminsTest() throws Exception {
        FilterForm filterForm = new FilterForm();
        filterForm.setId(1L);
        filterForm.setName("test");
        String jsonString = jsonMapper.writeValueAsString(new PageImpl<>(List.of(testDTO), PageRequest.of(1,1), 1));
        this.mockMvc.perform(get("/admin/admins/get-admins")
                        .with(csrf())
                        .param("page","1")
                        .param("size","1")
                        .param("filters",jsonMapper.writeValueAsString(filterForm))
                        .flashAttr("auth_admin", testUser))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonString));
    }

    @Test
    void getMastersByTypeTest() throws Exception {
        String jsonString = jsonMapper.writeValueAsString(List.of(testDTO, testDTO));
        this.mockMvc.perform(get("/admin/admins/get-masters-by-type")
                        .with(csrf())
                        .param("typeID","0")
                        .flashAttr("auth_admin", testUser))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonString));
    }

}
