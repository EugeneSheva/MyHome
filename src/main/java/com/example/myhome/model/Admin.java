package com.example.myhome.model;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// --- ПОЛЬЗОВАТЕЛИ ---

@Data
@Entity
@Table(name = "admins")
public class Admin implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String first_name, last_name;
    private String phone_number;

    private String email;

    private String password;

    private boolean active = true;

    private String full_name = first_name + " " + last_name;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfRegistry;

    @ManyToOne (cascade = CascadeType.PERSIST)
    @JoinColumn(name="user_role_id")
    private UserRole role;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime lastActive;

    @JsonIgnore
    @ManyToMany(mappedBy = "receivers")
    private List<Message> messageList;
    @JsonIgnore
    @OneToMany
    @JoinColumn(name = "ownerId")
    private List<CashBox> cashBoxList;
    @JsonIgnore
    @OneToMany
    @JoinColumn(name = "manager")
    private List<CashBox> cashBoxListManager;
    @JsonIgnore
    @ManyToMany(mappedBy = "admins")
    List<Building> buildings;

    @Override
    public String toString() {
        return "Admin{" +
                "id=" + id +
                ", name='" + first_name+ " " + last_name + '\'' +
                ", phone_number=" + phone_number +
                ", email='" + email + '\'' +
                ", active=" + active +
                ", dateOfRegistry=" + dateOfRegistry +
                ", role=" + role +
                ", lastActive=" + lastActive +
                "}\n";
    }

    public String getFullName() {
        return this.first_name + ' ' + this.last_name;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for(String permission : this.role.getPermissions()) {
            authorities.add(new SimpleGrantedAuthority(permission));
        }
        return authorities;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.active;
    }
}

