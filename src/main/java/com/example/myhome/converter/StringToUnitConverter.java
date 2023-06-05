package com.example.myhome.converter;

import com.example.myhome.model.Unit;
import org.springframework.core.convert.converter.Converter;

public class StringToUnitConverter implements Converter<String, Unit> {
    @Override
    public Unit convert(String source) {
        Unit unit = new Unit();
        try {
            Long id = Long.parseLong(source);
            unit.setId(id);
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
        }
        return unit;
    }
}
