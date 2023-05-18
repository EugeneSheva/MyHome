package com.example.myhome.model.pages;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@Data
@MappedSuperclass
public class Page {

    @Id
    private long id;

    //SEO
    private String seo_title, seo_description, seo_keywords;

}
