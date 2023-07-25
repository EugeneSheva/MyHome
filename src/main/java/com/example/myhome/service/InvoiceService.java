package com.example.myhome.service;

import com.example.myhome.dto.InvoiceDTO;
import com.example.myhome.model.Invoice;
import com.example.myhome.model.InvoiceComponents;
import com.example.myhome.model.InvoiceTemplate;
import com.example.myhome.model.filter.FilterForm;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

public interface InvoiceService {

    List<Invoice> findAllInvoices();

    List<Invoice> findAllByPage(Integer page, Integer page_size);

    List<Invoice> findAllBySpecification(FilterForm filters);

    Page<Invoice> findAllBySpecificationAndPage(FilterForm filters, Integer page, Integer page_size);

    Page<InvoiceDTO> findAllBySpecificationAndPageCabinet(FilterForm filters, Integer page, Integer size);

    Page<InvoiceDTO> findAllInvoicesByFiltersAndPage(FilterForm filters, Pageable pageable);

    List<Invoice> findAllByApartmentId(Long id);

    List<Invoice> findAllByOwnerId(Long id);

    Invoice findInvoiceById(Long invoice_id);

    InvoiceDTO findInvoiceDTOById(Long invoice_id);

    Specification<Invoice> buildSpecFromFilters(FilterForm filters);

    List<InvoiceTemplate> findAllTemplates();

    InvoiceTemplate findDefaultTemplate();

    InvoiceTemplate findTemplateById(Long template_id);

    void setDefaultTemplate(InvoiceTemplate template);

    Long getMaxInvoiceId();

    Long count();

    Long countTemplates();

    @Transactional
    Invoice saveInvoice(Invoice invoice);

    Invoice saveInvoice(InvoiceDTO dto);

    InvoiceTemplate saveTemplate(InvoiceTemplate template);

    List<InvoiceTemplate> saveAllTemplates(List<InvoiceTemplate> list);

    List<Invoice> saveAllInvoices(List<Invoice> list);

    void deleteInvoiceById(Long invoice_id);

    void deleteTemplateById(Long template_id);

    InvoiceDTO buildInvoice(InvoiceDTO invoice,
                            String date,
                            String[] services,
                            String[] unit_prices,
                            String[] unit_amounts);

    List<InvoiceComponents> buildComponents(String[] services,
                                            String[] unit_prices,
                                            String[] unit_amounts);

    Long getFilteredInvoiceCount(FilterForm filters);

    String turnInvoiceIntoExcel(Invoice invoice, InvoiceTemplate template) throws IOException;

    List<Double> getListSumInvoicesByMonth();

    List<Double> getListSumPaidInvoicesByMonth();

    List<String> getListOfMonthName();

    List<Double> getListExpenseByApartmentByMonth(Long id);

    Double getAverageTotalPriceForApartmentLastYear(Long apartment);

    void sendExcelInvoiceToEmail();

}
