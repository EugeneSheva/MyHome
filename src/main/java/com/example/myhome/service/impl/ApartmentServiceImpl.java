package com.example.myhome.service.impl;

import com.example.myhome.dto.ApartmentDTO;
import com.example.myhome.exception.NotFoundException;
import com.example.myhome.mapper.ApartmentDTOMapper;
import com.example.myhome.model.Apartment;
import com.example.myhome.model.ApartmentAccount;
import com.example.myhome.model.filter.FilterForm;
import com.example.myhome.repository.AccountRepository;
import com.example.myhome.repository.ApartmentRepository;
import com.example.myhome.service.ApartmentService;
import com.example.myhome.service.BuildingService;
import com.example.myhome.util.FileUploadUtil;
import com.example.myhome.util.MappingUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApartmentServiceImpl implements ApartmentService {
    private final AccountRepository accountRepository;
    @Value("${upload.path}")
    private String uploadPath;
    private final ApartmentRepository apartmentRepository;
    private final FileUploadUtil fileUploadUtil;
    private final BuildingService buildingService;

    private final ApartmentDTOMapper mapper;

    public Apartment findById(Long id) {
        return (id == null) ? null : apartmentRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    public Apartment findByNumber(Long number) {
        return apartmentRepository.findByNumber(number).orElse(null);
    }

    public Apartment save(Apartment apartment) {
        Apartment savedApartment = apartmentRepository.save(apartment);
        if(apartment.getAccount() != null) {
            ApartmentAccount account = apartment.getAccount();
            account.setApartment(savedApartment);
            account.setBuilding(savedApartment.getBuilding());
            account.setSection(savedApartment.getSection());
            account.setOwner(savedApartment.getOwner());
            accountRepository.save(account);
        }
        return savedApartment;
    }
    public Apartment save(ApartmentDTO dto) {
        return save(mapper.fromDTOToApartment(dto));
    }

    public void deleteById(Long id) {
        apartmentRepository.deleteById(id);
    }

    public Long getNumberById(Long id) {
        return apartmentRepository.findById(id).orElseThrow().getNumber();
    }

    public List<Apartment> findAll() {
        return apartmentRepository.findAll();
    }

    public Page<Apartment> findAll(Pageable pageable) {
        return apartmentRepository.findAll(pageable);
    }

    public List<ApartmentDTO> findDtoApartments() {
        return apartmentRepository.findAll().stream().map(MappingUtils::fromApartmentToDTO).collect(Collectors.toList());
    }


    public List<ApartmentDTO> convertApartmentsToApartmentsDTO(List<Apartment> apartmentList) {
        return apartmentList.stream().map(mapper::fromApartmentToDTO).collect(Collectors.toList());
    }

    public ApartmentDTO convertApartmentsToApartmentsDTO(Apartment apartment) {
        return mapper.fromApartmentToDTO(apartment);
    }


    public ApartmentDTO findApartmentDto(Long id) {
        Apartment apartment = apartmentRepository.findById(id).orElseThrow();
        return mapper.fromApartmentToDTO(apartment);
    }


    public List<ApartmentDTO> findDtoApartmentsWithDebt() {
        return apartmentRepository.findApartmentsByBalanceBefore(0D).stream().map(mapper::fromApartmentToDTO).collect(Collectors.toList());
    }

    public List<ApartmentDTO> findDtoApartmentsByBuilding(Long building_id) {
        return apartmentRepository.findApartmentsByBuildingId(building_id).stream().map(mapper::fromApartmentToDTO).collect(Collectors.toList());
    }

    public List<ApartmentDTO> findDtoApartmentsByBuildingWithDebt(Long building_id) {
        return apartmentRepository.findApartmentsByBuildingIdAndBalanceBefore(building_id, 0D).stream().map(mapper::fromApartmentToDTO).collect(Collectors.toList());
    }

    public List<ApartmentDTO> findDtoApartmentsByBuildingAndSection(Long building_id, String section) {
        return apartmentRepository.findApartmentsByBuildingIdAndSectionContainingIgnoreCase(building_id, section).stream()
                .map(mapper::fromApartmentToDTO).collect(Collectors.toList());
    }

    public List<ApartmentDTO> findDtoApartmentsByBuildingAndSectionWithDebt(Long building_id, String section) {
        return apartmentRepository.findApartmentsByBuildingIdAndSectionContainingIgnoreCaseAndBalanceBefore(building_id, section, 0D)
                .stream().map(mapper::fromApartmentToDTO).collect(Collectors.toList());
    }

    public List<ApartmentDTO> findDtoApartmentsByBuildingAndFloor(Long building_id, String floor) {
        return apartmentRepository.findApartmentsByBuildingIdAndFloorContainingIgnoreCase(building_id, floor)
                .stream().map(mapper::fromApartmentToDTO).collect(Collectors.toList());
    }

    public List<ApartmentDTO> findDtoApartmentsByBuildingAndFloorWithDebt(Long building_id, String floor) {
        return apartmentRepository.findApartmentsByBuildingIdAndFloorContainingIgnoreCaseAndBalanceBefore(building_id, floor, 0D)
                .stream().map(mapper::fromApartmentToDTO).collect(Collectors.toList());
    }

    public List<ApartmentDTO> findDtoApartmentsByBuildingAndSectionAndFloor(Long building_id, String section, String floor) {
        return apartmentRepository.findApartmentsByBuildingIdAndSectionContainingIgnoreCaseAndFloorContainingIgnoreCase(building_id, section, floor)
                .stream().map(mapper::fromApartmentToDTO).collect(Collectors.toList());
    }

    public List<ApartmentDTO> findDtoApartmentsByBuildingAndSectionAndFloorWithDebt(Long building_id, String section, String floor) {
        return apartmentRepository.findApartmentsByBuildingIdAndSectionContainingIgnoreCaseAndFloorContainingIgnoreCaseAndBalanceBefore(building_id, section, floor, 0D)
                .stream().map(mapper::fromApartmentToDTO).collect(Collectors.toList());
    }

    public Long getQuantity() {
        return apartmentRepository.count();
    }

    public boolean accountHasApartment(Long account_id) {
        return (accountRepository.findById(account_id).orElseThrow().getApartment() != null);
    }

    public Page<ApartmentDTO> findBySpecificationAndPage(Integer page, Integer size, FilterForm filters) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Apartment> apartments = apartmentRepository.findByFilters(filters.getNumber(), filters.getBuildingName(), filters.getSection(),
                filters.getFloor(), filters.getOwner(), filters.getDebtSting(), pageable);
        List<ApartmentDTO> listDTO = apartments.getContent().stream().map(mapper::fromApartmentToDTO).collect(Collectors.toList());
        return new PageImpl<>(listDTO, pageable, apartments.getTotalElements());
    }
//
}
