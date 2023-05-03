package com.example.myhome.home.converters;

import com.example.myhome.home.dto.ApartmentDTO;
import org.springframework.core.convert.converter.Converter;

public class StringToApartmentDTOConverter implements Converter<String, ApartmentDTO> {
    @Override
    public ApartmentDTO convert(String source) {
        ApartmentDTO dto = new ApartmentDTO();
        try {
            Long id = Long.parseLong(source);
            dto.setId(id);
        } catch (NumberFormatException e) {
            System.out.println(e.getLocalizedMessage());
        }
        System.out.println(dto);
        return dto;
    }
}
