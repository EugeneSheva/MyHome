package com.example.myhome.model;

import com.example.myhome.dto.OwnerDTO;
import com.example.myhome.service.registration.RegistrationRequest;
import com.example.myhome.util.UserStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

// --- ВЛАДЕЛЬЦЫ КВАРТИР ---

@Data
@Entity
@Table(name="owners")
public class Owner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String first_name;

    private String last_name;

    private String fathers_name;

    private String phone_number;

    private String email;
    private String password;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthdate;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime added_at;

    private String viber, telegram;

    @JsonIgnore
    @OneToMany(mappedBy = "owner", fetch = FetchType.EAGER)
    private List<Apartment> apartments;
    @JsonIgnore
    @ToString.Exclude
    @ManyToMany(mappedBy = "receivers")
    private List<Message> messages;

    @JsonIgnore
    @ToString.Exclude
    @ManyToMany(mappedBy = "unreadReceivers")
    private List<Message>unreadMessages;

    private boolean has_debt;

    private String description;
    @Enumerated(EnumType.STRING)
    private UserStatus status;

//    private UserRole role = UserRole.USER;

    //аватарка
    private String profile_picture;

    private Boolean enabled = false;

    public Owner() {
    }

    public Owner(OwnerDTO dto) {
        this.id = dto.getId();
        this.first_name = dto.getFirst_name();
        this.last_name = dto.getLast_name();
        this.fathers_name = dto.getFathers_name();
        this.email = dto.getEmail();
        this.birthdate = LocalDate.parse(dto.getDate());
        this.phone_number = dto.getPhone_number();
    }

    public Owner(Long id, String first_name, String last_name, String fathers_name, String phone_number, String email, String password) {
        this.id = id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.fathers_name = fathers_name;
        this.phone_number = phone_number;
        this.email = email;
        this.password = password;
    }

    public Owner(RegistrationRequest request) {
        this.first_name = request.getFirst_name();
        this.last_name = request.getLast_name();
        this.fathers_name = request.getFathers_name();
        this.email = request.getEmail();
        this.password = request.getPassword();
    }

    public String getFullName() {
        return this.first_name + ' ' + this.last_name + ' ' + this.fathers_name;
    }

    public boolean isEnabled() {return this.enabled;}

    public OwnerDTO transformIntoDTO() {
        return OwnerDTO.builder()
                .id(this.id)
                .first_name(this.first_name)
                .last_name(this.last_name)
                .fathers_name(this.fathers_name)
                .text(this.first_name + " " + this.last_name + " " + this.fathers_name)
                .build();
    }


}