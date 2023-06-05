package com.example.myhome.model;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "apartments")
public class Apartment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinTable(name="building_apartments",
            joinColumns = @JoinColumn(name="apartment_id"),
            inverseJoinColumns = @JoinColumn(name="building_id"))
    private Building building;

    private String section;
    private String floor;
    private Long number;

    private Double balance;

    private Double square;

    //В примере за каждой квартирой ставится только один лицевой счет
    @OneToOne(mappedBy = "apartment", cascade = CascadeType.MERGE)
    private ApartmentAccount account;

    @ManyToOne
    @JoinTable(name="apartment_owners",
            joinColumns = @JoinColumn(name="apartment_id"),
            inverseJoinColumns = @JoinColumn(name="owner_id"))
    private Owner owner;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "tariff_id")
    private Tariff tariff;

    //Показания счётчиков
    @JsonIgnore
    @OneToMany(mappedBy = "apartment")
    private List<MeterData> meterDataList;

    @JsonIgnore
    @OneToMany(mappedBy = "apartment")
    private List<RepairRequest> repairRequestsList;

    //Квитанции
    @JsonIgnore
    @OneToMany(mappedBy = "apartment")
    private List<Invoice> invoiceList;

    public Apartment() {
    }

    public Apartment(Long id, Building building, String section, String floor, Long number, Double balance, Double square, Owner owner) {
        this.id = id;
        this.building = building;
        this.section = section;
        this.floor = floor;
        this.number = number;
        this.balance = balance;
        this.square = square;
        this.owner = owner;
    }

    @Override
    public String toString() {
        return "Apartment{" +
                "id=" + id +
                ", section=" + section +
                ", floor=" + floor +
                ", number=" + number +
                ", balance=" + balance +
                "}\n";
    }

    @PreRemove
    void clearAccount() {
        this.account.setApartment(null);
    }
}