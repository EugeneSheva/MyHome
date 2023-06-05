package com.example.myhome.converter;

import com.example.myhome.model.UserRole;
import org.springframework.core.convert.converter.Converter;

public class RoleToStringConverter implements Converter<UserRole, String> {
    @Override
    public String convert(UserRole source) {
        return source.getName();
    }
}
