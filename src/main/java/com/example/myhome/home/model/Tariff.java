package com.example.myhome.home.model;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

// --- ТАРИФЫ ---

@Data
@Entity
@Table(name = "tariff")
public class Tariff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "#{settings.system.tariffs.name.empty}")
    private String name;
    @NotBlank(message = "Необходимо указать описание тарифа")
    private String description;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime date;

    @ElementCollection
    @CollectionTable(name="tariff_services_mapping",
    joinColumns = @JoinColumn(name="tariff_id"))
    @MapKeyJoinColumn(name="service_id")
    @Column(name="price")
    private Map<Service, Double> components;

//    @OneToMany
//    @JoinColumn(name = "tariff")
//    private List<TariffComponents> componentsList;


    public Tariff() {
    }

    public Tariff(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
