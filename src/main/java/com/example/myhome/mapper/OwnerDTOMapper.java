package com.example.myhome.mapper;

import com.example.myhome.dto.OwnerDTO;
import com.example.myhome.model.Owner;
import com.example.myhome.repository.OwnerRepository;
import com.example.myhome.util.UserStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class OwnerDTOMapper {

    @Autowired private OwnerRepository ownerRepository;

    public Owner fromDTOToOwner(OwnerDTO dto){

        if(dto == null) return null;

        Owner owner = (dto.getId() != null && dto.getId() != 0) ? ownerRepository.getReferenceById(dto.getId()) : new Owner();
        owner.setId(dto.getId());
        owner.setFirst_name(dto.getFirst_name());
        owner.setLast_name(dto.getLast_name());
        owner.setFathers_name(dto.getFathers_name());
        owner.setEmail(dto.getEmail());
        owner.setBirthdate(dto.getBirthdate());
        owner.setPhone_number(dto.getPhone_number());
        owner.setDescription(dto.getDescription());
        owner.setStatus(UserStatus.valueOf(dto.getStatus()));

        return owner;
    }
    public OwnerDTO fromOwnerToDTO(Owner owner) {

        OwnerDTO dto = new OwnerDTO();

        if(owner != null) {
            String status = (owner.getStatus() != null) ? owner.getStatus().name() : "";
            dto.setId(owner.getId());
            dto.setFirst_name(owner.getFirst_name());
            dto.setLast_name(owner.getLast_name());
            dto.setFathers_name(owner.getFathers_name());
            dto.setEmail(owner.getEmail());
            dto.setFullName(String.join(" ", owner.getFirst_name(), owner.getFathers_name(), owner.getLast_name()));
            dto.setPhone_number(owner.getPhone_number());
            dto.setEmail(owner.getEmail());
            dto.setStatus(status);
            dto.setText(dto.getFullName() + "(ID:" + dto.getId() + ")");
            dto.setDate((owner.getAdded_at() != null) ? owner.getAdded_at().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "---");
            dto.setBirthdate(owner.getBirthdate());
        }

        return dto;
    }
    public Owner toEntityСabinetEditProfile(OwnerDTO dto) {
        Owner owner = new Owner();

        if (dto != null) {
            owner.setId(dto.getId());
            owner.setProfile_picture(dto.getProfile_picture());
            owner.setFirst_name(dto.getFirst_name());
            owner.setLast_name(dto.getLast_name());
            owner.setFathers_name(dto.getFathers_name());
            owner.setEmail(dto.getEmail());
            owner.setBirthdate(dto.getBirthdate());
            owner.setPhone_number(dto.getPhone_number());
            owner.setTelegram(dto.getTelegram());
            owner.setViber(dto.getViber());
            owner.setDescription(dto.getDescription());
            owner.setAdded_at(dto.getAdded_at());
        }
        return owner;
    }

}
