package com.example.myhome.converter;

import com.example.myhome.model.Admin;
import org.springframework.core.convert.converter.Converter;

public class AdminToStringConverter implements Converter<Admin, String> {

    @Override
    public String convert(Admin source) {
        return source.getRole().getName() + " - " + source.getFullName();
    }
}
