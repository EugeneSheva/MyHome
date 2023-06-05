package com.example.myhome.mapper;

import com.example.myhome.dto.BuildingDTO;
import com.example.myhome.model.Building;
import org.springframework.stereotype.Component;

@Component
public class BuildingDTOMapper {

    public Building fromDTOToBuilding(BuildingDTO dto) {

        if(dto == null) return null;

        Building building = new Building();
        building.setId(dto.getId());
        building.setName(dto.getName());
        building.setSections(dto.getSections());
        building.setAddress(dto.getAddress());
        building.setFloors(dto.getFloors());

        return building;
    }
    public BuildingDTO fromBuildingToDTO(Building building) {

        if(building == null) return null;

        BuildingDTO dto = new BuildingDTO();
        dto.setId(building.getId());
        dto.setName(building.getName());
        dto.setSections(building.getSections());
        dto.setAddress(building.getAddress());
        dto.setFloors(building.getFloors());
        dto.setText(building.getName());
        dto.setImg1(building.getImg1());
        dto.setImg2(building.getImg2());
        dto.setImg3(building.getImg3());
        dto.setImg4(building.getImg4());
        dto.setImg5(building.getImg5());

        return dto;
    }

}
