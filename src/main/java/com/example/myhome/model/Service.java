package com.example.myhome.model;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
    @Size(min=2,message="Название должно быть длиннее двух символов!")
    @Size(max=50,message="Название должно быть больше 50 символов!")
    private String name;

    //флажок "показывать в счётчиках"
    private boolean show_in_meters;

    @NotNull
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
