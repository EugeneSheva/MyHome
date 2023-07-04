package com.example.myhome.model.pages;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@Entity
@Table(name="page_contacts")
public class ContactsPage extends Page {

    //Контактная информация
    @NotBlank(message = "Необходимо заполнить поле")
    @Size(min=2,max=100,message = "Поле должно быть размером 2-100 символов")
    private String title, description;

    @NotBlank(message = "Необходимо заполнить поле")
    @Size(min=2,max=100,message = "Поле должно быть размером 2-100 символов")
    @Pattern(regexp = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]", message = "Неправильный формат URL!")
    private String website_link;

    //Контакты
    @NotBlank(message = "Необходимо заполнить поле")
    @Size(min=2,max=100,message = "Поле должно быть размером 2-100 символов")
    private String name, location, address;

    @NotBlank(message = "Необходимо заполнить поле")
    @Size(min=2,max=100,message = "Поле должно быть размером 2-100 символов")
    @Pattern(regexp = "\\+\\d{12}", message = "Неправильный формат телефона!")
    private String phone;

    @NotBlank(message = "Необходимо заполнить поле")
    @Size(min=2,max=100,message = "Поле должно быть размером 2-100 символов")
    @Email(message = "Неправильный формат электронной почты!")
    private String email;

    //Карта
    @NotBlank(message = "Необходимо заполнить поле")
    @Size(min=2,max=700,message = "Поле должно быть размером 2-700 символов")
    @Column(columnDefinition = "TEXT", name = "map_code")
    private String map_code;

}
