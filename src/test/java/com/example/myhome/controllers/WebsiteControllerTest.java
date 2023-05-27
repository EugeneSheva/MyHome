package com.example.myhome.controllers;

import com.example.myhome.config.TestConfig;
import com.example.myhome.controller.WebsiteController;
import com.example.myhome.model.Admin;
import com.example.myhome.model.pages.AboutPage;
import com.example.myhome.model.pages.ContactsPage;
import com.example.myhome.model.pages.MainPage;
import com.example.myhome.model.pages.ServicesPage;
import com.example.myhome.repository.ServiceRepository;
import com.example.myhome.service.WebsiteService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.webservices.server.WebServiceServerTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.ConstraintViolation;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = TestConfig.class)
@AutoConfigureMockMvc
@WithUserDetails("test")
public class WebsiteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Admin testUser;

    @Autowired
    private WebsiteController controller;

    @MockBean
    private WebsiteService service;

    @Autowired
    private ServiceRepository repository;

    @Autowired
    private LocalValidatorFactoryBean validator;

    static MainPage testMainPage;
    static AboutPage testAboutPage;
    static ServicesPage testServicesPage;
    static ContactsPage testContactsPage;

    @BeforeAll
    static void setupObjects() {
        testMainPage = new MainPage();
        testAboutPage = new AboutPage();
        testServicesPage = new ServicesPage();
        testContactsPage = new ContactsPage();

        testMainPage.setTitle("test");
        testMainPage.setDescription("test");
        testMainPage.setBlock_1_img("test");
        testMainPage.setBlock_2_img("test");
        testMainPage.setBlock_3_img("test");
        testMainPage.setBlock_4_img("test");
        testMainPage.setBlock_5_img("test");
        testMainPage.setBlock_6_img("test");
        testMainPage.setBlock_1_title("test");
        testMainPage.setBlock_2_title("test");
        testMainPage.setBlock_3_title("test");
        testMainPage.setBlock_4_title("test");
        testMainPage.setBlock_5_title("test");
        testMainPage.setBlock_6_title("test");
        testMainPage.setBlock_1_description("test");
        testMainPage.setBlock_2_description("test");
        testMainPage.setBlock_3_description("test");
        testMainPage.setBlock_4_description("test");
        testMainPage.setBlock_5_description("test");
        testMainPage.setBlock_6_description("test");

        testAboutPage.setTitle("test");
        testAboutPage.setDescription("test");
        testAboutPage.setAdd_title("test");
        testAboutPage.setAdd_description("test");

        testContactsPage.setTitle("test");
        testContactsPage.setDescription("test");
        testContactsPage.setWebsite_link("test");
        testContactsPage.setName("test");
        testContactsPage.setLocation("test");
        testContactsPage.setAddress("test");
        testContactsPage.setPhone("test");
        testContactsPage.setEmail("test");
        testContactsPage.setMap_code("test");

        testServicesPage.setServiceDescriptions(List.of(new ServicesPage.ServiceDescription()));
    }

    @BeforeEach
    void setupMocks(){
        when(service.getMainPage()).thenReturn(testMainPage);
        when(service.getAboutPage()).thenReturn(testAboutPage);
        when(service.getServicesPage()).thenReturn(testServicesPage);
        when(service.getContactsPage()).thenReturn(testContactsPage);
    }

    @Test
    void contextLoads() {
        assertThat(controller).isNotNull();
        assertThat(repository).isNotNull();
        assertThat(mockMvc).isNotNull();

        assertThat(testUser).isNotNull();
    }

    @Test
    void showMainPageEditTest() throws Exception {
        this.mockMvc.perform(get("/admin/website/home").flashAttr("auth_admin", testUser)).andExpect(status().isOk());
    }

    @Test
    @Transactional
    void showAboutPageEditTest() throws Exception {
        this.mockMvc.perform(get("/admin/website/about").flashAttr("aboutPage", testAboutPage).flashAttr("auth_admin", testUser)).andExpect(status().isOk());
    }

    @Test
    void updateMainPageTest() throws Exception {
        this.mockMvc.perform(post("/admin/website/home")
                .with(csrf())
                .flashAttr("mainPage", testMainPage)
                .flashAttr("auth_admin", testUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/website/home"));
    }

    @Test
    void updateAboutPageTest() throws Exception {
        this.mockMvc.perform(post("/admin/website/about")
                        .with(csrf())
                        .flashAttr("aboutPage", testAboutPage)
                        .flashAttr("auth_admin", testUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/website/about"));
    }

    @Test
    void showServicesPageEditTest() throws Exception {
        this.mockMvc.perform(get("/admin/website/services")
                .flashAttr("servicesPage", testServicesPage).flashAttr("auth_admin", testUser)).andExpect(status().isOk());
    }

    @Test
    void updateServicesPageTest() throws Exception {
        MockMultipartFile firstFile = new MockMultipartFile("service_images", "filename.txt", "text/plain", "some xml".getBytes());
        MockMultipartFile secondFile = new MockMultipartFile("service_images", "other-file-name.data", "text/plain", "some other type".getBytes());
        this.mockMvc.perform(MockMvcRequestBuilders.multipart("/admin/website/services")
                        .file(firstFile)
                        .file(secondFile)
                        .param("titles", new String[]{"test"})
                        .param("descriptions", new String[]{"test"})
                        .with(csrf())
                        .flashAttr("servicesPage", testServicesPage)
                        .flashAttr("auth_admin", testUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/website/services"));
    }

    @Test
    void showContactsPageEditTest() throws Exception {
        this.mockMvc.perform(get("/admin/website/contacts").flashAttr("auth_admin", testUser)).andExpect(status().isOk());
    }

    @Test
    void updateContactsPageTest() throws Exception {
        this.mockMvc.perform(post("/admin/website/contacts")
                .with(csrf())
                .flashAttr("contactsPage", testContactsPage)
                .flashAttr("auth_admin", testUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/website/contacts"));
    }

    @Test
    void deleteAboutImageTest() throws Exception {
        this.mockMvc.perform(get("/admin/website/delete-about-image/1").with(csrf()).flashAttr("auth_admin", testUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/website/about"));
    }

    @Test
    void deleteAboutAddImageTest() throws Exception {
        this.mockMvc.perform(get("/admin/website/delete-about-add-image/0").with(csrf()).flashAttr("auth_admin", testUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/website/about"));
    }

    @Test
    void deleteDocumentTest() throws Exception {
        this.mockMvc.perform(get("/admin/website/delete-document/1").with(csrf()).flashAttr("auth_admin",testUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/website/about"));
    }

    @Test
    void pagesValidationTest() throws Exception {
        Set<ConstraintViolation<MainPage>> violations1 = validator.validate(new MainPage());
        Set<ConstraintViolation<AboutPage>> violations2 = validator.validate(new AboutPage());
        Set<ConstraintViolation<ServicesPage>> violations3 = validator.validate(new ServicesPage());
        Set<ConstraintViolation<ContactsPage>> violations4 = validator.validate(new ContactsPage());

        assertThat(violations1.size()).isEqualTo(14);
        assertThat(violations2.size()).isEqualTo(4);
        assertThat(violations3.size()).isEqualTo(1);
        assertThat(violations4.size()).isEqualTo(9);

        this.mockMvc.perform(post("/admin/website/home")
                .with(csrf())
                .flashAttr("mainPage", new MainPage())
                .flashAttr("auth_admin", testUser))
                .andExpect(status().isOk())
                .andExpect(model().attribute("validation", "failed"));

        this.mockMvc.perform(post("/admin/website/about")
                        .with(csrf())
                        .flashAttr("aboutPage", new AboutPage())
                        .flashAttr("auth_admin", testUser))
                .andExpect(status().isOk())
                .andExpect(model().attribute("validation", "failed"));

        MockMultipartFile firstFile = new MockMultipartFile("service_images", "filename.txt", "text/plain", "some xml".getBytes());
        MockMultipartFile secondFile = new MockMultipartFile("service_images", "other-file-name.data", "text/plain", "some other type".getBytes());
        this.mockMvc.perform(MockMvcRequestBuilders.multipart("/admin/website/services")
                        .file(firstFile)
                        .file(secondFile)
                        .param("titles", new String[]{"test"})
                        .param("descriptions", new String[]{"test"})
                        .with(csrf())
                        .flashAttr("servicesPage", new ServicesPage())
                        .flashAttr("auth_admin", testUser))
                .andExpect(status().isOk())
                .andExpect(model().attribute("validation", "failed"));

        this.mockMvc.perform(post("/admin/website/contacts")
                        .with(csrf())
                        .flashAttr("contactsPage", new ContactsPage())
                        .flashAttr("auth_admin", testUser))
                .andExpect(status().isOk())
                .andExpect(model().attribute("validation", "failed"));
    }


}
