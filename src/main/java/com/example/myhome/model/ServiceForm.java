package com.example.myhome.model;

import lombok.Data;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@Component
public class ServiceForm {

    @NotEmpty(message = "Список услуг не должен быть пустым")
    private List<@Valid Service> serviceList;

    @NotEmpty(message = "Список единиц измерения не должен быть пустым")
    private List<Unit> unitList;
}
