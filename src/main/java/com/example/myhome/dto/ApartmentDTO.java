package com.example.myhome.dto;


import com.example.myhome.model.Tariff;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApartmentDTO {

    private Long id;
    private String section;
    private String floor;
    private Long number;
    private Double balance;
    private Double square;
    private String fullName;
    private BuildingDTO building;
    private ApartmentAccountDTO account;
    private OwnerDTO owner;
    @JsonIgnore private Tariff tariff;
    private Long accountNo;

    public ApartmentDTO(Long id, BuildingDTO building, String section, String floor, Long number, Long account, OwnerDTO owner, Double balance) {
        this.id = id;
        this.section = section;
        this.floor = floor;
        this.number = number;
        this.balance = balance;
        this.square = square;
        this.fullName = fullName;
        this.building = building;
        this.accountNo = account;
        this.owner = owner;
    }

    public ApartmentDTO(Long id, String section, String floor, Long number, Double balance, Double square, String fullName, BuildingDTO building, ApartmentAccountDTO account, OwnerDTO owner) {
        this.id = id;
        this.section = section;
        this.floor = floor;
        this.number = number;
        this.balance = balance;
        this.square = square;
        this.fullName = fullName;
        this.building = building;
        this.account = account;
        this.owner = owner;
    }

    public ApartmentDTO(Long id, String section, String floor, Long number, Double balance, Double square, BuildingDTO building) {
        this.id = id;
        this.section = section;
        this.floor = floor;
        this.number = number;
        this.balance = balance;
        this.square = square;
        this.building = building;

    }

    public ApartmentDTO(Long id, String section, String floor, Long number, Double balance, Double square, BuildingDTO building, Long account) {
        this.id = id;
        this.section = section;
        this.floor = floor;
        this.number = number;
        this.balance = balance;
        this.square = square;
        this.building = building;
        this.accountNo = account;
    }

}
