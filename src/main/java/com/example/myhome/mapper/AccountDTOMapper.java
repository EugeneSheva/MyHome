package com.example.myhome.mapper;

import com.example.myhome.dto.ApartmentAccountDTO;
import com.example.myhome.dto.ApartmentDTO;
import com.example.myhome.dto.BuildingDTO;
import com.example.myhome.dto.OwnerDTO;
import com.example.myhome.model.Apartment;
import com.example.myhome.model.ApartmentAccount;
import com.example.myhome.model.Building;
import com.example.myhome.model.Owner;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

@Component
@Log
public class AccountDTOMapper {

    public ApartmentAccount fromDTOToAccount(ApartmentAccountDTO dto) {

        if(dto == null) return null;

        ApartmentAccount account = new ApartmentAccount();
        account.setId(dto.getId());
        account.setIsActive(dto.getIsActive());
        account.setChangedState(dto.getChangedState());
        account.setSection(dto.getSection());
        account.setBalance(dto.getBalance());

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

        log.info("Created dto: ");
        log.info(dto.toString());

        return dto;
    }

}
