package com.example.myhome.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tariff_components")
public class TariffComponents {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
//
    private String service_name;
//
//    private Long price;
//    private String currency;
//
    @ManyToOne
    @JoinColumn(name = "tariff_id")
    private Tariff tariff;

    private double price;
    private String currency;
    private String unit;

    public TariffComponents() {
    }

    public TariffComponents(String service_name, double price) {
        this.service_name = service_name;
        this.price = price;
    }
}
