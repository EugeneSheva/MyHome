package com.example.myhome.controller;

import com.example.myhome.model.pages.AboutPage;
import com.example.myhome.service.WebsiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class MainController {

    @Autowired private WebsiteService websiteService;

    @GetMapping
    public String showMainPage(Model model) {
        model.addAttribute("page", websiteService.getMainPage());
        model.addAttribute("contacts", websiteService.getContactsPage());
        return "main_website/index";
    }

    @GetMapping("/about")
    public String showAboutPage(Model model) {
        AboutPage page = websiteService.getAboutPage();
        List<String> photos = Arrays.stream(page.getPhotos().split(",")).filter((photo) -> !photo.equals("")).collect(Collectors.toList());
        List<String> add_photos = Arrays.stream(page.getAdd_photos().split(",")).filter((photo) -> !photo.equals("")).collect(Collectors.toList());
//        List<AboutPage.Document> documents = websiteService.getAllDocuments();
        model.addAttribute("page", page);
        model.addAttribute("photos", photos);
        model.addAttribute("add_photos", add_photos);
//        model.addAttribute("documents", documents);
        return "main_website/about";
    }

    @GetMapping("/services")
    public String showServicesPage(Model model) {
        model.addAttribute("servicesPage", websiteService.getServicesPage());
        return "main_website/services";
    }

    @GetMapping("/contacts")
    public String showContactsPage(Model model) {
        model.addAttribute("contactsPage", websiteService.getContactsPage());
        return "main_website/contacts";
    }

    @PostMapping("/change-language")
    public @ResponseBody String changeLanguage(@RequestParam("language") String language, HttpServletRequest request, HttpServletResponse response) {
        LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
        Locale locale = new Locale(language.split("_")[0], language.split("_")[1]);
        localeResolver.setLocale(request, response, locale);
        System.out.println(request.getRequestURL().toString());
        return "Locale changed to: " + locale.getDisplayLanguage();
    }

}
