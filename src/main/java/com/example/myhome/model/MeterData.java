package com.example.myhome.model;

import javax.persistence.*;
import javax.validation.constraints.Digits;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;


import java.time.LocalDate;

// --- ПОКАЗАНИЯ СЧЕТЧИКОВ ---

@Data
@Entity
@Table(name = "meters_data")
public class MeterData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "apartment_id")
    private Apartment apartment;

    @ManyToOne
    @JoinColumn(name = "building_id")
    private Building building;

    private String section;

    @Digits(integer = 5, fraction = 4)
    private Double currentReadings = 0.0;

    @Enumerated(EnumType.STRING)
    private MeterPaymentStatus status;

    @ManyToOne
    @JoinColumn(name = "service_id")
    private Service service;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date = LocalDate.now();


}
