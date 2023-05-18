package com.example.myhome.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Iterator;

// --- УСЛУГИ ---

@Data
@Entity
@Table(name = "services")
public class Service implements Iterable<Service> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Имя услуги не может быть пустым!")
    private String name;

    //флажок "показывать в счётчиках"
    private boolean show_in_meters;

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


}
