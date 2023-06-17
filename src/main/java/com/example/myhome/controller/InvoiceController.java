package com.example.myhome.controller;

import com.example.myhome.controller.socket.WebsocketController;
import com.example.myhome.dto.ApartmentDTO;
import com.example.myhome.dto.BuildingDTO;
import com.example.myhome.dto.InvoiceDTO;
import com.example.myhome.model.InvoiceTemplate;
import com.example.myhome.model.filter.FilterForm;

import com.example.myhome.service.*;
import com.example.myhome.validator.InvoiceValidator;
import com.example.myhome.model.Apartment;
import com.example.myhome.model.Invoice;
import com.example.myhome.util.FileDownloadUtil;
import com.example.myhome.util.FileUploadUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.java.Log;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;

@SuppressWarnings("unused")
@Controller
@RequestMapping("/admin/invoices")
@Log
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private CashBoxService cashBoxService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private ApartmentService apartmentService;

    @Autowired
    private BuildingService buildingService;

    @Autowired
    private ServiceService serviceService;

    @Autowired
    private TariffService tariffService;

    @Autowired
    private MeterDataService meterDataService;

    @Autowired
    private FileUploadUtil fileUploadUtil;

    @Autowired private OwnerService ownerService;

    @Autowired
    private InvoiceValidator validator;

    @Autowired
    private FileDownloadUtil fileDownloadUtil;

    @Autowired private WebsocketController websocketController;

    @GetMapping
    public String showInvoicePage(Model model,
                                  FilterForm form) {

        model.addAttribute("owners", ownerService.findAllDTO());
        model.addAttribute("cashbox_balance", cashBoxService.calculateBalance());
        model.addAttribute("account_balance", accountService.getSumOfAccountBalances());
        model.addAttribute("account_debt", accountService.getSumOfAccountDebts());

        model.addAttribute("filter_form", form);

        return "admin_panel/invoices/invoices";
    }

    @GetMapping("/search")
    public String showSearch(@RequestParam long flat_id, Model model) {
        Apartment apartment = apartmentService.findById(flat_id);
        model.addAttribute("current_date", LocalDate.now());
        model.addAttribute("invoices", apartment.getInvoiceList());
        return "admin_panel/invoices/invoices";
    }

    @GetMapping("/{id}")
    public String showInvoiceInfo(@PathVariable long id, Model model) {
        InvoiceDTO invoice = invoiceService.findInvoiceDTOById(id);
        log.info("PROFILE INFO");
        log.info(invoice.toString());
        model.addAttribute("invoice", invoice);
        model.addAttribute("total_price", invoice.getTotal_price());
        model.addAttribute("current_date", LocalDate.now());
        return "admin_panel/invoices/invoice_profile";
    }

    @GetMapping("/create")
    public String showCreateInvoicePage(@RequestParam(required = false) Long flat_id, Model model) {
        InvoiceDTO invoiceDTO = new InvoiceDTO();
        BuildingDTO buildingDTO = new BuildingDTO();
        ApartmentDTO apartmentDTO = new ApartmentDTO();
        if(flat_id != null) {
            System.out.println("ID IS NOT NULL");
            ApartmentDTO apartment = apartmentService.findApartmentDto(flat_id);
            System.out.println(apartment.toString());
            invoiceDTO.setApartment(apartment);
            invoiceDTO.setBuilding(apartment.getBuilding());
            invoiceDTO.setOwner(apartment.getOwner());
            invoiceDTO.setAccount(apartment.getAccount());
            invoiceDTO.setSection(apartment.getSection());
        } else {
            invoiceDTO.setBuilding(buildingDTO);
            invoiceDTO.setApartment(apartmentDTO);
        }
        model.addAttribute("id", invoiceService.getMaxInvoiceId()+1L);
        model.addAttribute("flat", invoiceDTO.getApartment());
        model.addAttribute("invoiceDTO", invoiceDTO);
        model.addAttribute("meters", (flat_id == null) ? meterDataService.findAllMeters() : meterDataService.findSingleMeterData(flat_id, null));
        model.addAttribute("current_date", LocalDate.now());
        return "admin_panel/invoices/invoice_card";
    }

    @GetMapping("/update/{id}")
    public String showUpdateInvoicePage(@PathVariable long id, Model model) {

        InvoiceDTO invoiceDTO = invoiceService.findInvoiceDTOById(id);

        model.addAttribute("flat", invoiceDTO.getApartment());
        model.addAttribute("id", id);
        model.addAttribute("invoiceDTO", invoiceDTO);
        model.addAttribute("meters", meterDataService.findSingleMeterData(invoiceDTO.getApartment().getId(), null));
        model.addAttribute("current_date", LocalDate.now());
        return "admin_panel/invoices/invoice_card";
    }

    @PostMapping("/create")
    public String createInvoice(@ModelAttribute InvoiceDTO invoiceDTO,
                                BindingResult bindingResult,
                                @RequestParam String date,
                                @RequestParam String[] services,
                                @RequestParam String[] unit_prices,
                                @RequestParam String[] unit_amounts,
                                Model model) {
        log.info(invoiceDTO.toString());

        invoiceDTO = invoiceService.buildInvoice(invoiceDTO, date, services, unit_prices, unit_amounts);

        validator.validate(invoiceDTO, bindingResult);
        if(bindingResult.hasErrors()) {
            log.info("Errors found");
            log.info(bindingResult.getAllErrors().toString());
            model.addAttribute("id", invoiceService.getMaxInvoiceId()+1L);
            model.addAttribute("meters",  meterDataService.findAllMeters());
            model.addAttribute("current_date", LocalDate.now());
            model.addAttribute("validation", "failed");
            return "admin_panel/invoices/invoice_card";
        }

        Invoice invoice = invoiceService.saveInvoice(invoiceDTO);

        websocketController.sendInvoiceItem(invoice);

        return "redirect:/admin/invoices";
    }

    @PostMapping("/update/{id}")
    public String updateInvoice(@PathVariable long id,
                                @ModelAttribute InvoiceDTO invoiceDTO,
                                BindingResult bindingResult,
                                @RequestParam String date,
                                @RequestParam String[] services,
                                @RequestParam String[] unit_prices,
                                @RequestParam String[] unit_amounts,
                                Model model) {

        log.info(invoiceDTO.toString());

        invoiceDTO = invoiceService.buildInvoice(invoiceDTO, date, services, unit_prices, unit_amounts);

        validator.validate(invoiceDTO, bindingResult);
        if(bindingResult.hasErrors()) {
            log.info("Errors found");
            log.info(bindingResult.getAllErrors().toString());
            model.addAttribute("id", invoiceService.getMaxInvoiceId()+1L);
            model.addAttribute("meters",  meterDataService.findAllMeters());
            model.addAttribute("current_date", LocalDate.now());
            model.addAttribute("validation", "failed");
            return "admin_panel/invoices/invoice_card";
        }

        Invoice invoice = invoiceService.saveInvoice(invoiceDTO);

        websocketController.sendInvoiceItem(invoice);

        return "redirect:/admin/invoices";
    }

    @GetMapping("/delete/{id}")
    public String deleteInvoice(@PathVariable long id) {
        invoiceService.deleteInvoiceById(id);
        return "redirect:/admin/invoices";
    }

    @GetMapping("/delete-invoice")
    public @ResponseBody String deleteInvoiceFromTable(@RequestParam long id) {
        invoiceService.deleteInvoiceById(id);
        return "Удалил квитанцию с ID " + id;
    }

    @GetMapping("/print/{id}")
    public String getPrintPage(@PathVariable long id, Model model) {
        Invoice invoice = invoiceService.findInvoiceById(id);
        model.addAttribute("invoice", invoice);
        model.addAttribute("default_template", invoiceService.findDefaultTemplate());
        model.addAttribute("templates", invoiceService.findAllTemplates());
        return "admin_panel/invoices/invoice_print";
    }

    @PostMapping("/print/{id}")
    public String printInvoice(@PathVariable long id, @RequestParam String template, RedirectAttributes redirectAttributes) {
        Invoice invoice = invoiceService.findInvoiceById(id);
        InvoiceTemplate invoiceTemplate = invoiceService.findTemplateById(Long.parseLong(template));
        try {
            String fileName = invoiceService.turnInvoiceIntoExcel(invoice, invoiceTemplate);
            log.info(fileName);
            log.info("HELLOY");
            return "redirect:/admin/invoices/download/" + fileName;
        } catch (IOException e) {
            log.severe("Error while creating excel file");
            redirectAttributes.addFlashAttribute("fail", "Загрузка файла не удалась");
            return "redirect:/admin/invoices/print/" + id;
        }
    }

    @PostMapping("/email/{id}")
    public @ResponseBody String sendInvoiceToEmail(@PathVariable long id, @RequestParam String template) {
        Invoice invoice = invoiceService.findInvoiceById(id);
        InvoiceTemplate invoiceTemplate = invoiceService.findTemplateById(Long.parseLong(template));
        try {
            String fileName = invoiceService.turnInvoiceIntoExcel(invoice, invoiceTemplate);
            log.info(fileName);
            String emailForSending = invoice.getOwner().getEmail();
            return fileDownloadUtil.sendFileToEmail(emailForSending, fileName);
        } catch (IOException e) {
            log.severe("Error while creating excel file");
            return "Error";
        }
    }

    @GetMapping("/send-to-email/{fileName}")
    public void sendFileToEmail(@PathVariable String fileName,
                                HttpServletRequest request,
                                HttpServletResponse response) throws IOException {

    }


    @GetMapping("/download/{fileName}")
    public void downloadFile(@PathVariable String fileName,
                               HttpServletRequest request,
                               HttpServletResponse response) throws IOException {
        fileDownloadUtil.downloadInvoice(response, fileName);
    }

    @GetMapping("/template")
    public String getTemplateSettingPage(Model model,
                                         @RequestParam(required = false) Long default_id,
                                         @RequestParam(required = false) Long delete_id){

        if(default_id != null) {
            invoiceService.setDefaultTemplate(invoiceService.findTemplateById(default_id));
            return "redirect:/admin/invoices/template";
        } else if(delete_id != null) {
            invoiceService.deleteTemplateById(delete_id);
            return "redirect:/admin/invoices/template";
        }
        model.addAttribute("default_template", invoiceService.findDefaultTemplate());
        model.addAttribute("templates", invoiceService.findAllTemplates());
        return "admin_panel/invoices/invoice_template_settings";
    }

    @PostMapping("/template")
    public String saveTemplate(@RequestParam String name,
                               @RequestParam MultipartFile file,
                               RedirectAttributes redirectAttributes) throws IOException {
        InvoiceTemplate template = new InvoiceTemplate();
        if(!name.isEmpty()) template.setName(name);
        else {
            redirectAttributes.addFlashAttribute("name_fail", "Missing name");
            return "redirect:/admin/invoices/template";
        }
        if(file.getSize() > 0) {
            String file_extension = FilenameUtils.getExtension(file.getOriginalFilename());
            log.info(file_extension);
            assert file_extension != null;
            if(file_extension.equalsIgnoreCase("xls") || file_extension.equalsIgnoreCase("xlsx")) {
                fileUploadUtil.saveFile("/files/", file.getOriginalFilename(), file);
                template.setFile(file.getOriginalFilename());
            }
        } else {
            redirectAttributes.addFlashAttribute("file_fail", "Missing file");
            return "redirect:/admin/invoices/template";
        }
        invoiceService.saveTemplate(template);
        return "redirect:/admin/invoices/template";
    }

    @GetMapping("/get-invoices")
    public @ResponseBody Page<InvoiceDTO> getInvoices(@RequestParam Integer page,
                                                   @RequestParam Integer size,
                                                   @RequestParam String filters) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        FilterForm form = mapper.readValue(filters, FilterForm.class);
        return invoiceService.findAllInvoicesByFiltersAndPage(form, PageRequest.of(page-1, size));
    }

    @GetMapping("/get-filtered-invoice-count")
    public @ResponseBody Long getFilteredInvoiceCount(@RequestParam String f_string) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        FilterForm filters = mapper.readValue(f_string, FilterForm.class);
        return invoiceService.getFilteredInvoiceCount(filters);
    }

    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("buildings", buildingService.findAll());
        model.addAttribute("tariffs", tariffService.findAllTariffs());
        model.addAttribute("services", serviceService.findAllServices());

        model.addAttribute("invoicesPageActive", true);
    }
}
