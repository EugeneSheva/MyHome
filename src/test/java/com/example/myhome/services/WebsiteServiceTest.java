package com.example.myhome.services;

import com.example.myhome.exception.NotFoundException;
import com.example.myhome.model.pages.AboutPage;
import com.example.myhome.model.pages.ContactsPage;
import com.example.myhome.model.pages.MainPage;
import com.example.myhome.model.pages.ServicesPage;
import com.example.myhome.repository.DocumentRepository;
import com.example.myhome.repository.PageRepository;
import com.example.myhome.service.WebsiteService;
import com.example.myhome.util.FileUploadUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@SpringBootTest
class WebsiteServiceTest {

    @MockBean PageRepository repository;
    @MockBean DocumentRepository documentRepository;
    @MockBean
    FileUploadUtil fileUploadUtil;
    @Autowired WebsiteService service;

    @Test
    void getMainPageTest() {
        MainPage expected = new MainPage();
        expected.setId(10L);
        given(repository.getMainPage()).willReturn(Optional.of(expected));
        MainPage page = service.getMainPage();
        verify(repository).getMainPage();
        assertThat(page.getId()).isEqualTo(10L);
    }

    @Test
    void getAboutPageTest() {
        AboutPage expected = new AboutPage();
        expected.setId(10L);
        given(repository.getAboutPage()).willReturn(Optional.of(expected));
        AboutPage page = service.getAboutPage();
        verify(repository).getAboutPage();
        assertThat(page.getId()).isEqualTo(10L);
    }

    @Test
    void getServicesPageTest() {
        ServicesPage expected = new ServicesPage();
        expected.setId(10L);
        given(repository.getServicesPage()).willReturn(Optional.of(expected));
        ServicesPage page = service.getServicesPage();
        verify(repository).getServicesPage();
        assertThat(page.getId()).isEqualTo(10L);
    }

    @Test
    void getContactsPageTest() {
        ContactsPage expected = new ContactsPage();
        expected.setId(10L);
        given(repository.getContactsPage()).willReturn(Optional.of(expected));
        ContactsPage page = service.getContactsPage();
        verify(repository).getContactsPage();
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
    void saveAboutPageInfoTest() throws IOException {
        AboutPage expected = new AboutPage();
        expected.setId(10L);
        expected.setPhotos("test1,test2");
        given(repository.getAboutPage()).willReturn(Optional.of(expected));
        MockMultipartFile testFile = new MockMultipartFile("test", new byte[1024]);

        assertThat(service.saveAboutPageInfo(expected, testFile, new MultipartFile[]{testFile,testFile},
                new MultipartFile[]{testFile,testFile},new String[]{"test1","test2"}, new MultipartFile[]{testFile,testFile}))
                .isNotNull();

    }

    @Test
    void saveAboutPageInfoTest_2() throws IOException {
        AboutPage expected = new AboutPage();
        expected.setId(10L);
        expected.setPhotos("test1,test2");
        given(repository.getAboutPage()).willReturn(Optional.of(expected));
        MockMultipartFile testFile = new MockMultipartFile("test", new byte[1024]);
        MockMultipartFile testFile2 = new MockMultipartFile("test", new byte[0]);

        assertThat(service.saveAboutPageInfo(expected, testFile2, new MultipartFile[]{testFile},
                new MultipartFile[]{testFile},new String[]{"test1","test2"}, new MultipartFile[]{testFile,testFile}))
                .isNotNull();
    }

    @Test
    void saveAboutPageInfoTest_3() throws Exception {
        AboutPage expected = new AboutPage();
        expected.setId(10L);
        expected.setPhotos("test1,test2");
        given(repository.getAboutPage()).willReturn(Optional.of(expected));
        MockMultipartFile testFile = new MockMultipartFile("test", new byte[1024]);
        MockMultipartFile testFile2 = new MockMultipartFile("test", new byte[0]);

        assertThat(service.saveAboutPageInfo(expected, testFile2, new MultipartFile[]{},
                new MultipartFile[]{},new String[]{"test1","test2"}, new MultipartFile[]{testFile,testFile}))
                .isNotNull();
    }

    @Test
    void saveServicesPageInfoTest() {
        ServicesPage expected = new ServicesPage();
        expected.setId(10L);
        given(repository.getServicesPage()).willReturn(Optional.of(expected));
        MockMultipartFile testFile = new MockMultipartFile("test", new byte[1024]);

        assertThat(service.saveServicesPageInfo(expected, new String[]{"test1", "test2"},
                new String[]{"test1", "test2"}, new MultipartFile[]{testFile,testFile}))
                .isNotNull();
    }

    @Test
    void saveServicesPageInfoTest_2() throws IOException {
        ServicesPage expected = new ServicesPage();
        expected.setId(10L);
        given(repository.getServicesPage()).willReturn(Optional.of(expected));
        MockMultipartFile testFile = new MockMultipartFile("test", new byte[1024]);

        doThrow(new NotFoundException()).when(fileUploadUtil).saveFile(any(),any(),any());

        assertThat(service.saveServicesPageInfo(expected, new String[]{"test1", "test2"},
                new String[]{"test1", "test2"}, new MultipartFile[]{testFile,testFile}))
                .isNotNull();
    }

    @Test
    void saveServicesPageInfoTest_3() throws IOException {
        ServicesPage expected = new ServicesPage();
        expected.setId(10L);
        expected.setServiceDescriptions(new ArrayList<>());
        given(repository.getServicesPage()).willReturn(Optional.of(expected));
        MockMultipartFile testFile = new MockMultipartFile("test", new byte[1024]);
        MockMultipartFile testFile2 = new MockMultipartFile("test", new byte[0]);

        doThrow(new NotFoundException()).when(fileUploadUtil).saveFile(any(),any(),any());

        assertThat(service.saveServicesPageInfo(expected, new String[]{"test1", "test2"},
                new String[]{"test1", "test2"}, new MultipartFile[]{testFile2,testFile2}))
                .isNotNull();
    }

    @Test
    void deleteImageAndGetPageTest() {
        AboutPage expected = new AboutPage();
        expected.setId(10L);
        expected.setPhotos("test1,test2");
        AboutPage test = new AboutPage();
        test.setId(10L);
        test.setPhotos("test1");
        assertThat(service.deleteImageAndGetPage(expected, 1)).isEqualTo(test);
    }

    @Test
    void deleteDocumentTest() {
        service.deleteDocument(1L);
    }

}