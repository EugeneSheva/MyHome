package com.example.myhome.model.pages;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Entity
@Table(name="page_service")
public class ServicesPage extends Page {

    @ElementCollection
    List<ServiceDescription> serviceDescriptions;

    @Data
    @Embeddable
    public static class ServiceDescription {

        private String title;
        @Column(columnDefinition = "TEXT")
        private String description;
        private String photo;

        @Transient
        private MultipartFile file;
    }
}
