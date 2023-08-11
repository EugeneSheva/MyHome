package com.example.myhome.controller;

import com.example.myhome.dto.*;

import com.example.myhome.dto.OwnerDTO;
import com.example.myhome.mapper.AccountDTOMapper;
import com.example.myhome.mapper.ApartmentDTOMapper;
import com.example.myhome.model.filter.FilterForm;


import com.example.myhome.service.BuildingService;
import com.example.myhome.validator.ApartmentValidator;
import com.example.myhome.model.*;
import com.example.myhome.repository.*;
import com.example.myhome.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin/apartments")
public class ApartmentController {

    @Value("${upload.path}")
    private String uploadPath;

    private final ApartmentService apartmentService;
    private final AccountService accountService;
    private final BuildingService buildingService;
    private final InvoiceService invoiceService;
    private final OwnerService ownerService;
    private final TariffService tariffService;
    private final CashBoxService cashBoxService;
    private final IncomeExpenseItemService incomeExpenseItemService;
    private final ApartmentValidator apartmentValidator;
    private final AdminService adminService;
    private final MeterDataService meterDataService;

    private final AccountDTOMapper accountDTOMapper;
    private final ApartmentDTOMapper apartmentDTOMapper;

    private final MessageSource messageSource;


    @GetMapping
    public String getApartment(Model model, @PageableDefault(sort = {"id"}, direction = Sort.Direction.ASC, size = 10) Pageable pageable) {
        List<OwnerDTO> ownerDTOList = ownerService.findAllDTO();
        model.addAttribute("owners", ownerDTOList);
        List<BuildingDTO> buildingList = buildingService.findAllDTO();
        model.addAttribute("buildings", buildingList);
        List<String> sectionList = new ArrayList<>();
        model.addAttribute("section", sectionList);
        List<String> floorList = new ArrayList<>();
        model.addAttribute("floor", floorList);

        FilterForm filterForm = new FilterForm();
        System.out.println(filterForm.getBuildingName());
        model.addAttribute("filterForm", filterForm);
        return "admin_panel/apartments/apartments";
    }

    @GetMapping("/{id}")
    public String getApartment(@PathVariable("id") Long id, Model model) {
        ApartmentDTO apartment = apartmentService.findApartmentDto(id);
        model.addAttribute("apartment", apartment);
        return "admin_panel/apartments/apartment";
    }

    @GetMapping("/new")
    public String createApartment(Model model) throws JsonProcessingException {
        List<OwnerDTO> ownerDTOList = ownerService.findAllDTO();
        model.addAttribute("owners", ownerDTOList);
        ApartmentDTO apartmentDTO = new ApartmentDTO();
        model.addAttribute("apartment", apartmentDTO);
        List<BuildingDTO> buildingList = buildingService.findAllDTO();
        model.addAttribute("buildings", buildingList);
        List<Tariff>tariffs = tariffService.findAllTariffs();
        model.addAttribute("tariffs", tariffs);
        List<ApartmentAccountDTO> accountDTOList = accountService.findAllAccounts().stream().map(accountDTOMapper::fromAccountToDTO).collect(Collectors.toList());
        model.addAttribute("accounts", accountDTOList);
        return "admin_panel/apartments/apartment_edit";
    }
    @GetMapping("edit/{id}")
    public String editApartment(@PathVariable("id") Long id, Model model) {
        List<OwnerDTO> ownerDTOList = ownerService.findAllDTO();
        model.addAttribute("owners", ownerDTOList);
        ApartmentDTO apartment = apartmentService.findApartmentDto(id);
        model.addAttribute("apartment", apartment);
        List<BuildingDTO> buildingList = buildingService.findAllDTO();
        model.addAttribute("buildings", buildingList);
        if(apartment.getBuilding()!=null) {
            List<String>sections = apartment.getBuilding().getSections();
            List<String>floors = apartment.getBuilding().getFloors();
            model.addAttribute("sections", sections);
            model.addAttribute("floors", floors);
        }
        List<Tariff>tariffs = tariffService.findAllTariffs();
        model.addAttribute("tariffs", tariffs);
        List<ApartmentAccountDTO> accountDTOList = accountService.findAllAccounts().stream().map(accountDTOMapper::fromAccountToDTO).collect(Collectors.toList());
        model.addAttribute("accounts", accountDTOList);
        return "admin_panel/apartments/apartment_edit";
    }

    @PostMapping("/save")
    public String saveApartment(@Valid @ModelAttribute("apartment") ApartmentDTO apartment, BindingResult bindingResult,
    Model model) throws IOException {
        apartmentValidator.validate(apartment, bindingResult);
        if (bindingResult.hasErrors()) {
            log.info(apartment.getSection());
            log.info(apartment.getFloor());
            model.addAttribute("apartment", apartment);
            List<BuildingDTO> buildingList = buildingService.findAllDTO();
            model.addAttribute("buildings", buildingList);
            List<Tariff>tariffs = tariffService.findAllTariffs();
            model.addAttribute("tariffs", tariffs);
            List<ApartmentAccountDTO> accountDTOList = accountService.findAllAccounts().stream().map(accountDTOMapper::fromAccountToDTO).collect(Collectors.toList());
            model.addAttribute("accounts", accountDTOList);
            List<String>sections = apartment.getBuilding().getSections();
            List<String>floors = apartment.getBuilding().getFloors();
            model.addAttribute("sections", sections);
            model.addAttribute("floors", floors);
            model.addAttribute("validation","failed");
            apartment.setOwner(ownerService.findByIdDTO(apartment.getOwner().getId()));
            return "admin_panel/apartments/apartment_edit";
        } else {
            apartment.setBalance((apartment.getAccount() != null) ? apartment.getAccount().getBalance() : 0);
            apartmentService.save(apartment);
            return "redirect:/admin/apartments/";
        }
    }

