package com.example.myhome.model.pages;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;

@Data
@Entity
@Table(name="page_main")
public class MainPage extends Page {

    //Слайдер
    private String slide1, slide2, slide3;

    //Краткая информация
    @NotBlank(message = "Заполните поле")
    private String title, description;
    private boolean show_links;

    @Transient
    private MultipartFile img1;

    //Рядом с нами
    private String block_1_img, block_2_img, block_3_img, block_4_img, block_5_img, block_6_img;
    @NotBlank(message = "Заполните поле")
    private String block_1_title, block_2_title, block_3_title, block_4_title, block_5_title, block_6_title;
    @NotBlank(message = "Заполните поле")
    private String block_1_description, block_2_description, block_3_description, block_4_description, block_5_description, block_6_description;

}
