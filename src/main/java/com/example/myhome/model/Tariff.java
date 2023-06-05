package com.example.myhome.model;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
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
    @Size(min=2,max=50,message = "Поле должно быть размеров 2-50 символов")
    private String name;
    @NotBlank(message = "Необходимо указать описание тарифа")
    @Size(min=2,max=255,message = "Поле должно быть размеров 2-255 символов")
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
