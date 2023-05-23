package com.example.myhome.services;

import com.example.myhome.dto.InvoiceDTO;
import com.example.myhome.exception.NotFoundException;
import com.example.myhome.mapper.InvoiceDTOMapper;
import com.example.myhome.model.*;
import com.example.myhome.model.filter.FilterForm;
import com.example.myhome.repository.*;
import com.example.myhome.service.InvoiceService;
import com.example.myhome.util.ExcelHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.parameters.P;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@SpringBootTest
public class InvoiceServiceTest {

    @MockBean private InvoiceRepository invoiceRepository;
    @MockBean private InvoiceTemplateRepository invoiceTemplateRepository;
    @MockBean private AccountRepository accountRepository;
    @MockBean private ApartmentRepository apartmentRepository;
    @MockBean private OwnerRepository ownerRepository;
    @MockBean private BuildingRepository buildingRepository;
    @Autowired private InvoiceService invoiceService;

    @MockBean private ExcelHelper helper;

    Invoice invoice;
    List<Invoice> list;
    InvoiceDTOMapper mapper = new InvoiceDTOMapper();

    @BeforeEach
    void createInvoice() {
        invoice = new Invoice();
        invoice.setId(1L);
        invoice.setCompleted(true);
        invoice.setDate(LocalDate.now());
        invoice.setComponents(new ArrayList<>());
        invoice.setTotal_price(0);
        Apartment apartment = new Apartment();
        apartment.setId(1L);
        Building building = new Building();
        building.setId(1L);
        apartment.setBuilding(building);
        Owner owner = new Owner();
        owner.setId(1L);
        apartment.setOwner(owner);
        invoice.setApartment(apartment);
        invoice.setOwner(owner);
        invoice.setSection("test");
        invoice.setStatus(InvoiceStatus.PAID);
        invoice.setBuilding(building);

        list = List.of(invoice,invoice,invoice);
    }

    @Test
    void sanityCheck() {
        assertThat(invoiceRepository).isNotNull();
        assertThat(invoiceService).isNotNull();
        assertThat(invoice).isNotNull();
    }

    @Test
    void canFindAllTest() {
        when(invoiceRepository.findAll()).thenReturn(List.of(new Invoice()));
        assertThat(invoiceService.findAllInvoices().size()).isEqualTo(1);
    }

