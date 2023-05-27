package com.example.myhome.model;

import lombok.Data;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Data
@Component
public class ServiceForm {

    private List<@Valid Service> serviceList = new ArrayList<>();
    private List<Unit> unitList = new ArrayList<>();
}
