package com.example.myhome.home.converter;

import com.example.myhome.home.dto.OwnerDTO;
import org.springframework.core.convert.converter.Converter;

public class StringToOwnerDTOConverter implements Converter<String, OwnerDTO> {
    @Override
    public OwnerDTO convert(String source) {
        OwnerDTO dto = new OwnerDTO();
        try {
            Long ownerID = Long.parseLong(source);
            dto.setId(ownerID);
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
        }
        return dto;
    }
}
