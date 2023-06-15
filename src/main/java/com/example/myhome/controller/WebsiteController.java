package com.example.myhome.controller;

import com.example.myhome.model.pages.AboutPage;
import com.example.myhome.model.pages.ContactsPage;
import com.example.myhome.model.pages.MainPage;
import com.example.myhome.model.pages.ServicesPage;
import com.example.myhome.service.WebsiteService;

import com.example.myhome.validator.MainPageValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/website")
@RequiredArgsConstructor
@Log
public class WebsiteController {

    private final WebsiteService websiteService;
    private final MainPageValidator validator;

    // Открыть страничку редактирования контента главной страницы
    @GetMapping("/home")
    public String showEditHomePage(Model model) {
        model.addAttribute("mainPage", websiteService.getMainPage());

        model.addAttribute("homeEditPageActive", true);

        log.info(Objects.requireNonNull(model.getAttribute("mainPage")).toString());
        return "admin_panel/website_settings/website_home";
    }

    // Открыть страничку редактирования контента страницы "О нас"
    @GetMapping("/about")
    public String showEditAboutPage(Model model) {
        AboutPage page = websiteService.getAboutPage();
        List<String> photos = Arrays.stream(page.getPhotos().split(","))
                .filter((photo) -> !photo.equals(""))
                .collect(Collectors.toList());
        List<String> add_photos = Arrays.stream(page.getAdd_photos().split(","))
                .filter((photo) -> !photo.equals(""))
                .collect(Collectors.toList());
        model.addAttribute("aboutPage", page);
        model.addAttribute("photos", photos);
        model.addAttribute("add_photos", add_photos);
        model.addAttribute("documents", websiteService.getAllDocuments());

        model.addAttribute("aboutEditPageActive", true);

        return "admin_panel/website_settings/website_about";
    }

    // Открыть страничку редактирования контента страницы "Услуги"
    @GetMapping("/services")
    public String showEditServicesPage(Model model) {
        model.addAttribute("servicesPage", websiteService.getServicesPage());

        model.addAttribute("servicesEditPageActive", true);

        return "admin_panel/website_settings/website_services";
    }

    // Открыть страничку редактирования контента страницы "Контакты"
    @GetMapping("/contacts")
    public String showEditContactsPage(Model model) {
        model.addAttribute("contactsPage", websiteService.getContactsPage());

        model.addAttribute("contactsEditPageActive", true);

        return "admin_panel/website_settings/website_contacts";
    }

    // =========================

    // Сохранение контента главной страницы
    // ¯\_(ツ)_/¯
    @PostMapping("/home")
    public String editHomePage(@Valid @ModelAttribute MainPage mainPage,
                               BindingResult bindingResult,
                               @RequestPart(required = false) MultipartFile page_slide1,
                               @RequestPart(required = false) MultipartFile page_slide2,
                               @RequestPart(required = false) MultipartFile page_slide3,
                               @RequestPart(required = false) MultipartFile page_block_1_img,
                               @RequestPart(required = false) MultipartFile page_block_2_img,
                               @RequestPart(required = false) MultipartFile page_block_3_img,
                               @RequestPart(required = false) MultipartFile page_block_4_img,
                               @RequestPart(required = false) MultipartFile page_block_5_img,
                               @RequestPart(required = false) MultipartFile page_block_6_img,
                               Model model) throws IOException {

        //validator.validate(mainPage, bindingResult);

        if(bindingResult.hasErrors()) {
            log.info("Errors found");
            log.info(bindingResult.getAllErrors().toString());
            model.addAttribute("validation", "failed");
            return "admin_panel/website_settings/website_home";
        }

        mainPage.setId(1);
        mainPage = websiteService.saveMainPageImages(mainPage, page_slide1, page_slide2, page_slide3, page_block_1_img,
                page_block_2_img, page_block_3_img, page_block_4_img, page_block_5_img, page_block_6_img);
        websiteService.savePage(mainPage);

        return "redirect:/admin/website/home";
    }

