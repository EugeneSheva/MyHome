package com.example.myhome.home.service;

import com.example.myhome.home.model.pages.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.Multipart;
import java.io.IOException;
import java.util.List;

public interface WebsiteService {

    public MainPage getMainPage();
    public AboutPage getAboutPage();
    public ServicesPage getServicesPage();
    public ContactsPage getContactsPage();

    public Page savePage(Page page);

    public List<AboutPage.Document> getAllDocuments();
    public void deleteDocument(Long index);

    public MainPage saveMainPageImages(MainPage page, MultipartFile page_slide1,
                                       MultipartFile page_slide2,
                                       MultipartFile page_slide3,
                                       MultipartFile page_block_1_img,
                                       MultipartFile page_block_2_img,
                                       MultipartFile page_block_3_img,
                                       MultipartFile page_block_4_img,
                                       MultipartFile page_block_5_img,
                                       MultipartFile page_block_6_img) throws IOException;
    public AboutPage saveAboutPageInfo(AboutPage page,MultipartFile page_director_photo,MultipartFile[] page_photos,
                                       MultipartFile[] page_add_photos,String[] document_names,MultipartFile[] document_files) throws IOException;
    public ServicesPage saveServicesPageInfo(ServicesPage page, String[] titles,String[] descriptions,
                                             MultipartFile[] service_images);
    public AboutPage deleteImageAndGetPage(AboutPage page, int index);


}
