package com.example.myhome.mapper;

import com.example.myhome.dto.OwnerDTO;
import com.example.myhome.model.Owner;
import org.springframework.stereotype.Component;

@Component
public class OwnerDTOMapper {

    public Owner fromDTOToOwner(OwnerDTO dto){

        if(dto == null) return null;

        Owner owner = new Owner();
        owner.setId(dto.getId());
        owner.setFirst_name(dto.getFirst_name());
        owner.setLast_name(dto.getLast_name());
        owner.setFathers_name(dto.getFathers_name());
        owner.setEmail(dto.getEmail());

        return owner;
    }
    public OwnerDTO fromOwnerToDTO(Owner owner) {

        OwnerDTO dto = new OwnerDTO();

        if(owner != null) {
            String status = (owner.getStatus() != null) ? owner.getStatus().getName() : "";
            dto.setId(owner.getId());
            dto.setFirst_name(owner.getFirst_name());
            dto.setLast_name(owner.getLast_name());
            dto.setFathers_name(owner.getFathers_name());
            dto.setEmail(owner.getEmail());
            dto.setFullName(String.join(" ", owner.getFirst_name(), owner.getFathers_name(), owner.getLast_name()));
            dto.setPhone_number(owner.getPhone_number());
            dto.setEmail(owner.getEmail());
            dto.setStatus(status);
        }

        return dto;
    }

}