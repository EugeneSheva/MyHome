package com.example.myhome.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// --- ЛИЦЕВЫЕ СЧЕТА ---

@Getter
@Setter
@ToString
@Entity
@Table(name = "apartment_account")
public class ApartmentAccount implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    private Boolean isActive;

    @Transient
    private Boolean changedState;

    @ToString.Exclude
    @OneToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="apartment_id")
    private Apartment apartment;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name="building_id")
    private Building building;

    @ManyToOne
    @JoinColumn(name="owner_id")
    private Owner owner;

    private String section;

    @ToString.Exclude
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "apartmentAccount")
    private List<CashBox> transactions = new ArrayList<>();

    @ToString.Exclude
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "account")
    private List<Invoice> invoices = new ArrayList<>();

    private Double balance = 0.0;

    public ApartmentAccount() {
    }

    public ApartmentAccount(Long id, Boolean isActive, Double balance) {
        this.id = id;
        this.isActive = isActive;
        this.balance = balance;
    }

    public Double getAccountBalance() {
        return this.transactions.stream()
                .map(CashBox::getAmount)
                .reduce(Double::sum).orElse(0.0)
                - this.getInvoices().stream()
                .map(Invoice::getTotal_price)
                .reduce(Double::sum).orElse(0.0);
    }

    public void addToBalance(double amount) {
        this.balance += amount;
    }
    public void removeFromBalance(double amount) {this.balance -= amount;}

    public Long getId() {
        return id;
    }
    public ApartmentAccount(Boolean isActive, Building building, Owner owner, String section, Double balance) {
        this.isActive = isActive;
        this.building = building;
        this.owner = owner;
        this.section = section;
        this.balance = balance;
    }
}
