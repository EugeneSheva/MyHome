package com.example.myhome.model.pages;

import lombok.Data;

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
        @NotBlank
        @Size(min=2,max=100,message = "Поле должно быть размером 2-100 символов")
        private String title;
        @NotBlank
        @Size(min=2,max=100,message = "Поле должно быть размером 2-100 символов")
        private String description;
        private String photo;
    }
}
