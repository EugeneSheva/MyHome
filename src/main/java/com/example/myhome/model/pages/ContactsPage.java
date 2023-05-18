package com.example.myhome.model.pages;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

@Data
@Entity
@Table(name="page_contacts")
public class ContactsPage extends Page {

    //Контактная информация
    @NotBlank(message = "Необходимо заполнить поле")
    private String title, description, website_link;

    //Контакты
    @NotBlank(message = "Необходимо заполнить поле")
    private String name, location, address, phone, email;

    //Карта
    @NotBlank(message = "Необходимо заполнить поле")
    private String map_code;

}
