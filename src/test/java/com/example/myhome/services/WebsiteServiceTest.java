package com.example.myhome.services;

import com.example.myhome.model.pages.AboutPage;
import com.example.myhome.model.pages.ContactsPage;
import com.example.myhome.model.pages.MainPage;
import com.example.myhome.model.pages.ServicesPage;
import com.example.myhome.repository.DocumentRepository;
import com.example.myhome.repository.PageRepository;
import com.example.myhome.service.WebsiteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@SpringBootTest
class WebsiteServiceTest {

    @MockBean PageRepository repository;
    @MockBean DocumentRepository documentRepository;
    @Autowired WebsiteService service;

    @Test
    void getMainPageTest() {
        MainPage expected = new MainPage();
        expected.setId(10L);
        given(repository.getMainPage()).willReturn(Optional.of(expected));
        MainPage page = service.getMainPage();
        verify(repository.getMainPage());
        assertThat(page.getId()).isEqualTo(10L);
    }

    @Test
    void getAboutPageTest() {
        AboutPage expected = new AboutPage();
        expected.setId(10L);
        given(repository.getAboutPage()).willReturn(Optional.of(expected));
        AboutPage page = service.getAboutPage();
        verify(repository.getAboutPage());
        assertThat(page.getId()).isEqualTo(10L);
    }

    @Test
    void getServicesPageTest() {
        ServicesPage expected = new ServicesPage();
        expected.setId(10L);
        given(repository.getServicesPage()).willReturn(Optional.of(expected));
        ServicesPage page = service.getServicesPage();
        verify(repository.getServicesPage());
        assertThat(page.getId()).isEqualTo(10L);
    }

    @Test
    void getContactsPageTest() {
        ContactsPage expected = new ContactsPage();
        expected.setId(10L);
        given(repository.getContactsPage()).willReturn(Optional.of(expected));
        ContactsPage page = service.getContactsPage();
        verify(repository.getContactsPage());
        assertThat(page.getId()).isEqualTo(10L);
    }

    @Test
    void savePageTest() {
        MainPage page = new MainPage();
        page.setId(1L);

        given(repository.save(any(MainPage.class))).willReturn(page);

        MainPage testPage = new MainPage();
        testPage.setId(0L);

        testPage = (MainPage) service.savePage(testPage);

        verify(repository).save(testPage);

        assertThat(testPage.getId()).isEqualTo(1L);
    }

    @Test
    void getAllDocumentsTest() {
        List<AboutPage.Document> documents = List.of(new AboutPage.Document(), new AboutPage.Document(), new AboutPage.Document());

        given(documentRepository.findAll()).willReturn(documents);

        List<AboutPage.Document> testList = service.getAllDocuments();

        verify(documentRepository).findAll();

        assertThat(testList.size()).isEqualTo(3);
    }


    @Test
    void saveMainPageImagesTest() throws IOException {
        MainPage page = new MainPage();
        page.setId(1L);

        MultipartFile testMultipartFile = new MockMultipartFile("file", "test.txt", MediaType.TEXT_PLAIN_VALUE, "test".getBytes());

        given(repository.getMainPage()).willReturn(Optional.of(page));

        service.saveMainPageImages(page, testMultipartFile, testMultipartFile, testMultipartFile, testMultipartFile, testMultipartFile, testMultipartFile, testMultipartFile, testMultipartFile, testMultipartFile);

        assertThat(page.getSlide1()).isEqualTo(testMultipartFile.getOriginalFilename());
        assertThat(page.getSlide2()).isEqualTo(testMultipartFile.getOriginalFilename());
        assertThat(page.getSlide3()).isEqualTo(testMultipartFile.getOriginalFilename());
        assertThat(page.getBlock_1_img()).isEqualTo(testMultipartFile.getOriginalFilename());
        assertThat(page.getBlock_2_img()).isEqualTo(testMultipartFile.getOriginalFilename());
        assertThat(page.getBlock_3_img()).isEqualTo(testMultipartFile.getOriginalFilename());
        assertThat(page.getBlock_4_img()).isEqualTo(testMultipartFile.getOriginalFilename());
        assertThat(page.getBlock_5_img()).isEqualTo(testMultipartFile.getOriginalFilename());
        assertThat(page.getBlock_6_img()).isEqualTo(testMultipartFile.getOriginalFilename());
    }

    @Test
    void doesntSaveEmptyFiles() throws IOException {
        MainPage page = new MainPage();
        page.setId(1L);

        MultipartFile testMultipartFile = new MockMultipartFile("file", "test.txt", MediaType.TEXT_PLAIN_VALUE, new byte[0]);

        given(repository.getMainPage()).willReturn(Optional.of(page));

        service.saveMainPageImages(page, testMultipartFile, testMultipartFile, testMultipartFile, testMultipartFile, testMultipartFile, testMultipartFile, testMultipartFile, testMultipartFile, testMultipartFile);

        assertThat(page.getSlide1()).isNull();
        assertThat(page.getSlide2()).isNull();
        assertThat(page.getSlide3()).isNull();
        assertThat(page.getBlock_1_img()).isNull();
        assertThat(page.getBlock_2_img()).isNull();
        assertThat(page.getBlock_3_img()).isNull();
        assertThat(page.getBlock_4_img()).isNull();
        assertThat(page.getBlock_5_img()).isNull();
        assertThat(page.getBlock_6_img()).isNull();
    }

    @Test
    void saveAboutPageInfo() {
    }

    @Test
    void saveServicesPageInfo() {
    }

}