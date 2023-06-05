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
import com.example.myhome.repository.BuildingRepository;
import com.example.myhome.repository.OwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ApartmentDTOMapper {

    @Autowired private BuildingRepository buildingRepository;
    @Autowired private OwnerRepository ownerRepository;
    @Autowired private AccountRepository accountRepository;

    public Apartment fromDTOToApartment(ApartmentDTO dto) {

        if(dto == null) return null;

        Apartment apartment = new Apartment();
        apartment.setId(dto.getId());
        apartment.setNumber(dto.getNumber());
        apartment.setSquare(dto.getSquare());
        apartment.setSection(dto.getSection());
        apartment.setFloor(dto.getFloor());
        apartment.setTariff(dto.getTariff());
        apartment.setBalance(dto.getBalance());
        if(dto.getBuilding() != null && dto.getBuilding().getId() != null) apartment.setBuilding(buildingRepository.getReferenceById(dto.getBuilding().getId()));
        if(dto.getOwner() != null && dto.getOwner().getId() != null) apartment.setOwner(ownerRepository.getReferenceById(dto.getOwner().getId()));
        if(dto.getAccount() != null && dto.getAccount().getId() != null) apartment.setAccount(accountRepository.getReferenceById(dto.getAccount().getId()));

        return apartment;
    }
    public ApartmentDTO fromApartmentToDTO(Apartment apartment) {

        if(apartment == null) return null;

        ApartmentDTO dto = new ApartmentDTO();
        dto.setId(apartment.getId());
        dto.setSection(apartment.getSection());
        dto.setFloor(apartment.getFloor());
        dto.setNumber(apartment.getNumber());
        dto.setBalance(apartment.getBalance());
        dto.setSquare(apartment.getSquare());
        dto.setFullName("кв. " + apartment.getNumber());
        dto.setTariff(apartment.getTariff());

        Building building = apartment.getBuilding();
        if(building != null) {
            dto.setBuilding(BuildingDTO.builder()
                    .id(building.getId())
                    .name(building.getName())
                    .sections(building.getSections())
                    .floors(building.getFloors())
                    .build());
            dto.setFullName("кв. " + apartment.getNumber() + ", " + apartment.getBuilding().getName());
        }

        Owner owner = apartment.getOwner();
        if(owner != null) {
            dto.setOwner(OwnerDTO.builder()
                            .id(owner.getId())
                            .fullName(owner.getFullName())
                    .build());
        }

        ApartmentAccount account = apartment.getAccount();
        if(account != null) {
            dto.setAccount(ApartmentAccountDTO.builder()
                            .id(account.getId())
                    .build());
        }

        return dto;
    }

}
