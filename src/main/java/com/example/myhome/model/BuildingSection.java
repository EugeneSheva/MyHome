package com.example.myhome.model;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "building_sections")
public class BuildingSection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="building_id")
    private Building building;

    @OneToMany(mappedBy = "section")
    private List<Apartment> apartments;

    private String name;

    public BuildingSection() {
    }

    public BuildingSection(Long id, Building building, String name) {
        this.id = id;
        this.building = building;
        this.name = name;
    }

    @Override
    public String toString() {
        return "BuildingSection{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
