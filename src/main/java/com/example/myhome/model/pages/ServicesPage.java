package com.example.myhome.model.pages;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Entity
@Table(name="page_service")
public class ServicesPage extends Page {

    @ElementCollection
    @NotNull
    List<ServiceDescription> serviceDescriptions;

    @Data
    @Embeddable
    public static class ServiceDescription {
        private String title;
        private String description;
        private String photo;
    }
}
