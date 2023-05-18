package com.example.myhome.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@Data
@Entity
@Table(name = "payment_details")
public class PaymentDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Название компании не должно быть пустым")
    private String name;

    @NotBlank(message = "Текст не должен быть пустым")
    private String description;
}
