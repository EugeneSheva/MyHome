package com.example.myhome.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name="resetTokens")
public class ForgotPasswordToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String token;
    private String email;

    public ForgotPasswordToken() {}
    public ForgotPasswordToken(String token) {this.token = token;}
}
