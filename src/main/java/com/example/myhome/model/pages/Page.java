package com.example.myhome.model.pages;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@MappedSuperclass
public class Page {

    @Id
    private long id;

    //SEO
    @Size(min=2, max=200, message="Поле должно быть размером 2-200 символов")
    private String seo_title, seo_description, seo_keywords;

}
