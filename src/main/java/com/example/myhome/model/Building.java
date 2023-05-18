package com.example.myhome.model;

import javax.persistence.*;


import com.example.myhome.dto.BuildingDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

// --- ДОМА ---

@Data
@Entity
@Table(name = "buildings")
public class Building {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    @ElementCollection
    private List<String>sections  = new ArrayList<>();
    private String address;
    @ElementCollection
    private List<String>floors = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "building")
    List<Apartment> apartments;

    private String img1;
    private String img2;
    private String img3;
    private String img4;
    private String img5;

    @ManyToMany
    @JoinTable(name="building_admins",
            joinColumns = @JoinColumn(name="building_id"),
            inverseJoinColumns = @JoinColumn(name="admin_id"))
    List<Admin> admins = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "building")
    private List<Invoice> invoiceList;

    public Building() {
    }

    public Building(BuildingDTO dto) {
        this.id = dto.getId();
        this.name = dto.getName();
        this.sections = dto.getSections();
        this.address = dto.getAddress();
        this.floors = dto.getFloors();
    }

    public Building(Long id, String name, String address, List<String> sections, List<String> floors) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.sections = sections;
        this.floors = floors;
    }

    @Override
    public String toString() {
        return "Building{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}

