package com.example.myhome.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "units")
public class Unit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    public Unit() {
    }

    public Unit(String name) {
        this.name = name;
    }

    public Unit(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}