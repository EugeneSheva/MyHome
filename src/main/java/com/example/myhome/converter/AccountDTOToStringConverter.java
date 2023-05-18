package com.example.myhome.converter;

import com.example.myhome.dto.ApartmentAccountDTO;
import org.springframework.core.convert.converter.Converter;

public class AccountDTOToStringConverter implements Converter<ApartmentAccountDTO, String> {
    @Override
    public String convert(ApartmentAccountDTO source) {
        return String.valueOf(source.getId());
    }
}
