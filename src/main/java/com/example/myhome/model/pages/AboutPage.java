package com.example.myhome.model.pages;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Entity
@Table(name="page_about")
public class AboutPage extends Page {

    //Информация
    @NotBlank
    @Size(min=2,max=50,message = "Название должно быть длиной 2-50 символов!")
    private String title;
    @NotBlank
    @Size(min=2,max=255,message = "Описание должно быть длиной 2-255 символов!")
    private String description;
    private String director_photo;

    //Фотогалерея
    private String photos = "";

    //Дополнительная информация
    @NotBlank
    @Size(min=2,max=50,message = "Название должно быть длиной 2-50 символов!")
    private String add_title;
    @NotBlank
    @Size(min=2,max=255,message = "Описание должно быть длиной 2-255 символов!")
    private String add_description;

    //Дополнительная фотогалерея
    private String add_photos = "";

    //Документы
//    @OneToMany
//    private List<Document> documents;
//
//    @Data
//    @Entity
//    @Table(name="documents")
//    public static class Document {
//        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
//        private long id;
//        @NotBlank
//        @Size(min=2,max=50,message = "Название должно быть длиной 2-50 символов!")
//        private String name;
//        @NotBlank
//        private String file;
//        @ManyToOne
//        private AboutPage page;
//    }

    @ElementCollection
    private List<Document> documents;

    @Data
    @Embeddable
    public static class Document {
        private String documentName;
        private String fileName;

        @Transient
        private MultipartFile file;
    }


}
