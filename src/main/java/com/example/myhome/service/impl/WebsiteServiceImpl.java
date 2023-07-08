package com.example.myhome.service.impl;

import com.example.myhome.model.pages.*;
//import com.example.myhome.repository.DocumentRepository;
import com.example.myhome.repository.PageRepository;
import com.example.myhome.service.WebsiteService;
import com.example.myhome.util.FileUploadUtil;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log
public class WebsiteServiceImpl implements WebsiteService {

    @Autowired
    private PageRepository pageRepository;

//    @Autowired
//    private DocumentRepository documentRepository;

    @Autowired
    private FileUploadUtil fileUploadUtil;

    private static final String imageSaveDir = "";

    public MainPage getMainPage() {return pageRepository.getMainPage().orElseThrow();}
    public AboutPage getAboutPage() {return pageRepository.getAboutPage().orElseThrow();}
    public ServicesPage getServicesPage() {return pageRepository.getServicesPage().orElseThrow();}
    public ContactsPage getContactsPage() {return pageRepository.getContactsPage().orElseThrow();}

    public Page savePage(Page page) {return pageRepository.save(page);}

//    public List<AboutPage.Document> getAllDocuments() {return documentRepository.findAll();}

    public AboutPage deleteImageAndGetPage(AboutPage page, int index) {
        String photos = page.getPhotos();
        String photoToDelete = photos.split(",")[index];
        photos = Arrays.stream(photos.split(","))
                .filter((photo) -> !photo.equals(photoToDelete))
                .collect(Collectors.joining(","));
        page.setPhotos(photos);
        return page;
    }

//    public void deleteDocument(Long document_id) {documentRepository.deleteById(document_id);}

    public MainPage saveMainPageImages(MainPage page,
                                       MultipartFile page_slide1,
                                       MultipartFile page_slide2,
                                       MultipartFile page_slide3,
                                       MultipartFile page_block_1_img,
                                       MultipartFile page_block_2_img,
                                       MultipartFile page_block_3_img,
                                       MultipartFile page_block_4_img,
                                       MultipartFile page_block_5_img,
                                       MultipartFile page_block_6_img) throws IOException {
        page.setId(1);
        MainPage originalPage = getMainPage();

        log.info("Setting and saving images for main page...");
        if(page_slide1 != null && page_slide1.getSize() > 0) {
            page.setSlide1(page_slide1.getOriginalFilename());
            fileUploadUtil.saveFile(imageSaveDir, page_slide1.getOriginalFilename(), page_slide1);
        } else page.setSlide1(originalPage.getSlide1());
        if(page_slide2 != null && page_slide2.getSize() > 0) {
            page.setSlide2(page_slide2.getOriginalFilename());
            fileUploadUtil.saveFile(imageSaveDir, page_slide2.getOriginalFilename(), page_slide2);
        } else page.setSlide2(originalPage.getSlide2());
        if(page_slide3 != null && page_slide3.getSize() > 0) {
            page.setSlide3(page_slide3.getOriginalFilename());
            fileUploadUtil.saveFile(imageSaveDir, page_slide3.getOriginalFilename(), page_slide3);
        } else page.setSlide3(originalPage.getSlide3());
        if(page_block_1_img != null && page_block_1_img.getSize() > 0) {
            page.setBlock_1_img(page_block_1_img.getOriginalFilename());
            fileUploadUtil.saveFile(imageSaveDir, page_block_1_img.getOriginalFilename(), page_block_1_img);
        } else page.setBlock_1_img(originalPage.getBlock_1_img());
        if(page_block_2_img != null && page_block_2_img.getSize() > 0) {
            page.setBlock_2_img(page_block_2_img.getOriginalFilename());
            fileUploadUtil.saveFile(imageSaveDir, page_block_2_img.getOriginalFilename(), page_block_2_img);
        } else page.setBlock_2_img(originalPage.getBlock_2_img());
        if(page_block_3_img != null && page_block_3_img.getSize() > 0) {
            page.setBlock_3_img(page_block_3_img.getOriginalFilename());
            fileUploadUtil.saveFile(imageSaveDir, page_block_3_img.getOriginalFilename(), page_block_3_img);
        } else page.setBlock_3_img(originalPage.getBlock_3_img());
        if(page_block_4_img != null && page_block_4_img.getSize() > 0) {
            page.setBlock_4_img(page_block_4_img.getOriginalFilename());
            fileUploadUtil.saveFile(imageSaveDir, page_block_4_img.getOriginalFilename(), page_block_4_img);
        } else page.setBlock_4_img(originalPage.getBlock_4_img());
        if(page_block_5_img != null && page_block_5_img.getSize() > 0) {
            page.setBlock_5_img(page_block_5_img.getOriginalFilename());
            fileUploadUtil.saveFile(imageSaveDir, page_block_5_img.getOriginalFilename(), page_block_5_img);
        } else page.setBlock_5_img(originalPage.getBlock_5_img());
        if(page_block_6_img != null && page_block_6_img.getSize() > 0) {
            page.setBlock_6_img(page_block_6_img.getOriginalFilename());
            fileUploadUtil.saveFile(imageSaveDir, page_block_6_img.getOriginalFilename(), page_block_6_img);
        } else page.setBlock_6_img(originalPage.getBlock_6_img());

        return page;
    }

