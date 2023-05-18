package com.example.myhome.model.pages;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@Entity
@Table(name="page_about")
public class AboutPage extends Page {

    //Информация
    @NotBlank
    private String title, description;
    private String director_photo;

    //Фотогалерея
    private String photos = "";

    //Дополнительная информация
    @NotBlank
    private String add_title, add_description;

    //Дополнительная фотогалерея
    private String add_photos = "";

    //Документы
    @OneToMany
    private List<Document> documents;

    @Data
    @Entity
    @Table(name="documents")
    public static class Document {
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        private long id;
        @NotBlank
        private String name;
        @NotBlank
        private String file;
        @ManyToOne
        private AboutPage page;
    }


}
