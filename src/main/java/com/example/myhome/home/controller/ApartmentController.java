package com.example.myhome.home.controller;

import com.example.myhome.home.model.*;
import com.example.myhome.home.model.filter.FilterForm;
import com.example.myhome.home.repository.*;
import com.example.myhome.home.service.*;
import com.example.myhome.home.validator.ApartmentValidator;
import com.example.myhome.util.UserRole;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/apartments")
public class ApartmentController {

    @Value("${upload.path}")
    private String uploadPath;
    private final ApartmentService apartmentService;
    private final ApartmentRepository apartmentRepository;
    private final ApartmentAccountService apartmentAccountService;
    private final BuildingService buildingService;
    private final BuildingRepository buildingRepository;
    private final OwnerService ownerService;
    private final TariffService tariffService;
    private final CashBoxRepository cashBoxRepository;
    private final CashBoxService cashBoxService;
    private final InvoiceRepository invoiceRepository;
    private final ApartmentValidator apartmentValidator;
    private  final AccountRepository accountRepository;
    private  final MeterDataRepository meterDataRepository;
    private  final IncomeExpenseRepository incomeExpenseRepository;
    private  final AdminService adminService;
    private  final MeterDataService meterDataService;


    @GetMapping("/")
    public String getApartment(Model model, @PageableDefault(sort = {"id"}, direction = Sort.Direction.ASC, size = 10) Pageable pageable) {
        List<OwnerDTO> ownerDTOList = ownerService.findAllDTO();
        model.addAttribute("owners", ownerDTOList);
        List<BuildingDTO> buildingList = buildingService.findAllDTO();
        model.addAttribute("buildings", buildingList);
        List<String> sectionList = new ArrayList<>();
        model.addAttribute("section", sectionList);
        List<String> floorList = new ArrayList<>();
        model.addAttribute("floor", floorList);


        Page<Apartment> apartmentList = apartmentService.findAll(pageable);
        model.addAttribute("apartments", apartmentList);

        FilterForm filterForm = new FilterForm();
        System.out.println(filterForm.getBuildingName());
        model.addAttribute("filterForm", filterForm);
        return "admin_panel/apartments/apartments";
    }

    @GetMapping("/{id}")
    public String getApartment(@PathVariable("id") Long id, Model model) {
        Apartment apartment = apartmentService.findById(id);
        model.addAttribute("apartment", apartment);
        return "admin_panel/apartments/apartment";
    }

    @GetMapping("/new")
    public String createApartment(Model model) throws JsonProcessingException {
        List<OwnerDTO> ownerDTOList = ownerService.findAllDTO();
        model.addAttribute("owners", ownerDTOList);
        Apartment apartment = new Apartment();
        model.addAttribute("apartment", apartment);
        List<BuildingDTO> buildingList = buildingService.findAllDTO();
        model.addAttribute("buildings", buildingList);
        List<Tariff>tariffs = tariffService.findAllTariffs();
        model.addAttribute("tariffs", tariffs);
        List<ApartmentAccountDTO> accountDTOList = apartmentAccountService.findDtoApartmentAccounts();
        model.addAttribute("accounts", accountDTOList);
        return "admin_panel/apartments/apartment_edit";
    }
    @GetMapping("edit/{id}")
    public String editApartment(@PathVariable("id") Long id, Model model) {
        List<OwnerDTO> ownerDTOList = ownerService.findAllDTO();
        model.addAttribute("owners", ownerDTOList);
        Apartment apartment = apartmentService.findById(id);
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
        List<ApartmentAccountDTO> accountDTOList = apartmentAccountService.findDtoApartmentAccounts();
        model.addAttribute("accounts", accountDTOList);
        return "admin_panel/apartments/apartment_edit";
    }

    @PostMapping("/save")
    public String saveApartment(@Valid @ModelAttribute("apartment") Apartment apartment, BindingResult bindingResult) throws IOException {
        apartmentValidator.validate(apartment, bindingResult);
        if (bindingResult.hasErrors()) {
            return "admin_panel/apartments/apartment_edit";
        } else {
            apartment.setBalance(apartment.getAccount().getBalance());
            apartmentService.save(apartment);
            return "redirect:/admin/apartments/";
        }
    }@PostMapping("/save&new")
    public String saveAndNew(@Valid @ModelAttribute("apartment") Apartment apartment, BindingResult bindingResult) throws IOException {
        apartmentValidator.validate(apartment, bindingResult);
        if (bindingResult.hasErrors()) {
            return "admin_panel/apartments/apartment_edit";
        } else {
            apartment.setBalance(apartment.getAccount().getBalance());
            apartmentService.save(apartment);
            return "redirect:/admin/apartments/new";
        }
    }

    @PostMapping("/filter")
    public String filterApartments(Model model, @ModelAttribute FilterForm filterForm, @RequestParam(name = "number",required = false) Long number, @RequestParam(name = "buildingName",required = false) String buildingName,
                                   @RequestParam(name = "section",required = false) String section, @RequestParam(name = "floor",required = false) String floor,
                                   @RequestParam(name = "owner", required = false) Long owner, @RequestParam(name = "debtSting",required = false) String debt,
                                   @PageableDefault(sort = {"id"}, direction = Sort.Direction.ASC, size = 10) Pageable pageable) throws IOException {
        filterForm.setOwnerEntity(ownerService.findByIdDTO(owner));
        Page<Apartment> apartmentList = apartmentRepository.findByFilters(number,buildingName,section,floor,owner,debt,pageable);
        model.addAttribute("apartments", apartmentList);
        List<OwnerDTO> ownerDTOList = ownerService.findAllDTO();
        model.addAttribute("owners", ownerDTOList);
        List<BuildingDTO> buildingList = buildingService.findAllDTO();
        model.addAttribute("buildings", buildingList);
        List<String> sectionList = new ArrayList<>();
        List<String> floorList = new ArrayList<>();
        if(buildingName!=null && buildingName.length()>0 && !buildingName.equalsIgnoreCase("-")) {
            Building b = buildingRepository.findByName(buildingName);
            sectionList=b.getSections();
            floorList=b.getFloors();
        }
        model.addAttribute("sections", sectionList);
        model.addAttribute("floors", floorList);
        model.addAttribute("filterForm", filterForm);
        return "admin_panel/apartments/apartments";

    }

