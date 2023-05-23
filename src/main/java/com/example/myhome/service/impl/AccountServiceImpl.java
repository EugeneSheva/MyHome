package com.example.myhome.service.impl;

import com.example.myhome.dto.ApartmentAccountDTO;
import com.example.myhome.mapper.AccountDTOMapper;
import com.example.myhome.model.Building;
import com.example.myhome.model.filter.FilterForm;
import com.example.myhome.repository.AccountRepository;
import com.example.myhome.repository.ApartmentRepository;
import com.example.myhome.repository.BuildingRepository;
import com.example.myhome.repository.OwnerRepository;
import com.example.myhome.service.AccountService;
import com.example.myhome.service.ApartmentService;
import com.example.myhome.service.BuildingService;
import com.example.myhome.service.OwnerService;
import com.example.myhome.specification.AccountSpecifications;
import com.example.myhome.model.Apartment;
import com.example.myhome.model.ApartmentAccount;
import com.example.myhome.model.Owner;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Log
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ApartmentRepository apartmentRepository;
    @Autowired private OwnerRepository ownerRepository;
    @Autowired private BuildingRepository buildingRepository;

    @Autowired private ApartmentService apartmentService;
    @Autowired private BuildingService buildingService;
    @Autowired private OwnerService ownerService;

    @Autowired private AccountDTOMapper mapper;

    @Override
    public List<ApartmentAccount> findAllAccounts() {
        log.info("Getting all apartment accounts...");
        List<ApartmentAccount> list = accountRepository.findAll();
        log.info("Found " + list.size() + " accounts");
        return list;
    }

    @Override
    public Page<ApartmentAccount> findAllAccountsByPage(Pageable pageable) {
        log.info("Getting accounts for page №" + pageable.getPageNumber() + " / size " + pageable.getPageSize());
        Page<ApartmentAccount> page = accountRepository.findAll(pageable);
        log.info("Found " + page.getContent().size() + " accounts");
        return page;
    }

    @Override
    public Page<ApartmentAccountDTO> findAllAccountsByFiltersAndPage(FilterForm filters, Pageable pageable) {
        log.info("Searching for accounts(page "+pageable.getPageNumber()+"/size "+pageable.getPageSize() + ") and specification");
        Page<ApartmentAccount> initialPage = accountRepository.findAll(buildSpecFromFilters(filters), pageable);
        log.info("Found " + initialPage.getContent().size() + " accounts");
        List<ApartmentAccountDTO> listDTO = initialPage.getContent().stream().map(mapper::fromAccountToDTO).collect(Collectors.toList());
        return new PageImpl<>(listDTO, pageable, initialPage.getTotalElements());
    }

    @Override
    public Long getMaxAccountId() {
        Long maxID = accountRepository.getMaxId().orElse(0L);
        log.info("Fetching last account ID: " + maxID);
        return maxID;
    }

    @Override
    public ApartmentAccount findAccountById(Long account_id) {
        log.info("Searching for account with ID: " + account_id);
        ApartmentAccount account = accountRepository.findById(account_id).orElseThrow();
        log.info("Found account! " + account);
        return account;
    }

    public ApartmentAccountDTO findAccountDTOById(Long account_id) {
        log.info("Searching for account with ID: " + account_id);
        ApartmentAccount account = accountRepository.findById(account_id).orElseThrow();
        log.info("Found account! " + account);
        ApartmentAccountDTO dto = mapper.fromAccountToDTO(account);
        if(dto != null && dto.getBuilding() != null && dto.getBuilding().getId() != null) {
            dto.setBuilding(buildingService.findBuildingDTObyId(dto.getBuilding().getId()));
        }
        return dto;
    }

    public ApartmentAccount getAccountNumberFromFlat(Long flat_id) {return accountRepository.findByApartmentId(flat_id).orElseThrow();}

    @Override
    public ApartmentAccount saveAccount(ApartmentAccount account) {
        log.info("Trying to save account...");
        log.info(account.toString());
        try {
            ApartmentAccount savedAcc = accountRepository.save(account);
            log.info("Saved account! " + savedAcc);
            return savedAcc;
        } catch (Exception e) {
            log.severe("Account not saved due to error");
            log.severe(e.getMessage());
            return null;
        }
    }

    @Override
    public ApartmentAccount saveAccount(ApartmentAccountDTO dto) {

        log.info("Forming account data to save from DTO");
        ApartmentAccount account = mapper.fromDTOToAccount(dto);
        Apartment apartment = apartmentRepository.getReferenceById(dto.getApartment().getId());
        account.setApartment(apartment);
        account.setOwner(apartment.getOwner());
        account.setBuilding(apartment.getBuilding());
        return saveAccount(account);

    }

    @Override
    public void deleteAccountById(Long account_id) {
        log.info("Trying to delete account with ID: " + account_id);
        try {
            ApartmentAccount account = findAccountById(account_id);
            log.info("Found account with ID " + account_id);
            account.getTransactions().clear();
            account.getInvoices().forEach(inv -> inv.setAccount(null));
            account.getInvoices().clear();
            log.info("Cleared all transactions and invoices of account with ID: " + account_id);
            accountRepository.delete(account);
            log.info("Successfully deleted account with ID: " + account_id);
        } catch (Exception e) {
            log.severe("Account not deleted due to error");
            log.severe(e.getMessage());
        }
    }

    @Override
    public Specification<ApartmentAccount> buildSpecFromFilters(FilterForm filters) {

        log.info("Building specification from filters: " + filters.toString());

        Long id = filters.getId();
        Boolean active = filters.getActive();
        Long apartment = (filters.getApartment() != null) ? filters.getApartment() : null;
        Building building = (filters.getBuilding() != null) ? buildingService.findById(filters.getBuilding()) : null;
        String section = filters.getSection();
        Owner owner = (filters.getOwner() != null) ? ownerService.findById(filters.getOwner()) : null;
        Boolean debt = filters.getDebt();
        log.info("HAS DEBT: " + debt);

        Specification<ApartmentAccount> spec = Specification.where(AccountSpecifications.hasId(id)
                                                            .and(AccountSpecifications.isActive(active))
                                                            .and(AccountSpecifications.hasApartmentNumber(apartment))
                                                            .and(AccountSpecifications.hasBuilding(building))
                                                            .and(AccountSpecifications.hasSection(section))
                                                            .and(AccountSpecifications.hasOwner(owner))
                                                            .and(AccountSpecifications.hasDebt(debt)));

        log.info("Specification is ready! " + spec);

        return spec;
    }

    public boolean apartmentHasAccount(long apartment_id) {
        return (apartmentRepository.findById(apartment_id).orElseThrow().getAccount() != null);
    }
    public Long getQuantity() { return accountRepository.countAllBy();}
    public Double getSumOfAccountBalances() {
        return accountRepository.findAll().stream()
                .map(ApartmentAccount::getAccountBalance)
                .filter(balance -> balance > 0)
                .reduce(Double::sum).orElse(0.0);
    }
    public Double getSumOfAccountDebts() {
        return accountRepository.findAll().stream()
                .map(ApartmentAccount::getAccountBalance)
                .filter(balance -> balance < 0)
                .reduce(Double::sum).orElse(0.0);
    }


    public ApartmentAccount getAccountWithBiggestId() {return accountRepository.findFirstByOrderByIdDesc().orElseThrow();}

}