    public AboutPage saveAboutPageInfo(AboutPage page,
                                       MultipartFile page_director_photo,
                                       MultipartFile[] page_photos,
                                       MultipartFile[] page_add_photos) throws IOException {
        page.setId(1);
        AboutPage originalPage = getAboutPage();

        //Сохранение единичного фото директора
        log.info("Saving director photo");
        if(page_director_photo.getSize() > 0) {
            fileUploadUtil.saveFile(imageSaveDir, page_director_photo.getOriginalFilename(), page_director_photo);
            page.setDirector_photo(page_director_photo.getOriginalFilename());
        } else page.setDirector_photo(originalPage.getDirector_photo());
        log.info("Saved director photo");

        //Сохранение фото из фотогалереи
        log.info("Saving photos...");
        if(page_photos.length > 1) {
            log.info(String.valueOf(page_photos.length));
            log.info("Photos found on page");
            log.info(Arrays.toString(page_photos));
            String photosToSave = "";
            List<String> list = Arrays.stream(originalPage.getPhotos().split(",")).collect(Collectors.toList());
            for(MultipartFile photo : page_photos) {
                log.info("Printing photo info:");
                log.info(photo.toString());
                if(photo.getSize() > 0) {
                    fileUploadUtil.saveFile(imageSaveDir, photo.getOriginalFilename(), photo);
                    list.add(photo.getOriginalFilename());
                }
            }
            photosToSave = String.join(",", list);
            log.info("Saved photos: " + photosToSave);
            page.setPhotos(photosToSave);

        } else if(page_photos.length == 1 && page_photos[0].getSize() > 0) {
            MultipartFile photo = page_photos[0];
            List<String> list = Arrays.stream(originalPage.getPhotos().split(",")).collect(Collectors.toList());
            fileUploadUtil.saveFile(imageSaveDir, photo.getOriginalFilename(), photo);
            log.info("Saved single photo: " + photo.getOriginalFilename());
            page.setPhotos(String.join(",", list) +","+ photo.getOriginalFilename());
        } else page.setPhotos(originalPage.getPhotos());

        log.info(page.toString());
        log.info(page.getPhotos());

        //Сохранение фото из доп.фотогалереи
        log.info("Saving additional photos...");
        if(page_add_photos.length > 1) {
            log.info(String.valueOf(page_add_photos.length));
            log.info("Photos found on page");
            log.info(Arrays.toString(page_add_photos));
            String photosToSave = "";
            List<String> list = Arrays.stream(originalPage.getAdd_photos().split(",")).collect(Collectors.toList());
            for(MultipartFile photo : page_add_photos) {
                log.info("Printing photo info:");
                log.info(photo.toString());
                if(photo.getSize() > 0) {
                    fileUploadUtil.saveFile(imageSaveDir, photo.getOriginalFilename(), photo);
                    list.add(photo.getOriginalFilename());
                }
            }
            photosToSave = String.join(",", list);
            log.info("Saved photos: " + photosToSave);
            page.setAdd_photos(photosToSave);

        } else if(page_add_photos.length == 1 && page_add_photos[0].getSize() > 0) {
            MultipartFile photo = page_add_photos[0];
            List<String> list = Arrays.stream(originalPage.getAdd_photos().split(",")).collect(Collectors.toList());
            fileUploadUtil.saveFile(imageSaveDir, photo.getOriginalFilename(), photo);
            log.info("Saved single photo: " + photo.getOriginalFilename());
            page.setAdd_photos(String.join(",", list) +","+ photo.getOriginalFilename());
        } else page.setAdd_photos(originalPage.getAdd_photos());

        log.info(page.toString());
        log.info(page.getAdd_photos());

        //Сохранение документов
        for(AboutPage.Document document : page.getDocuments()) {
            if((document.getFileName() == null || document.getFileName().isEmpty()) &&
                    (document.getFile() != null && document.getFile().getSize() > 0)) {
                log.info(document.getFile().toString());
                log.info(document.getFile().getContentType());
                log.info(document.getFile().getOriginalFilename());
                fileUploadUtil.saveFile("/documents", document.getFile().getOriginalFilename(), document.getFile());
                document.setFileName(document.getFile().getOriginalFilename());
            }
        }

        log.info("Final page info to save: " + page);
        return page;
    }

    public ServicesPage saveServicesPageInfo(ServicesPage page) throws IOException {
        page.setId(1);

        for(ServicesPage.ServiceDescription service : page.getServiceDescriptions()) {
            log.info(service.toString());
            if(service.getFile() != null && service.getFile().getSize() > 0) {
                fileUploadUtil.saveFile("/pages/services", service.getFile().getOriginalFilename(), service.getFile());
                service.setPhoto(service.getFile().getOriginalFilename());
            }
        }

        return pageRepository.save(page);
    }


}