    // Сохранение контента страницы "О нас"
    // ¯\_(ツ)_/¯
    @PostMapping("/about")
    public String editAboutPage(@Valid @ModelAttribute AboutPage aboutPage,
                                BindingResult bindingResult,
                                @RequestPart(required = false) MultipartFile page_director_photo,
                                @RequestPart(required = false) MultipartFile[] page_photos,
                                @RequestPart(required = false) MultipartFile[] page_add_photos,
                                @RequestParam(required = false) String[] document_names,
                                @RequestParam(required = false) MultipartFile[] document_files,
                                Model model) throws IOException {

        if(bindingResult.hasErrors()) {
            log.info("Errors found");
            log.info(bindingResult.getAllErrors().toString());
            model.addAttribute("validation", "failed");
            return "admin_panel/website_settings/website_about";
        }

        aboutPage.setId(1);
        aboutPage = websiteService.saveAboutPageInfo(aboutPage, page_director_photo, page_photos, page_add_photos, document_names, document_files);
        websiteService.savePage(aboutPage);

        return "redirect:/admin/website/about";
    }

    // Сохранение контента страницы "Услуги"
    @PostMapping("/services")
    public String editServicesPage(@Valid @ModelAttribute ServicesPage servicesPage,
                                   BindingResult bindingResult,
                                   @RequestParam String[] titles,
                                   @RequestParam String[] descriptions,
                                   @RequestParam MultipartFile[] service_images,
                                   Model model) {

        if(bindingResult.hasErrors()) {
            log.info("Errors found");
            log.info(bindingResult.getAllErrors().toString());
            model.addAttribute("validation", "failed");
            return "admin_panel/website_settings/website_services";
        }

        servicesPage = websiteService.saveServicesPageInfo(servicesPage, titles, descriptions, service_images);
        websiteService.savePage(servicesPage);

        return "redirect:/admin/website/services";
    }


    // Сохранение контента страницы "Контакты"
    @PostMapping("/contacts")
    public String editContactsPage(@Valid @ModelAttribute ContactsPage contactsPage, BindingResult bindingResult, Model model){

        if(bindingResult.hasErrors()) {
            log.info("Errors found");
            log.info(bindingResult.getObjectName());
            log.info(bindingResult.getAllErrors().toString());
            model.addAttribute("page", contactsPage);
            model.addAttribute("validation", "failed");
            return "admin_panel/website_settings/website_contacts";
        }

        contactsPage.setId(1);
        websiteService.savePage(contactsPage);
        return "redirect:/admin/website/contacts";
    }

    // =========================

    // Удаление картинки из контента страницы "О нас" по ID
    @GetMapping("/delete-about-image/{index}")
    public String deleteAboutImage(@PathVariable int index, Model model) {
        AboutPage page = websiteService.getAboutPage();
        page = websiteService.deleteImageAndGetPage(page, index);
        websiteService.savePage(page);
        return "redirect:/admin/website/about";
    }

    // Удаление дополнительной картинки из контента страницы "О нас" по ID
    @GetMapping("/delete-about-add-image/{index}")
    public String deleteAboutAddImage(@PathVariable int index, Model model) {
        AboutPage page = websiteService.getAboutPage();
        String photos = page.getAdd_photos();
        List<String> photos_l = new ArrayList<>(Arrays.asList(photos.split(",")));
        photos_l.remove(index);
        page.setAdd_photos(String.join(",", photos_l));
        websiteService.savePage(page);
        return "redirect:/admin/website/about";
    }

    // Удаление документа из контента страницы "О нас" по ID
    @GetMapping("/delete-document/{id}")
    public String deleteDocument(@PathVariable long id) {
        websiteService.deleteDocument(id);
        return "redirect:/admin/website/about";
    }


    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("websiteEditPageActive", true);
    }


}
