package com.example.myhome.home.controller;

import com.example.myhome.home.model.Apartment;
import com.example.myhome.home.model.Building;
import com.example.myhome.home.model.Owner;
import com.example.myhome.home.service.BuildingService;
import com.example.myhome.home.service.OwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;


@Controller
@RequiredArgsConstructor
@RequestMapping("/owners")
public class OwnerController {

    @Value("${upload.path}")
    private String uploadPath;
    private final OwnerService ownerService;

    @GetMapping("/")
    public String getOwners(Model model) {
        List<Owner> ownerList = ownerService.findAll();
        model.addAttribute("owners", ownerList);
        return "admin_panel/owners";
    }

    @GetMapping("/{id}")
    public String getOwner(@PathVariable("id") Long id, Model model) {
        Owner owner = ownerService.findById(id);
        model.addAttribute("owner", owner);
        return "admin_panel/owner";
    }

    @GetMapping("/new")
    public String createOwner(Model model) {
        Owner owner = new Owner();
        model.addAttribute("owner", owner);
        return "admin_panel/owner_edit";
    }
    @GetMapping("edit/{id}")
    public String editeOwner(@PathVariable("id") Long id, Model model) {
        Owner owner = ownerService.findById(id);
        model.addAttribute("owner", owner);
        return "admin_panel/owner_edit";
    }

    @PostMapping("/save")
    public String saveCoffee(@ModelAttribute("owner") Owner owner, @RequestParam("img1") MultipartFile file) throws IOException {
        owner.setProfile_picture(ownerService.saveOwnerImage(owner.getId(), file));
        ownerService.save(owner);
        return "redirect:/owners/";
    }

    @GetMapping("/delete/{id}")
    public String dellete(@PathVariable("id") Long id) {
        Owner owner = ownerService.findById(id);
        try {
            Files.deleteIfExists(Path.of(uploadPath + owner.getProfile_picture()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ownerService.deleteById(id);
        return "redirect:/owners/";
    }
}