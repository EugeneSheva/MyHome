package com.example.myhome.services;

import com.example.myhome.dto.ApartmentAccountDTO;
import com.example.myhome.mapper.AccountDTOMapper;
import com.example.myhome.model.*;
import com.example.myhome.repository.AccountRepository;
import com.example.myhome.repository.ApartmentRepository;
import com.example.myhome.service.AccountService;
import com.example.myhome.validator.AccountValidator;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.validation.DataBinder;
import org.springframework.validation.FieldError;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AccountServiceTest {

    @MockBean private AccountRepository accountRepository;
    @MockBean private ApartmentRepository apartmentRepository;
    @MockBean private AuthenticationManager manager;
    @Autowired private AccountService accountService;
    @Autowired private AccountValidator validator;
    @Autowired private AccountDTOMapper mapper;

    @MockBean private MessageSource messageSource;

    @Test
    @Order(1)
    void sanityTest() {
        assertThat(accountRepository).isNotNull();
        assertThat(accountService).isNotNull();
        assertThat(manager).isNotNull();
        assertThat(validator).isNotNull();
        assertThat(messageSource).isNotNull();
        assertThat(mapper).isNotNull();
    }

    @Test
    void canLoadAllAccountsTest() {
        List<ApartmentAccount> expected = List.of(new ApartmentAccount(), new ApartmentAccount());

        given(accountRepository.findAll()).willReturn(expected);

        List<ApartmentAccount> list = accountService.findAllAccounts();

        verify(accountRepository).findAll();

        assertThat(list).isEqualTo(expected);
    }

    @Test
    void canLoadSingleAccountTest() {
        ApartmentAccount expected = new ApartmentAccount();
        expected.setId(1L);

        given(accountRepository.findById(1L)).willReturn(Optional.of(expected));

        ApartmentAccount acc = accountService.findAccountById(1L);
        verify(accountRepository).findById(1L);

        assertThat(acc).isEqualTo(expected);
    }

    @Test
    void throwsExceptionOnAccountNotFound() {
        assertThrows(NoSuchElementException.class, () -> accountService.findAccountById(1L));
    }

    @Test
    void calculatesBalancesAndDebtsCorrectly() {
        Random random = new Random();
        List<ApartmentAccount> accountList = new ArrayList<>();
        double total_positive_balance = 0.0;
        double total_debt = 0.0;
        for (int i = 0; i < 10; i++) {
            ApartmentAccount apartmentAccount = new ApartmentAccount();
            apartmentAccount.setTransactions(new ArrayList<>());
            apartmentAccount.setApartment(new Apartment());
            apartmentAccount.getApartment().setInvoiceList(new ArrayList<>());

            double account_balance = 0.0;

            for(int j = 0; j < 5; j++) {
                CashBox cashBox = new CashBox();
                cashBox.setCompleted(true);
                double transaction_amount = Math.floor(random.nextDouble()*10000);
                cashBox.setAmount(transaction_amount);
                account_balance += transaction_amount;
                apartmentAccount.getTransactions().add(cashBox);
            }

            for(int k = 0; k < 5; k++) {
                Invoice invoice = new Invoice();
                invoice.setCompleted(true);
                double invoice_price = Math.floor(random.nextDouble()*10000);
                invoice.setTotal_price(invoice_price);
                account_balance -= invoice_price;
                apartmentAccount.getInvoices().add(invoice);
            }
            if(account_balance < 0) total_debt += account_balance;
            else total_positive_balance += account_balance;

            System.out.println("sum from account positive transactions: " + apartmentAccount.getTransactions().stream().map(CashBox::getAmount).reduce(Double::sum).orElse(0.0));
            System.out.println("sum from account negative invoices: " + apartmentAccount.getApartment().getInvoiceList().stream().map(Invoice::getTotal_price).reduce(Double::sum).orElse(0.0));
            System.out.println("account balance: " + account_balance);

            System.out.println("total debt: " + total_debt);
            System.out.println("total positive balance: " + total_positive_balance);
            System.out.println("======");

            accountList.add(apartmentAccount);
        }

        accountList.forEach(account -> {
            System.out.println(account.toString());
            System.out.println(account.getTransactions().toString());
            System.out.println(account.getApartment().getInvoiceList().toString());
        });

        given(accountRepository.findAll()).willReturn(accountList);
        accountService.getSumOfAccountBalances();
        verify(accountRepository).findAll();
        assertThat(accountService.getSumOfAccountBalances()).isEqualTo(total_positive_balance);
        assertThat(accountService.getSumOfAccountDebts()).isEqualTo(total_debt);
    }

    @Test
    void transformsIntoDTOWithoutOtherDTO() {
        ApartmentAccount account = new ApartmentAccount();
        account.setId(1L);
        account.setSection("test");
        account.setBalance(100.0);
        account.setIsActive(true);

        ApartmentAccountDTO dto = mapper.fromAccountToDTO(account);

        assertThat(dto.getId()).isEqualTo(account.getId());
        assertThat(dto.getSection()).isEqualTo(account.getSection());
        assertThat(dto.getBalance()).isEqualTo(account.getBalance());
        assertThat(dto.getIsActive()).isEqualTo(account.getIsActive());
        assertThat(dto.getApartment()).isNotNull();
        assertThat(dto.getApartment().getId()).isNull();
        assertThat(dto.getBuilding()).isNotNull();
        assertThat(dto.getBuilding().getId()).isNull();
        assertThat(dto.getOwner()).isNotNull();
        assertThat(dto.getOwner().getId()).isNull();
    }

    @Test
    void transformsIntoDTOWithOtherDTO() {
        ApartmentAccount account = new ApartmentAccount();
        account.setId(1L);
        account.setSection("test");
        account.setBalance(100.0);
        account.setIsActive(true);

        Building building = new Building();
        building.setId(1L);
        building.setName("test");
        account.setBuilding(building);

        Owner owner = new Owner();
        owner.setId(1L);
        owner.setFirst_name("test");
        owner.setFathers_name("test");
        owner.setLast_name("test");
        account.setOwner(owner);

        Apartment apartment = new Apartment();
        apartment.setId(1L);
        apartment.setOwner(owner);
        account.setApartment(apartment);

        ApartmentAccountDTO dto = mapper.fromAccountToDTO(account);

        assertThat(dto.getId()).isEqualTo(account.getId());
        assertThat(dto.getSection()).isEqualTo(account.getSection());
        assertThat(dto.getBalance()).isEqualTo(account.getBalance());
        assertThat(dto.getIsActive()).isEqualTo(account.getIsActive());
        assertThat(dto.getApartment()).isNotNull();
        assertThat(dto.getApartment().getId()).isEqualTo(1L);
        assertThat(dto.getBuilding()).isNotNull();
        assertThat(dto.getBuilding().getId()).isEqualTo(1L);
        assertThat(dto.getBuilding().getName()).isEqualTo("test");
        assertThat(dto.getOwner()).isNotNull();
        assertThat(dto.getOwner().getFullName()).isEqualTo("test test test");
    }

    @Test
    void returnNullOnTransformNullToDTO() {
        ApartmentAccount account = null;
        assertThat(mapper.fromAccountToDTO(account)).isNull();
    }

    @Test
    void validate() {
        ApartmentAccountDTO dto = new ApartmentAccountDTO();
        dto.getBuilding().setId(null);
        dto.setSection("0");
        dto.getApartment().setId(0L);

        DataBinder binder = new DataBinder(dto);
        binder.setValidator(validator);
        binder.validate();
        List<FieldError> errorList = binder.getBindingResult().getFieldErrors();

        assertThat(errorList.get(0).getField()).isEqualTo("building");
        assertThat(errorList.get(1).getField()).isEqualTo("isActive");
        assertThat(errorList.size()).isEqualTo(2);

        dto.getBuilding().setId(10L);

        binder = new DataBinder(dto);
        binder.setValidator(validator);
        binder.validate();
        errorList = binder.getBindingResult().getFieldErrors();

        assertThat(errorList.get(0).getField()).isEqualTo("section");
        assertThat(errorList.get(1).getField()).isEqualTo("isActive");
        assertThat(errorList.size()).isEqualTo(2);

        dto.setSection("test");

        binder = new DataBinder(dto);
        binder.setValidator(validator);
        binder.validate();
        errorList = binder.getBindingResult().getFieldErrors();

        assertThat(errorList.get(0).getField()).isEqualTo("apartment");
        assertThat(errorList.get(1).getField()).isEqualTo("isActive");
        assertThat(errorList.size()).isEqualTo(2);
    }

    @Test
    void checksForExistingApartmentAccount() {

        Apartment testApartment = new Apartment();
        ApartmentAccount testAccount = new ApartmentAccount();
        testApartment.setAccount(testAccount);

        given(apartmentRepository.findById(anyLong())).willReturn(Optional.of(testApartment));

        ApartmentAccountDTO dto = new ApartmentAccountDTO();
        dto.getBuilding().setId(10L);
        dto.setSection("test");
        dto.getApartment().setId(10L);
        dto.setIsActive(true);

        DataBinder binder = new DataBinder(dto);
        binder.setValidator(validator);
        binder.validate();
        List<FieldError> errorList = binder.getBindingResult().getFieldErrors();

        assertThat(errorList.get(0).getField()).isEqualTo("apartment");
        assertThat(errorList.size()).isEqualTo(1);
    }
}