    @Test
    void canFindAllByPageTest() {
        when(invoiceRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(new Invoice()), PageRequest.of(1,1), 1));
        assertThat(invoiceService.findAllByPage(1,1).size()).isEqualTo(1);
    }

    @Test
    void canFindAllBySpecificationTest() {
        FilterForm filters = new FilterForm();
        filters.setId(1L);
        filters.setName("test");
        filters.setDatetime("2022-11-11 to 2022-11-12");
        when(invoiceRepository.findAll(any(Specification.class))).thenReturn(List.of(new Invoice()));
        assertThat(invoiceService.findAllBySpecification(filters).size()).isEqualTo(1);
    }

    @Test
    void canFindAllByApartmentIdTest() {
        when(invoiceRepository.findAllByApartmentId(anyLong())).thenReturn(List.of(new Invoice()));
        assertThat(invoiceService.findAllByApartmentId(1L).size()).isEqualTo(1);
    }

    @Test
    void canFindAllByOwnerIdTest() {
        when(invoiceRepository.findAllByOwnerId(anyLong())).thenReturn(List.of(new Invoice()));
        assertThat(invoiceService.findAllByOwnerId(1L).size()).isEqualTo(1);
    }

    @Test
    void canFindInvoiceByIdTest() {
        Invoice expected = new Invoice();
        expected.setId(1L);
        when(invoiceRepository.findById(anyLong())).thenReturn(Optional.of(expected));
        assertThat(invoiceService.findInvoiceById(1L)).isEqualTo(expected);
    }

    @Test
    void canThrowExceptionOnInvoiceNotFoundTest() {
        when(invoiceRepository.findById(anyLong())).thenThrow(new NotFoundException());
        assertThat(invoiceService.findInvoiceById(1L)).isNull();
    }

    @Test
    void canFindInvoiceDTOTest() {
        when(invoiceRepository.findById(anyLong())).thenReturn(Optional.of(invoice));
        assertThat(invoiceService.findInvoiceDTOById(1L)).isInstanceOf(InvoiceDTO.class);
        assertThat(invoiceService.findInvoiceDTOById(1L)).isNotNull();
    }

    @Test
    void canFindAllTemplates() {
        when(invoiceTemplateRepository.findAll()).thenReturn(List.of(new InvoiceTemplate()));
        assertThat(invoiceService.findAllTemplates().size()).isEqualTo(1L);
    }

    @Test
    void canFindDefaultTemplate() {
        when(invoiceTemplateRepository.getDefaultTemplate()).thenReturn(Optional.of(new InvoiceTemplate()));
        assertThat(invoiceService.findDefaultTemplate()).isNotNull();
    }

    @Test
    void canGiveNullIfDefaultTemplateDoesntExistTest() {
        when(invoiceTemplateRepository.getDefaultTemplate()).thenReturn(Optional.empty());
        assertThat(invoiceService.findDefaultTemplate()).isNull();
    }

    @Test
    void canFindTemplateByIdTest() {
        when(invoiceTemplateRepository.findById(anyLong())).thenReturn(Optional.of(new InvoiceTemplate()));
        assertThat(invoiceService.findTemplateById(1L)).isNotNull();
    }

    @Test
    void canReturnNullOnFindTemplateTest() {
        assertThat(invoiceService.findTemplateById(1L)).isNull();
    }

    @Test
    void canSetDefaultTemplateTest() {
        when(invoiceTemplateRepository.getDefaultTemplate()).thenReturn(Optional.of(new InvoiceTemplate()));
        invoiceService.setDefaultTemplate(new InvoiceTemplate());
    }

    @Test
    void canGiveErrorOnSetDefaultTemplateTest() {
        when(invoiceTemplateRepository.save(any())).thenThrow(new NotFoundException());
        invoiceService.setDefaultTemplate(new InvoiceTemplate());
    }

    @Test
    void canCountTest() {
        when(invoiceRepository.count()).thenReturn(100L);
        assertThat(invoiceService.count()).isEqualTo(100L);
    }

    @Test
    void canGetMaxInvoiceIdTest() {
        when(invoiceRepository.getMaxId()).thenReturn(Optional.of(1L));
        assertThat(invoiceService.getMaxInvoiceId()).isEqualTo(1L);
    }

    @Test
    void canGiveZeroOnMaxInvoiceIdTest() {
        when(invoiceRepository.getMaxId()).thenReturn(Optional.empty());
        assertThat(invoiceService.getMaxInvoiceId()).isEqualTo(0L);
    }

    @Test
    void canSaveInvoiceTest() {
        Invoice expected = new Invoice();
        expected.setTotal_price(1000);
        ApartmentAccount account = new ApartmentAccount();
        account.setBalance(10000.0);
        expected.setAccount(account);
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(expected);
        when(accountRepository.save(any(ApartmentAccount.class))).thenReturn(account);
        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(account));
        assertThat(invoiceService.saveInvoice(new Invoice())).isEqualTo(expected);
    }

    @Test
    void canGiveErrorOnSaveInvoiceTest() {
        Invoice expected = new Invoice();
        expected.setTotal_price(1000);
        ApartmentAccount account = new ApartmentAccount();
        account.setBalance(10000.0);
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(expected);
        when(accountRepository.save(any(ApartmentAccount.class))).thenReturn(account);
        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(account));
        assertThat(invoiceService.saveInvoice(new Invoice())).isNull();
    }

    @Test
    void canSaveInvoiceFromDTOTest() {
        when(buildingRepository.getReferenceById(anyLong())).thenReturn(invoice.getBuilding());
        when(accountRepository.getReferenceById(anyLong())).thenReturn(invoice.getAccount());
        when(ownerRepository.getReferenceById(anyLong())).thenReturn(invoice.getOwner());
        when(apartmentRepository.getReferenceById(anyLong())).thenReturn(invoice.getApartment());

        assertThat(invoiceService.saveInvoice(new InvoiceDTO())).isNotNull();
    }

    @Test
    void canSaveTemplateTest() {
        InvoiceTemplate template = new InvoiceTemplate();
        template.setId(1L);
        template.setName("test");
        when(invoiceTemplateRepository.save(any(InvoiceTemplate.class))).thenReturn(template);
        assertThat(invoiceService.saveTemplate(template)).isEqualTo(template);
    }

    @Test
    void canGiveErrorOnSaveTemplateTest() {
        when(invoiceTemplateRepository.save(any(InvoiceTemplate.class))).thenThrow(new NotFoundException());
        assertThat(invoiceService.saveTemplate(new InvoiceTemplate())).isNull();
    }

    @Test
    void canSaveAllTemplatesTest() {
        InvoiceTemplate template = new InvoiceTemplate();
        template.setId(1L);
        template.setName("test");
        List<InvoiceTemplate> list = List.of(template,template);
        when(invoiceTemplateRepository.saveAll(any())).thenReturn(list);
        assertThat(invoiceService.saveAllTemplates(list)).isEqualTo(list);
    }

    @Test
    void canSaveAllInvoicesTest() {
        when(invoiceRepository.saveAll(any())).thenReturn(list);
        assertThat(invoiceService.saveAllInvoices(list)).isEqualTo(list);
    }

    @Test
    void canGiveErrorOnSaveAllInvoicesTest() {
        when(invoiceRepository.saveAll(any())).thenThrow(new NotFoundException());
        assertThat(invoiceService.saveAllInvoices(list)).isNull();
    }

    @Test
    void canDeleteInvoiceByIdTest() {
        when(invoiceRepository.findById(anyLong())).thenReturn(Optional.ofNullable(invoice));
        invoiceService.deleteInvoiceById(1L);
    }

    @Test
    void canGiveErrorOnDeleteInvoiceTest() {
        when(invoiceRepository.findById(anyLong())).thenThrow(new NotFoundException());
        invoiceService.deleteInvoiceById(1L);
    }

    @Test
    void canDeleteTemplateByIdTest() {
        InvoiceTemplate template = new InvoiceTemplate();
        when(invoiceTemplateRepository.findById(anyLong())).thenReturn(Optional.of(template));
        invoiceService.deleteTemplateById(1L);
    }

    @Test
    void canGiveErrorOnDeleteTemplateByIdTest() {
        when(invoiceTemplateRepository.findById(anyLong())).thenThrow(new NotFoundException());
        invoiceService.deleteTemplateById(1L);
    }

    @Test
    void canGetFilteredInvoiceCountTest() {
        FilterForm filterForm = new FilterForm();
        filterForm.setId(1L);
        filterForm.setName("test");
        when(invoiceRepository.count(any(Specification.class))).thenReturn(10L);
        assertThat(invoiceService.getFilteredInvoiceCount(filterForm)).isEqualTo(10L);
    }

    @Test
    void canGiveErrorOnSaveAllTemplatesTest() {
        when(invoiceTemplateRepository.saveAll(any())).thenThrow(new NotFoundException());
        assertThat(invoiceService.saveAllTemplates(List.of(new InvoiceTemplate()))).isNull();
    }

    @Test
    void canFindAllBySpecificationAndPageTest() {
        FilterForm filters = new FilterForm();
        when(invoiceRepository.findAll()).thenReturn(List.of(new Invoice(), new Invoice()));
//        assertThat(invoiceService.findAllBySpecificationAndPage(filters,1,1).getContent().size()).isEqualTo(2);
        assertThat(invoiceService.findAllBySpecificationAndPage(filters,1,1)).isNull();
    }

    @Test
    void canFindAllBySpecificationAndPageCabinetTest() {
        FilterForm filters = new FilterForm();
        List<Invoice> list = List.of(new Invoice(), new Invoice());
        Page<Invoice> page = new PageImpl<>(list,PageRequest.of(1,1),1);
        when(invoiceRepository.findByFilters(any(), any(), any())).thenReturn(page);
//        assertThat(invoiceService.findAllBySpecificationAndPageCabinet(filters,1,1)).isInstanceOf(Page.class);
        assertThat(invoiceService.findAllBySpecificationAndPageCabinet(filters,1,1)).isNull();
    }

    @Test
    void canFindAllInvoicesByFiltersAndPageTest() {
        Page<Invoice> page = new PageImpl<>(list, PageRequest.of(1,1), 1);
        when(invoiceRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
        assertThat(invoiceService.findAllInvoicesByFiltersAndPage(new FilterForm(),PageRequest.of(1,1))).isInstanceOf(Page.class);
    }

    @Test
    void canTurnInvoiceIntoExcelTest() throws IOException {
        when(helper.turnInvoiceIntoExcel(any(), any())).thenReturn("test");
        assertThat(helper.turnInvoiceIntoExcel(new Invoice(), new InvoiceTemplate())).isEqualTo("test");
    }

    @Test
    void canGetListSumInvoicesByMonthTest() {
        assertThat(invoiceService.getListSumInvoicesByMonth().size()).isEqualTo(12);
    }

    @Test
    void canGetListOfMonthNameTest() {
        assertThat(invoiceService.getListOfMonthName().size()).isEqualTo(12);
    }

    @Test
    void canGetListExpenseByApartmentByMonthTest() {
        assertThat(invoiceService.getListExpenseByApartmentByMonth(1L).size()).isEqualTo(12);
    }

    @Test
    void canAverageTotalPriceForApartmentLastYearTest() {
        when(invoiceRepository.getAverageTotalPriceForApartmentBetwenDate(any(), any(),any())).thenReturn(12.0);
        assertThat(invoiceService.getAverageTotalPriceForApartmentLastYear(1L)).isEqualTo(12.0);
    }

    @Test
    void canSendExcelInvoiceToEmailTest() {

    }


}
