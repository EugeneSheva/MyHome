package com.example.myhome.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Entity
@Table(name = "payment_details")
public class PaymentDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Название компании не должно быть пустым")
    @Size(min=2,max=150,message = "Длина строки должна быть 2-150 символов")
    private String name;

    @NotBlank(message = "Текст не должен быть пустым")
    @Size(min=2,max=255,message = "Описание должно иметь длину 2-255 символов")
    private String description;
}