    @PostMapping("/save&new")
    public String saveAndNew(@Valid @ModelAttribute("apartment") ApartmentDTO apartment, BindingResult bindingResult,
                             Model model) throws IOException {
        apartmentValidator.validate(apartment, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("apartment", apartment);
            List<BuildingDTO> buildingList = buildingService.findAllDTO();
            model.addAttribute("buildings", buildingList);
            List<Tariff>tariffs = tariffService.findAllTariffs();
            model.addAttribute("tariffs", tariffs);
            List<ApartmentAccountDTO> accountDTOList = accountService.findAllAccounts().stream().map(accountDTOMapper::fromAccountToDTO).collect(Collectors.toList());
            model.addAttribute("accounts", accountDTOList);
            List<String>sections = apartment.getBuilding().getSections();
            List<String>floors = apartment.getBuilding().getFloors();
            model.addAttribute("sections", sections);
            model.addAttribute("floors", floors);
            model.addAttribute("validation","failed");
            return "admin_panel/apartments/apartment_edit";
        } else {
            apartment.setBalance(apartment.getAccount().getBalance());
            apartmentService.save(apartment);
            return "redirect:/admin/apartments/new";
        }
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            apartmentService.deleteById(id);
        } catch (Exception e) {
            log.error("Apartment deletion error");
            redirectAttributes.addFlashAttribute("fail",messageSource.getMessage("apartment.delete.error", null, LocaleContextHolder.getLocale()));
        }
        return "redirect:/admin/apartments/";
    }


    @GetMapping("/incomesByApartmentAccount/{id}")
    public String incomesByApartmentAccount(Model model, @PathVariable("id") Long id) {
        return "redirect:/admin/cashbox/show-incomes?account_id="
                +apartmentService.findById(id).getAccount().getId();
    }

    @GetMapping("/invoicesByApartment/{id}")
    public String invoicesByApartment(Model model, @PathVariable("id") Long id) {
        return "redirect:/admin/invoices?apartment="+apartmentService.findById(id).getNumber();
    }

    @GetMapping("/metersDataByApartment/{id}")
    public String metersDataByApartment(Model model, @PathVariable("id") Long id) {
        return "redirect:/admin/meters?apartment="+apartmentService.findById(id).getNumber();
    }

    @GetMapping("/NewIncomesByApartment/{id}")
    public String NewIncomesByApartment(Model model, @PathVariable("id") Long id) {
        if(accountService.apartmentHasAccount(id))
            return "redirect:/admin/cashbox/newIncome?account_id=" + apartmentService.findById(id).getAccount().getId();
        else return "redirect:/admin/apartments/"+id;
    }

    @GetMapping("/NewInvoiceByApartment/{id}")
    public String NewInvoiceByApartment(Model model, @PathVariable("id") Long id) {
        return "redirect:/admin/invoices/create?flat_id="+id;
    }

    @GetMapping("/getUsers")
    @ResponseBody
    public Page<OwnerDTO> getOwners(@RequestParam(name = "searchQuerie", defaultValue = "") String searchQuerie,
                                    @RequestParam(name = "page", defaultValue = "0") int page,
                                    @RequestParam(name = "size", defaultValue = "2") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<OwnerDTO> ownerPage = ownerService.findByNameFragmentDTO(searchQuerie, pageable);
        return ownerPage;
    }

    @GetMapping("/get-apartments-page")
    @ResponseBody
    public Page<ApartmentDTO> getApartmentsByPage(@RequestParam Integer page,
                                                  @RequestParam Integer size,
                                                  @RequestParam String filters) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        FilterForm form = mapper.readValue(filters, FilterForm.class);
        return apartmentService.findBySpecificationAndPage(page, size, form);
    }
    @GetMapping("/create-apartment-account")
    @ResponseBody
    public Long createAapartmentAccount(@RequestParam String building,
                                        @RequestParam String owner, @RequestParam String section) throws JsonProcessingException {
        System.out.println("ajax");
        System.out.println("building " +building);
        System.out.println("owner "+ owner);
        System.out.println("section "+ section);
        ApartmentAccount apartmentAccount = accountService.saveAccount(new ApartmentAccount(true, buildingService.findById(Long.valueOf(building)), ownerService.findById(Long.valueOf(owner)), section, 0.0));
        return apartmentAccount.getId();
    }

    @GetMapping("/get-section")
    public @ResponseBody String getApartmentSection(@RequestParam Long id) {
        String section = apartmentService.findById(id).getSection();
        System.out.println("Found section " + section);
        byte[] bytes = section.getBytes(StandardCharsets.UTF_8);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    @GetMapping("/get-apartment")
    public @ResponseBody ApartmentDTO getSingleApartment(@RequestParam Long id) {
        Apartment apartment = apartmentService.findById(id);
        return apartmentDTOMapper.fromApartmentToDTO(apartment);
    }

    @GetMapping("/get-owner")
    public @ResponseBody Owner getOwner(@RequestParam long flat_id) {
        return apartmentService.findById(flat_id).getOwner();
    }

    @GetMapping("/get-meters")
    public @ResponseBody List<MeterData> getMeters(@RequestParam long flat_id) {
        return apartmentService.findById(flat_id).getMeterDataList();
    }



    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("apartmentsPageActive", true);
    }
}
