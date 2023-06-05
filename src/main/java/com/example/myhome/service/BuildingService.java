package com.example.myhome.service;

import com.example.myhome.controller.ApartmentController;
import com.example.myhome.dto.BuildingDTO;
import com.example.myhome.model.Apartment;
import com.example.myhome.model.Building;
import com.example.myhome.model.filter.FilterForm;
import com.example.myhome.specification.BuildingSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface BuildingService {
    Building findById(Long id);

    List<Building> findAll();

    Page<Building> findAll(Pageable pageable);

    List<BuildingDTO> findAllDTO();

    BuildingDTO findBuildingDTObyId(Long id);

    Building save(Building building);

    Page<BuildingDTO> findAllBySpecification(FilterForm filters, Integer page, Integer size);

    default Specification<Building> buildSpecFromFilters(FilterForm filters) {
        Long id = filters.getId();
        String name = filters.getName();
        String address = filters.getAddress();

        return Specification.where(BuildingSpecifications.hasId(id)
                .and(BuildingSpecifications.hasNameLike(name))
                .and(BuildingSpecifications.hasAddressLike(address)));
    }

    List<BuildingDTO> findByPage(String search, int page);

    List<Apartment> getSectionApartments(long building_id, String section_name);

    Long countBuildings();

    void deleteById(Long id);

    Long getQuantity();

    Building saveBuildingImages(Long id, MultipartFile file1, MultipartFile file2, MultipartFile file3, MultipartFile file4, MultipartFile file5) throws IOException;

    List<BuildingDTO> convertBuildingToBuildingDTO(List<Building> buildingList);

    BuildingDTO convertBuildingToBuildingDTO(Building building);

    Building findByName(String buildingName);
}
