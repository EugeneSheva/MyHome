package com.example.myhome.model;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

// --- ЗАЯВКИ ВЫЗОВА МАСТЕРА ---

@Data
@Entity
@Table(name = "repair_requests")
public class RepairRequest implements Cloneable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime best_time_request;

    @ManyToOne
    @JoinColumn(name="master_type")
    private UserRole master_type;

    private String description, comment;

    @ManyToOne
    @JoinColumn(name = "apartment_id")
    private Apartment apartment;

    private String phone_number;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Owner owner;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Admin master;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime request_date;

    @Transient
    private String date;
    @Transient
    private String time;

    @Enumerated(EnumType.STRING)
    private RepairStatus status;


    @Override
    public RepairRequest clone() {
        try {
            RepairRequest clone = (RepairRequest) super.clone();
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
