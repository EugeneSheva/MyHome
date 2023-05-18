package com.example.myhome.dto;


import lombok.*;

// --- ЛИЦЕВЫЕ СЧЕТА ---

@Data
@AllArgsConstructor
@Builder
public class ApartmentAccountDTO {

    private Long id;
    private Boolean isActive;
    private Boolean changedState = false;
    private Double balance = 0.0;
    private String section;

    private ApartmentDTO apartment;
    private BuildingDTO building;
    private OwnerDTO owner;

    public ApartmentAccountDTO() {
        this.apartment = new ApartmentDTO();
        this.building = new BuildingDTO();
        this.owner = new OwnerDTO();
    }

    public ApartmentAccountDTO(Long id, Boolean isActive, Double balance) {
        this.id = id;
        this.isActive = isActive;
        this.balance = balance;
    }
}
