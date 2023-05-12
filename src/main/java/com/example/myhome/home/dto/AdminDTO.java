package com.example.myhome.home.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// --- ПОЛЬЗОВАТЕЛИ ---

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminDTO {

    private Long id;
    private String text;

    private String first_name, last_name;



    private String phone_number;
    private String email;
    private String password;
    private String confirm_password;
    private boolean active = true;
    private String role;
    private Long userRoleID;

    public String getFullName() {
        return this.first_name + ' ' + this.last_name;
    }

    public AdminDTO(Long id, String first_name, String last_name, String phone_number, String email, boolean active, String role, Long userRoleID) {
        this.id = id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.phone_number = phone_number;
        this.email = email;
        this.active = active;
        this.role = role;
        this.userRoleID = userRoleID;
    }

}

