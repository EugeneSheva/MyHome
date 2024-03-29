package com.example.myhome.mapper;

import com.example.myhome.dto.ApartmentAccountDTO;
import com.example.myhome.dto.ApartmentDTO;
import com.example.myhome.dto.BuildingDTO;
import com.example.myhome.dto.OwnerDTO;
import com.example.myhome.model.Apartment;
import com.example.myhome.model.ApartmentAccount;
import com.example.myhome.model.Building;
import com.example.myhome.model.Owner;
import com.example.myhome.repository.AccountRepository;
import com.example.myhome.repository.ApartmentRepository;
import com.example.myhome.repository.BuildingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AccountDTOMapper {

    @Autowired private AccountRepository accountRepository;
    @Autowired private ApartmentRepository apartmentRepository;
    @Autowired private BuildingRepository buildingRepository;

    public ApartmentAccount fromDTOToAccount(ApartmentAccountDTO dto) {

        if(dto == null) return null;

        ApartmentAccount account = new ApartmentAccount();
        account.setId(dto.getId());
        account.setIsActive(dto.getIsActive());
        account.setChangedState(dto.getChangedState());
        account.setSection(dto.getSection());
        account.setBalance(dto.getBalance());
        if(dto.getApartment() != null && dto.getApartment().getId() != null && dto.getApartment().getId() != 0) {
            log.info("APART IS NOT NULL");
            Apartment apartment = apartmentRepository.findById(dto.getApartment().getId()).orElse(null);
            account.setApartment(apartment);
            if(apartment != null) {
                account.setBuilding(apartment.getBuilding());
                account.setSection(apartment.getSection());
            }
            else if (dto.getSection() != null && !dto.getSection().equalsIgnoreCase("0")) account.setSection(dto.getSection());
            else dto.setSection("ERROR SECTION");
        }
        else if(dto.getBuilding() != null && dto.getBuilding().getId() != null) {
            log.info("APART IS NULL");
            if(dto.getSection() != null && !dto.getSection().equalsIgnoreCase("0"))
                account.setSection(dto.getSection());
            account.setBuilding(buildingRepository.getReferenceById(dto.getBuilding().getId()));
        }
        return account;
    }
    public ApartmentAccountDTO fromAccountToDTO(ApartmentAccount account) {

        if(account == null) return null;

        ApartmentAccountDTO dto = new ApartmentAccountDTO();
        dto.setId(account.getId());
        dto.setIsActive(account.getIsActive());
        dto.setBalance(account.getBalance());
        dto.setChangedState(account.getChangedState());
        dto.setSection(account.getSection());

        Apartment apartment = account.getApartment();
        if(apartment != null) {
            dto.setApartment(
                    ApartmentDTO.builder()
                            .id(apartment.getId())
                            .fullName("кв. " + apartment.getNumber() + ", " + account.getBuilding().getName())
                            .build());
        }

        Building building = account.getBuilding();
        if(building != null) {
            dto.setBuilding(
                    BuildingDTO.builder()
                            .id(building.getId())
                            .name(building.getName())
                            .build()
            );
        }

        Owner owner = (account.getApartment() != null) ? account.getApartment().getOwner() : null;
        if(owner != null) {
            dto.setOwner(
                    OwnerDTO.builder()
                            .id(owner.getId())
                            .fullName(owner.getFullName())
                            .build()
            );
        }

//        log.info("Created dto: ");
//        log.info(dto.toString());

        return dto;
    }

}
