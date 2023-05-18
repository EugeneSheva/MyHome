package com.example.myhome.model;

import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name="user_roles")
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> permissions = new HashSet<>();

    private Boolean master = false;

    public UserRole() {
    }

    public UserRole(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
