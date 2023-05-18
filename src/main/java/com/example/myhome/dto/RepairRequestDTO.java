package com.example.myhome.dto;

import com.example.myhome.model.RepairStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepairRequestDTO {

    private Long id;
    private String best_time;
    private Long masterTypeID;
    private String masterTypeName;
    private String description;
    private String comment;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate request_date;
    private LocalTime request_time;

    private Long apartmentID;
    private Long apartmentNumber;
    private String apartmentBuildingName;

    private Long ownerID;
    private String ownerFullName;
    private String ownerPhoneNumber;

    private Long masterID;
    private String masterFullName;

    private RepairStatus status;
    private String statusName;

}
