package com.example.myhome.model;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Iterator;

// --- УСЛУГИ ---

@Data
@Entity
@Table(name = "services")
public class Service implements Iterable<Service> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String name;

    //флажок "показывать в счётчиках"
    private boolean show_in_meters;

    @Transient
    private boolean ok;

    @NotNull(message="Выберите единицу измерения!")
    @ManyToOne
    @JoinColumn(name = "unit_ID")
    private Unit unit;

    public Service() {
    }

    public Service(String name, boolean show_in_meters, Unit unit) {
        this.name = name;
        this.show_in_meters = show_in_meters;
        this.unit = unit;
    }

    @Override
    public Iterator<Service> iterator() {
        return null;
    }

    @AssertTrue(message="Название должно быть размером 2-50 символов!")
    private boolean isOk() {
        return !this.name.isBlank() && this.name.length() >= 2 && this.name.length() <= 50;
    }


}
