package com.example.myhome.service.impl;
import com.example.myhome.dto.ApartmentAccountDTO;
import com.example.myhome.exception.NotFoundException;
import com.example.myhome.model.ApartmentAccount;
import com.example.myhome.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApartmentAccountServiceImpl {


    private final AccountRepository accountRepository;


    public ApartmentAccount findById(Long id) {
        return accountRepository.findById(id).orElseThrow(() -> new NotFoundException());
    }

    public ApartmentAccount save(ApartmentAccount apartmentAccount) {
        return accountRepository.save(apartmentAccount);
    }

    public void deleteById(Long id) {
        accountRepository.deleteById(id);
    }

    public List<ApartmentAccount> findAll() {
        return accountRepository.findAll();
    }

    public List<ApartmentAccountDTO> findDtoApartmentAccounts() {
        List<ApartmentAccountDTO> apartmentAccountDTOS = new ArrayList<>();
        for (ApartmentAccount apartmentAccount : accountRepository.findAll()) {
//            apartmentAccountDTOS.add(new ApartmentAccountDTO(apartmentAccount.getId(), apartmentAccount.getIsActive(),apartmentAccount.getNumber(),apartmentAccount.getApartment().getId(),apartmentAccount.getBalance()));
        }
        return apartmentAccountDTOS;
    }

    public Long getQuantity() {
        return accountRepository.countAllBy();
    }

    public Double getSumOfAccountBalances() {
        return accountRepository.findAll().stream()
                .map(ApartmentAccount::getBalance)
                .filter(balance -> balance > 0)
                .reduce(Double::sum).orElse(0.0);
    }

    public Double getSumOfAccountDebts() {
        return accountRepository.findAll().stream()
                .map(ApartmentAccount::getBalance)
                .filter(balance -> balance < 0)
                .reduce(Double::sum).orElse(0.0);
    }

//    public ApartmentAccountDTO convertApartAccountToApartAccountDTO(ApartmentAccount account) {
//        return new ApartmentAccountDTO(account.getId(), account.getIsActive(), account.getNumber(), account.getBalance());
//    }


}
