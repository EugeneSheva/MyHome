package com.example.myhome.service;

import com.example.myhome.dto.ApartmentDTO;
import com.example.myhome.model.Apartment;
import com.example.myhome.model.filter.FilterForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ApartmentService {

    List<Apartment> findAll();
    Page<Apartment> findAll(Pageable pageable);
    List<ApartmentDTO> findDtoApartments();

    Apartment findById(Long id);
    Apartment findByNumber(Long number);

    Apartment save(Apartment apartment);

    void deleteById(Long id);

    ApartmentDTO findApartmentDto(Long id);
    ApartmentDTO convertApartmentsToApartmentsDTO(Apartment apartment);
    List<ApartmentDTO> convertApartmentsToApartmentsDTO(List<Apartment> apartmentList);

    List<ApartmentDTO> findDtoApartmentsWithDebt();
    List<ApartmentDTO> findDtoApartmentsByBuilding(Long building_id);
    List<ApartmentDTO> findDtoApartmentsByBuildingWithDebt(Long building_id);
    List<ApartmentDTO> findDtoApartmentsByBuildingAndSection(Long building_id, String section);
    List<ApartmentDTO> findDtoApartmentsByBuildingAndSectionWithDebt(Long building_id, String section);
    List<ApartmentDTO> findDtoApartmentsByBuildingAndFloor(Long building_id, String floor);
    List<ApartmentDTO> findDtoApartmentsByBuildingAndFloorWithDebt(Long building_id, String floor);
    List<ApartmentDTO> findDtoApartmentsByBuildingAndSectionAndFloor(Long building_id, String section, String floor);
    List<ApartmentDTO> findDtoApartmentsByBuildingAndSectionAndFloorWithDebt(Long building_id, String section, String floor);

    Long getNumberById(Long id);
    Long getQuantity();

    Page<ApartmentDTO> findBySpecificationAndPage(Integer page, Integer size, FilterForm filters);

}
