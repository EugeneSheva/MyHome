package com.example.myhome.service;

import com.example.myhome.dto.InvoiceDTO;
import com.example.myhome.model.Invoice;
import com.example.myhome.model.InvoiceTemplate;
import com.example.myhome.model.filter.FilterForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface InvoiceTemplateService {

    List<InvoiceTemplate> findAllTemplates();
    InvoiceTemplate findDefaultTemplate();

    InvoiceTemplate findTemplateById(Long template_id);

    void setDefaultTemplate(InvoiceTemplate template);

    InvoiceTemplate saveTemplate(InvoiceTemplate template);

    void deleteTemplateById(Long template_id);

    List<InvoiceTemplate> saveAllTemplates(List<InvoiceTemplate> list);
    public Page<InvoiceDTO> findAllInvoicesByFiltersAndPage(FilterForm filters, Pageable pageable);
    public Invoice findInvoiceById(Long invoice_id);

    public void deleteInvoiceById(Long invoice_id);

}
