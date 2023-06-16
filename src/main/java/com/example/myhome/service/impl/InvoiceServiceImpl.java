package com.example.myhome.service.impl;

import com.example.myhome.dto.InvoiceDTO;
import com.example.myhome.exception.NotFoundException;
import com.example.myhome.mapper.InvoiceDTOMapper;
import com.example.myhome.model.filter.FilterForm;
import com.example.myhome.repository.AccountRepository;
import com.example.myhome.repository.InvoiceRepository;
import com.example.myhome.repository.InvoiceTemplateRepository;
import com.example.myhome.specification.InvoiceSpecifications;
import com.example.myhome.model.*;
import com.example.myhome.service.*;
import com.example.myhome.util.ExcelHelper;
import lombok.extern.java.Log;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Log
public class InvoiceServiceImpl implements InvoiceTemplateService, InvoiceService {

    @Autowired private InvoiceRepository invoiceRepository;
    @Autowired private InvoiceTemplateRepository invoiceTemplateRepository;
    @Autowired private InvoiceDTOMapper mapper;

    @Autowired private ApartmentService apartmentService;
    @Autowired private AccountRepository accountRepository;
    @Autowired private EmailService emailService;
    @Autowired private OwnerService ownerService;
    @Autowired private ServiceService serviceService;

    @Autowired private ExcelHelper excelHelper;


    private static final String FILE_PATH = "C:\\Users\\OneSmiLe\\IdeaProjects\\MyHome\\src\\main\\resources\\static\\files\\";

    @Override
    public List<Invoice> findAllInvoices() {
        log.info("Getting all invoices from DB");
        List<Invoice> list = invoiceRepository.findAll();
        log.info("Found " + list.size() + " elements");
        return list;
    }

    @Override
    public List<Invoice> findAllByPage(Integer page, Integer page_size) {
        return invoiceRepository.findAll(PageRequest.of(page, page_size)).toList();
    }

    @Override
    public List<Invoice> findAllBySpecification(FilterForm filters) {
        log.info("Filters found!");
        log.info(filters.toString());

        Specification<Invoice> specification = buildSpecFromFilters(filters);

        return invoiceRepository.findAll(specification);
    }

    @Override
    public Page<Invoice> findAllBySpecificationAndPage(FilterForm filters, Integer page, Integer page_size) {
        Specification<Invoice> specification = buildSpecFromFilters(filters);

        Pageable pageable = PageRequest.of(page, page_size);

        return invoiceRepository.findAll(specification, pageable);
    }