    @GetMapping("/delete/{id}")
    public String dellete(@PathVariable("id") Long id) {
        apartmentService.deleteById(id);
        return "redirect:/admin/apartments/";
    }

    @GetMapping("/get-owner")
    public @ResponseBody Owner getOwner(@RequestParam long flat_id) {
        return apartmentService.findById(flat_id).getOwner();
    }

    @GetMapping("/get-meters")
    public @ResponseBody List<MeterData> getMeters(@RequestParam long flat_id) {
        return apartmentService.findById(flat_id).getMeterDataList();
    }

    @GetMapping("/incomesByApartmentAccount/{id}")
    public String incomesByApartmentAccount(Model model, @PathVariable("id") Long id) {
        List<CashBox>cashBoxes=cashBoxRepository.findAllByApartmentAccountId(id);
        model.addAttribute("cashBoxList", cashBoxes);
        model.addAttribute("cashBoxSum", cashBoxRepository.sumAmount());
        model.addAttribute("accountBalance", accountRepository.getSumOfAccountBalances());
        model.addAttribute("sumDebt", accountRepository.getSumOfAccountDebts());
        return "admin_panel/cash_box/cashboxes";
    }

    @GetMapping("/invoicesByApartment/{id}")
    public String invoicesByApartment(Model model, @PathVariable("id") Long id) {
        List<Invoice>invoices=invoiceRepository.findAllByApartmentId(id);
        model.addAttribute("invoices", invoices);
        model.addAttribute("cashbox_balance", cashBoxService.calculateBalance());
        model.addAttribute("account_balance", apartmentAccountService.getSumOfAccountBalances());
        model.addAttribute("account_debt", apartmentAccountService.getSumOfAccountDebts());
        model.addAttribute("filter_form", new FilterForm());
        return "admin_panel/invoices/invoices";
    }
@GetMapping("/metersDataByApartment/{id}")
    public String metersDataByApartment(Model model, @PathVariable("id") Long id) {
        List<MeterData>meterDataList = meterDataRepository.findAllByApartmentId(id);
        model.addAttribute("meter_data_rows", meterDataList);
    model.addAttribute("filter_form", new FilterForm());
        return "admin_panel/meters/meters";
    }

    @GetMapping("/NewIncomesByApartment/{id}")
    public String NewIncomesByApartment(Model model, @PathVariable("id") Long id) {
        Apartment apartment = apartmentService.findById(id);
        List<IncomeExpenseItems>incomeItemsList=incomeExpenseRepository.findAllByIncomeExpenseType(IncomeExpenseType.INCOME);
        model.addAttribute("incomeItemsList", incomeItemsList);

        List<AdminDTO>adminDTOList = adminService.findAllDTO();

        // получение только юзеров с ролями "админ", "директор", "бухгалтер", без сантехников и т.д.
        adminDTOList = adminDTOList.stream()
                .filter(admin -> admin.getRole() == UserRole.ADMIN ||
                        admin.getRole() == UserRole.DIRECTOR ||
                        admin.getRole() == UserRole.MANAGER ||
                        admin.getRole() == UserRole.ACCOUNTANT)
                .collect(Collectors.toList());
        model.addAttribute("admins", adminDTOList);

        List<OwnerDTO> ownerDTOList = ownerService.findAllDTO();
        model.addAttribute("owners", ownerDTOList);

        List<ApartmentAccountDTO> apartmentAccountDTOS = apartmentAccountService.findDtoApartmentAccounts();
        model.addAttribute("accounts", apartmentAccountDTOS);

        CashBox cashBox = new CashBox();
        cashBox.setOwner(apartment.getOwner());
        cashBox.setApartmentAccount(apartment.getAccount());
        cashBox.setIncomeExpenseType(IncomeExpenseType.INCOME);
        model.addAttribute("cashBoxItem", cashBox);
        return "admin_panel/cash_box/cashbox_edit";
    }



    @GetMapping("/NewInvoiceByApartment/{id}")
    public String NewInvoiceByApartment(Model model, @PathVariable("id") Long id) {
        Invoice invoice = new Invoice();
        invoice.setApartment(apartmentService.findById(id));
        model.addAttribute("invoice", invoice);
        model.addAttribute("meters",  meterDataService.findSingleMeterData(id, null));
        return "admin_panel/invoices/invoice_card";
    }

    @GetMapping("/getUsers")
    @ResponseBody
    public Page<OwnerDTO> getOwners(@RequestParam(name = "searchQuerie", defaultValue = "") String searchQuerie,
                                    @RequestParam(name = "page", defaultValue = "0") int page,
                                    @RequestParam(name = "size", defaultValue = "2") int size) {

        System.out.println("page" + page + "size" + size);
        System.out.println("searchQuerie " + searchQuerie);
        Pageable pageable = PageRequest.of(page, size);

        Page<OwnerDTO> ownerPage = ownerService.findByNameFragmentDTO(searchQuerie, pageable);

        return ownerPage;
    }
}
