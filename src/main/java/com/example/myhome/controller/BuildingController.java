package com.example.myhome.controller;

import com.example.myhome.dto.AdminDTO;
import com.example.myhome.dto.BuildingDTO;
import com.example.myhome.mapper.BuildingDTOMapper;
import com.example.myhome.model.Building;
import com.example.myhome.model.filter.FilterForm;
import com.example.myhome.repository.BuildingRepository;
import com.example.myhome.service.AdminService;
import com.example.myhome.service.BuildingService;
import com.example.myhome.validator.BuildingValidator;
import com.example.myhome.model.Admin;
import com.example.myhome.model.Apartment;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/buildings")
@Log
public class BuildingController {

    @Autowired
    private final BuildingValidator validator;


    @Value("${upload.path}")
    private String uploadPath;
    private final BuildingService buildingService;
    private final BuildingRepository buildingRepository;
    private final AdminService adminService;

    private final BuildingValidator buildingValidator;

    private final BuildingDTOMapper mapper;
    @GetMapping
    public String getBuildigs(Model model, @PageableDefault(sort = {"id"}, direction = Sort.Direction.ASC, size = 10) Pageable pageable) {
        Page<Building> buildingList = buildingService.findAll(pageable);
        model.addAttribute("buildings", buildingList);
        model.addAttribute("totalPagesCount", buildingList.getTotalPages());
        FilterForm filterForm = new FilterForm();
        model.addAttribute("filterForm", filterForm);
        return "admin_panel/buildings/buildings";
    }

    @GetMapping("/{id}")
    public String getBuildig(@PathVariable("id") Long id, Model model) {
        Building building = buildingService.findById(id);
        model.addAttribute("building", building);
        return "admin_panel/buildings/building";
    }

    @GetMapping("/new")
    public String createBuildig(Model model) {
        Building building = new Building();
        model.addAttribute("building", building);
        List<AdminDTO>adminList=adminService.findAllDTO();
        model.addAttribute("adminList", adminList);
        return "admin_panel/buildings/building_edit";
    }
    @GetMapping("edit/{id}")
    public String editBuildig(@PathVariable("id") Long id, Model model) {
        Building building = buildingService.findById(id);
        model.addAttribute("building", building);
        List<AdminDTO>adminList=adminService.findAllDTO();
        model.addAttribute("adminList", adminList);
//        model.addAttribute("selectedAdmin", new Admin());
        return "admin_panel/buildings/building_edit";
    }

    @PostMapping("/save")
    public String saveBuildig(@Valid @ModelAttribute("building") Building build, BindingResult bindingResult, @RequestParam("name") String name, @RequestParam("address") String address,
                              @RequestParam("sections") List<String> sections, @RequestParam(name = "id", defaultValue = "0") Long id, @RequestParam("floors") List<String> floors, @RequestParam("admins") List<Admin> admins,
                              @RequestParam("img01") MultipartFile file1, @RequestParam("img02") MultipartFile file2, @RequestParam("img03") MultipartFile file3, @RequestParam("img04") MultipartFile file4,
                              @RequestParam("img05") MultipartFile file5) throws IOException {
        buildingValidator.validate(build, bindingResult);
        if (bindingResult.hasErrors()) {
                return "admin_panel/buildings/building_edit";
            } else {
            System.out.println("admins "+admins);
                Building building = buildingService.saveBuildindImages(id, file1, file2, file3, file4, file5);
                building.setName(name);
                building.setAddress(address);
                building.setFloors(floors);
                building.setSections(sections);
                building.setAdmins(admins);
                buildingService.save(building);
                return "redirect:/admin/buildings/";
            }
    }

    @PostMapping("/filter")
    public String filterBuildig(Model model, @RequestParam("name") String name, @RequestParam("address") String address, @PageableDefault(sort = {"id"}, direction = Sort.Direction.ASC, size = 10) Pageable pageable) throws IOException {
        System.out.println("name "+name);
        System.out.println("address "+address);
        Page<Building> buildingList = buildingRepository.findByFilters(name,address,pageable);
        System.out.println(buildingList);
        FilterForm filterForm = new FilterForm();
        filterForm.setName(name);
        filterForm.setAddress(address);
        model.addAttribute("filterForm", filterForm);
        model.addAttribute("buildings", buildingList);
            return "admin_panel/buildings/buildings";
    }

    @GetMapping("/delete/{id}")
    public String dellete(@PathVariable("id") Long id) {
        Building building = buildingService.findById(id);
        try {
            Files.deleteIfExists(Path.of(uploadPath + building.getImg1()));
            Files.deleteIfExists(Path.of(uploadPath + building.getImg2()));
            Files.deleteIfExists(Path.of(uploadPath + building.getImg3()));
            Files.deleteIfExists(Path.of(uploadPath + building.getImg4()));
            Files.deleteIfExists(Path.of(uploadPath + building.getImg5()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        buildingService.deleteById(id);
        return "redirect:/admin/buildings/";
    }

    @GetMapping("/get-sections/{id}")
    public @ResponseBody List<String> getBuildingSections(@PathVariable long id) {
        return buildingService.findById(id).getSections();
    }

    @GetMapping("/get-section-apartments")
    public @ResponseBody List<Apartment> getBuildingSectionApartments(@RequestParam long id, @RequestParam String section_name) {

        List<Apartment> apartments = buildingService.findById(id).getApartments();
        return apartments.stream().filter((apartment) -> apartment.getSection().equals(section_name)).collect(Collectors.toList());

    }
    @GetMapping("/get-section-apartments/{id}")
    public @ResponseBody List<Apartment> getBuildingSectionApartmentsFromQuery(@PathVariable long id, @RequestParam String section_name) {
        return buildingService.getSectionApartments(id, section_name);
    }

    @GetMapping("/get-building")
    public @ResponseBody BuildingDTO getSingleBuilding(@RequestParam Long building_id) {
        Building building = buildingService.findById(building_id);
        BuildingDTO dto = mapper.fromBuildingToDTO(building);
        log.info(dto.toString());
        return dto;
    }

    @GetMapping("/get-buildings")
    public @ResponseBody Map<String, Object> getBuildings(@RequestParam String search,
                                                     @RequestParam int page) {
        log.info("Getting all buildings that have in their name: " + search);
        Map<String, Object> map = new HashMap<>();
        Map<String, Boolean> pagination = new HashMap<>();
        pagination.put("more", ((long) page*5) < buildingService.countBuildings());
        map.put("results", buildingService.findByPage(search, page));
        map.put("pagination", pagination);
        System.out.println(map.get("results").toString());
        System.out.println(map.get("pagination").toString());
        return map;
    }

    @GetMapping("/get-buildings-page")
    public @ResponseBody Page<BuildingDTO> getBuildingsPage(@RequestParam Integer page,
                                                            @RequestParam Integer size,
                                                            @RequestParam String filters) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        FilterForm form = mapper.readValue(filters, FilterForm.class);
        return buildingService.findAllBySpecification(form, page, size);
    }
}