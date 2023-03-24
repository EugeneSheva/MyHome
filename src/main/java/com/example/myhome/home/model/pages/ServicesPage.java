package com.example.myhome.home.model.pages;

import lombok.Data;

import javax.persistence.*;
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
        private String description;
        private String photo;
    }
}
