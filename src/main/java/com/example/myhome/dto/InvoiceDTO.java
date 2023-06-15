package com.example.myhome.dto;

import com.example.myhome.model.InvoiceComponents;
import com.example.myhome.model.InvoiceStatus;
import com.example.myhome.model.Tariff;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

// --- КВИТАНЦИИ ---

@Data
@AllArgsConstructor
@Builder
public class InvoiceDTO {

    private Long id;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    private Boolean completed;
    private String section;
    private InvoiceStatus status;
    private String statusName;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateFrom;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateTo;
    private double total_price;

    private List<InvoiceComponents> components;
    private Tariff tariff;

    private ApartmentDTO apartment;
    private BuildingDTO building;
    private ApartmentAccountDTO account;
    private OwnerDTO owner;

    public InvoiceDTO() {
        this.apartment = new ApartmentDTO();
        this.building = new BuildingDTO();
        this.account = new ApartmentAccountDTO();
        this.owner = new OwnerDTO();
    }
}