    @Override
    public Page<InvoiceDTO> findAllBySpecificationAndPageCabinet(FilterForm filters, Integer page, Integer size) {
        List<InvoiceDTO> listDTO = new ArrayList<>();
        Pageable pageable = PageRequest.of(page-1, size);
        Page<Invoice>invoiceList = invoiceRepository.findByFilters(filters.getDate() != null ? LocalDate.parse(filters.getDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")) : null, filters.getStatus() != null ? InvoiceStatus.valueOf(filters.getStatus()) : null, pageable);
        invoiceList.forEach(inv -> listDTO.add(mapper.fromInvoiceToDTO(inv)));

        return new PageImpl<>(listDTO, pageable, invoiceList.getTotalElements());
    }

    @Override
    public Page<InvoiceDTO> findAllInvoicesByFiltersAndPage(FilterForm filters, Pageable pageable) {
        log.info("Getting invoices (page " + pageable.getPageNumber() + "/size " + pageable.getPageSize() + ")");
        Specification<Invoice> specification = buildSpecFromFilters(filters);
        Page<Invoice> page = invoiceRepository.findAll(specification, pageable);
        log.info("Found " + page.getContent().size() + " elements");
        List<InvoiceDTO> list = page.getContent().stream().map(mapper::fromInvoiceToDTO).collect(Collectors.toList());
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    @Override
    public List<Invoice> findAllByApartmentId(Long id) {
        return invoiceRepository.findAllByApartmentId(id);
    }

    @Override
    public List<Invoice> findAllByOwnerId(Long id) {
        return invoiceRepository.findAllByOwnerId(id);
    }

    @Override
    public Invoice findInvoiceById(Long invoice_id) {
        log.info("Searching for invoice with ID: " + invoice_id);
        try {
            Invoice invoice = invoiceRepository.findById(invoice_id).orElseThrow(NotFoundException::new);
            log.info("Invoice found! " + invoice);
            return invoice;
        } catch (NotFoundException e) {
            log.severe("Invoice not found...");
            log.severe(e.getMessage());
            return null;
        }
    }

    @Override
    public InvoiceDTO findInvoiceDTOById(Long invoice_id) {
        Invoice invoice = findInvoiceById(invoice_id);
        return mapper.fromInvoiceToDTO(invoice);
    }

    @Override
    public Specification<Invoice> buildSpecFromFilters(FilterForm filters) {

        log.info("Building specification from filters: " + filters.toString());

        Long id = filters.getId();
        InvoiceStatus status = (filters.getStatus() != null) ? InvoiceStatus.valueOf(filters.getStatus()) : null;
        Long apartment = filters.getApartment() != null ? filters.getApartment() : null;
        Owner owner = (filters.getOwner() != null) ? ownerService.findById(filters.getOwner()) : null;
        Boolean completed = filters.getCompleted();

        String month = filters.getMonth();

        log.info(month);

        LocalDateTime monthTime = null;
        LocalDate m = null, m2 = null;
        if(month != null) {
            monthTime = LocalDateTime.parse(month, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            m = monthTime.toLocalDate();
            m2 = m.withMonth(m.getMonthValue()+1);
        }

        String date = filters.getDatetime();
        LocalDate date_from = null;
        LocalDate date_to = null;
        if(date != null && !date.isEmpty()) {
            date_from = LocalDate.parse(date.split(" to ")[0]);
            date_to = LocalDate.parse(date.split(" to ")[1]);
        }

        Specification<Invoice> spec = Specification.where(InvoiceSpecifications.hasId(id)
                                    .and(InvoiceSpecifications.hasStatus(status))
                                    .and(InvoiceSpecifications.hasApartmentNumber(apartment))
                                    .and(InvoiceSpecifications.hasOwner(owner))
                                    .and(InvoiceSpecifications.isCompleted(completed))
                                    .and(InvoiceSpecifications.datesBetween(date_from, date_to))
                                    .and(InvoiceSpecifications.datesBetween(m, m2)));

        log.info("Specification ready! " + spec);
        return spec;
    }

    @Override
    public List<InvoiceTemplate> findAllTemplates() {
        log.info("Getting all invoice templates from DB");
        List<InvoiceTemplate> list = invoiceTemplateRepository.findAll();
        log.info("Found " + list.size() + " elements");
        return list;
    }

    @Override
    public InvoiceTemplate findDefaultTemplate() {
        log.info("Getting default template...");
        Optional<InvoiceTemplate> opt = invoiceTemplateRepository.getDefaultTemplate();
        if(opt.isEmpty()) {
            log.warning("Default template doesn't exist");
            return null;
        }
        else {
            log.info("Template found! " + opt.get());
            return opt.get();
        }
    }

    @Override
    public InvoiceTemplate findTemplateById(Long template_id) {
        log.info("Searching for invoice template with ID: " + template_id);
        try {
            InvoiceTemplate template = invoiceTemplateRepository.findById(template_id).orElseThrow(NotFoundException::new);
            log.info("Template found! " + template);
            return template;
        } catch (NotFoundException e) {
            log.severe("Template not found!");
            return null;
        }
    }

    @Override
    public void setDefaultTemplate(InvoiceTemplate template) {
        log.info("Setting template with ID " + template.getId() + " as default");
        try {
            Optional<InvoiceTemplate> defaultTemplate = invoiceTemplateRepository.getDefaultTemplate();
            if(defaultTemplate.isPresent()) {
                defaultTemplate.get().setDefault(false);
                invoiceTemplateRepository.save(defaultTemplate.get());
            }
            template.setDefault(true);
            invoiceTemplateRepository.save(template);
            log.info("Template saved as default!");
        } catch (Exception e) {
            log.severe("Something went wrong");
            log.severe(e.getMessage());
        }
    }

    @Override
    public Long getMaxInvoiceId() {
        Long maxID = invoiceRepository.getMaxId().orElse(0L);
        log.info("Getting last invoice ID: " + maxID);
        return maxID;
    }

    @Override
    public Long count() {
        return invoiceRepository.count();
    }

    @Override
    @Transactional
    public Invoice saveInvoice(Invoice invoice) {
        log.info("Trying to save invoice...");
        log.info(invoice.toString());
        try {
            Invoice savedInvoice = invoiceRepository.save(invoice);
            log.info("Invoice successfully saved!");
            log.info(savedInvoice.toString());

            log.info("Trying to update account balance now");
            ApartmentAccount account = accountRepository.findById(savedInvoice.getAccount().getId()).orElse(null);
            if(account != null) {
                log.info("Found account: " + account);
                account.setBalance(account.getBalance() - savedInvoice.getTotal_price());
                log.info("Updated balance: " + account.getBalance());
                accountRepository.save(account);
                log.info("Saved account!");
            }

            return savedInvoice;
        } catch (Exception e) {
            log.severe("Something went wrong during saving");
            log.severe(e.getMessage());
            return null;
        }
    }

    @Override
    public Invoice saveInvoice(InvoiceDTO dto) {
        Invoice invoice = mapper.fromDTOToInvoice(dto);
        return saveInvoice(invoice);
    }

    @Override
    public InvoiceTemplate saveTemplate(InvoiceTemplate template) {
        log.info("Trying to save invoice template...");
        log.info(template.toString());
        try {
            InvoiceTemplate savedTemplate = invoiceTemplateRepository.save(template);
            log.info("Template successfully saved!");
            log.info(savedTemplate.toString());
            return savedTemplate;
        } catch (Exception e) {
            log.severe("Something went wrong during saving");
            log.severe(e.getMessage());
            return null;
        }
    }

    @Override
    public List<InvoiceTemplate> saveAllTemplates(List<InvoiceTemplate> list) {
        log.info("Trying to save a list of templates!");
        log.info(list.toString());
        try {
            List<InvoiceTemplate> savedTemplates = invoiceTemplateRepository.saveAll(list);
            log.info("Templates successfully saved!");
            log.info(savedTemplates.toString());
            return savedTemplates;
        } catch (Exception e) {
            log.severe("Something went wrong during saving");
            log.severe(e.getMessage());
            return null;
        }
    }

    @Override
    public List<Invoice> saveAllInvoices(List<Invoice> list) {
        log.info("Trying to save a list of invoices!");
        log.info(list.toString());
        try {
            List<Invoice> savedInvoices = invoiceRepository.saveAll(list);
            log.info("Invoices successfully saved!");
            log.info(savedInvoices.toString());
            return savedInvoices;
        } catch (Exception e) {
            log.severe("Something went wrong during saving");
            log.severe(e.getMessage());
            return null;
        }
    }

    @Override
    public void deleteInvoiceById(Long invoice_id) {
        log.info("Trying to delete invoice with ID: " + invoice_id);
        try {
            Invoice invoice = findInvoiceById(invoice_id);
            log.info("Invoice found!");
            invoice.getComponents().clear();
            log.info("Components cleared from invoice");
            invoiceRepository.delete(invoice);
            log.info("Invoice with ID: " + invoice_id + " successfully deleted");
        } catch (Exception e) {
            log.severe("Something went wrong during deletion");
            log.severe(e.getMessage());
        }
    }

    @Override
    public void deleteTemplateById(Long template_id) {
        log.info("Trying to delete invoice template with ID: " + template_id);
        try {
            InvoiceTemplate template = findTemplateById(template_id);
            log.info("Invoice template found!");
            invoiceTemplateRepository.delete(template);
            log.info("Invoice template with ID: " + template_id + " successfully deleted");
        } catch (Exception e) {
            log.severe("Something went wrong during deletion");
            log.severe(e.getMessage());
        }
    }


    @Override
    public InvoiceDTO buildInvoice(InvoiceDTO invoice,
                                   String date,
                                   String[] services,
                                   String[] unit_prices,
                                   String[] unit_amounts) {

        log.info("Building invoice!");
        invoice.setDate(LocalDate.parse(date));
        log.info("Set date " + LocalDate.parse(date));
        log.info("Clearing old components list, if present");
        if(invoice.getComponents() != null) invoice.getComponents().clear();
        else invoice.setComponents(new ArrayList<>());
        log.info("Old components cleared");
        log.info("Adding components");
        List<InvoiceComponents> componentsList = buildComponents(services, unit_prices, unit_amounts);
        componentsList.forEach(comp -> log.info(String.valueOf(comp.getTotalPrice())));
        invoice.setComponents(componentsList);
        Double total_price = componentsList.stream().map(InvoiceComponents::getTotalPrice).reduce(Double::sum).orElse(0.0);
        log.info(String.valueOf(total_price));
        invoice.setTotal_price(total_price);
        log.info("Total price of an invoice: " + total_price);
        log.info("Built invoice!");
        log.info(invoice.toString());

        return invoice;
    }

    @Override
    public List<InvoiceComponents> buildComponents(String[] services,
                                                   String[] unit_prices,
                                                   String[] unit_amounts) {

        List<InvoiceComponents> list = new ArrayList<>();

        for (int i = 1; i < services.length; i++) {

            if(services[i].isEmpty() || unit_amounts[i].isEmpty() || unit_prices[i].isEmpty()) continue;
            try {
                Double.parseDouble(unit_prices[i]);
                Double.parseDouble(unit_amounts[i]);
            } catch (NumberFormatException e) {
                log.info("Incorrect component found, skipping");
                continue;
            }

            InvoiceComponents component = new InvoiceComponents();
            component.setService(serviceService.findServiceById(Long.parseLong(services[i])));
            component.setUnit_price(Double.parseDouble(unit_prices[i]));
            component.setUnit_amount(Double.parseDouble(unit_amounts[i]));
            list.add(component);
        }
        log.info("List of new components created!");

        return list;
    }


    @Override
    public Long getFilteredInvoiceCount(FilterForm filters) {

        Specification<Invoice> spec = buildSpecFromFilters(filters);

        return invoiceRepository.count(spec);
    }

    @Override
    public String turnInvoiceIntoExcel(Invoice invoice, InvoiceTemplate template) throws IOException {

        return excelHelper.turnInvoiceIntoExcel(invoice, template);

    }

    @Override
    public List<Double> getListSumInvoicesByMonth() {
        List<Double>doubleList = new ArrayList<>();
        LocalDate now = LocalDate.now();
        LocalDate begin = now.minusMonths(11);
        for (int i = 0; i < 12; i++) {
            Double tmp = invoiceRepository.getSumIvoice(begin.getMonthValue(), begin.getYear());
            if (tmp == null) tmp=0D;
            doubleList.add(tmp);
            begin = begin.plusMonths(1);
        }
        return doubleList;
    }
    @Override
    public List<Double> getListSumPaidInvoicesByMonth() {
        List<Double>doubleList = new ArrayList<>();
        LocalDate now = LocalDate.now();
        LocalDate begin = now.minusMonths(11);
        for (int i = 0; i < 12; i++) {
            Double tmp = invoiceRepository.getSumPaidIvoice(begin.getMonthValue(), begin.getYear(), InvoiceStatus.PAID);
            if (tmp == null) tmp=0D;
//            if (tmp <0) tmp= tmp*-1;
            doubleList.add(tmp);
            begin = begin.plusMonths(1);
        }
        return doubleList;
    }
    @Override
    public List<String> getListOfMonthName() {
        List<String>doubleList = new ArrayList<>();
        LocalDate now = LocalDate.now();
        LocalDate begin = now.minusMonths(11);
        for (int i = 0; i < 12; i++) {
            String tmp = begin.getMonth().name() + " " + begin.getYear();
            doubleList.add(tmp);
            begin = begin.plusMonths(1);
        }
        return doubleList;
    }

    @Override
    public List<Double> getListExpenseByApartmentByMonth(Long id) {
        List<Double>doubleList = new ArrayList<>();
        LocalDate now = LocalDate.now();
        LocalDate begin = now.minusMonths(11);
        for (int i = 0; i < 12; i++) {
            Double tmp = invoiceRepository.getTotalPriceByApartmentIdAndMonthAndYear(id, begin.getMonthValue(), begin.getYear());
            if (tmp == null) tmp=0D;
            doubleList.add(tmp);
            begin = begin.plusMonths(1);
        }
        return doubleList;
    }

    @Override
    public Double getAverageTotalPriceForApartmentLastYear(Long apartment) {
        LocalDate lastYear = LocalDate.now().minusYears(1);
        LocalDate today = LocalDate.now();
        return invoiceRepository.getAverageTotalPriceForApartmentBetwenDate(apartment, lastYear, today);
    }

    @Override
    public void sendExcelInvoiceToEmail() {
        System.out.println("brr");
    }

}
