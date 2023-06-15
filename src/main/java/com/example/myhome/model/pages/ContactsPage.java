package com.example.myhome.model.pages;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Entity
@Table(name="page_contacts")
public class ContactsPage extends Page {

    //Контактная информация
    @NotBlank(message = "Необходимо заполнить поле")
    @Size(min=2,max=100,message = "Поле должно быть размером 2-100 символов")
    private String title, description, website_link;

    //Контакты
    @NotBlank(message = "Необходимо заполнить поле")
    @Size(min=2,max=100,message = "Поле должно быть размером 2-100 символов")
    private String name, location, address, phone, email;

    //Карта
    @NotBlank(message = "Необходимо заполнить поле")
    @Column(columnDefinition = "TEXT", name = "map_code")
    private String map_code;

}
