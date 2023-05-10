package com.example.myhome.home.model;

import lombok.Data;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name="page_roles")
public class PageRoleDisplay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String page_name;
    private String code;

    private Boolean role_director = false;
    private Boolean role_admin = false;
    private Boolean role_manager = false;
    private Boolean role_accountant = false;
    private Boolean role_plumber = false;
    private Boolean role_electrician = false;

    public String getRoles() throws IllegalAccessException {
        List<String> roleNames = new ArrayList<>();
        for(Field field : this.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if(!field.getName().equalsIgnoreCase("id") && !field.getName().equalsIgnoreCase("page_name")) {
                if(field.get(this).equals(Boolean.TRUE)) {
                    roleNames.add("''" +field.getName().toUpperCase()+"''");
                }
            }
            field.setAccessible(false);
        }
        return String.join(", ", roleNames);
    }

    public PageRoleDisplay() {
    }

    public PageRoleDisplay(String page_name) {
        this.page_name = page_name;
    }

    public PageRoleDisplay(Long id, String page_name) {
        this.id = id;
        this.page_name = page_name;
    }
}
