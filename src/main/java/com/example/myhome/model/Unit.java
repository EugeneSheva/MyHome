package com.example.myhome.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Entity
@Table(name = "units")
public class Unit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Transient
    private boolean ok;

    public Unit() {
    }

    public Unit(String name) {
        this.name = name;
    }

    public Unit(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    @AssertTrue(message="Название должно быть размером 2-50 символов!")
    private boolean isOk() {
        return !this.name.isBlank() && this.name.length() >= 2 && this.name.length() <= 50;
    }
}