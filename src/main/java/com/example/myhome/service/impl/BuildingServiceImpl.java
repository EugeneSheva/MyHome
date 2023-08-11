package com.example.myhome.service.impl;


import com.example.myhome.dto.AdminDTO;
import com.example.myhome.dto.BuildingDTO;
import com.example.myhome.exception.NotFoundException;
import com.example.myhome.mapper.AdminDTOMapper;
import com.example.myhome.mapper.ApartmentDTOMapper;
import com.example.myhome.mapper.BuildingDTOMapper;
import com.example.myhome.model.Apartment;
import com.example.myhome.model.Building;

import com.example.myhome.model.filter.FilterForm;
import com.example.myhome.repository.BuildingRepository;
import com.example.myhome.service.BuildingService;
import com.example.myhome.util.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.FieldError;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BuildingServiceImpl implements BuildingService {
    private final BuildingRepository buildingRepository;
    private final FileUploadUtil fileUploadUtil;

    private final BuildingDTOMapper mapper;
    private final ApartmentDTOMapper apartmentMapper;
    private final AdminDTOMapper adminMapper;
    private final MessageSource messageSource;
    @Override
    public Building findById(Long id) {
        return buildingRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    @Override
    public List<Building> findAll() {
        return buildingRepository.findAll();
    }

    @Override
    public Page<Building> findAll(Pageable pageable) {
        return buildingRepository.findAll(pageable);
    }

    @Override
    public List<BuildingDTO> findAllDTO() {
        return buildingRepository.findAll().stream().map(mapper::fromBuildingToDTO).collect(Collectors.toList());
    }

    @Override
    public BuildingDTO findBuildingDTObyId(Long id) {
        Building building = buildingRepository.findById(id).orElseThrow();
        BuildingDTO dto = mapper.fromBuildingToDTO(building);
        dto.setApartments(building.getApartments().stream().map(apartmentMapper::fromApartmentToDTO).collect(Collectors.toList()));
        dto.setAdmins(building.getAdmins().stream().map(adminMapper::fromAdminToDTO).collect(Collectors.toList()));
        return dto;
    }

    @Override
    public Building save(Building building) {
        return buildingRepository.save(building);
    }

    @Override
    public Page<BuildingDTO> findAllBySpecification(FilterForm filters, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Building> initialPage = buildingRepository.findAll(buildSpecFromFilters(filters), pageable);

        List<BuildingDTO> listDTO = initialPage.getContent().stream()
                .map(mapper::fromBuildingToDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(listDTO, pageable, initialPage.getTotalElements());
    }

    @Override
    public List<BuildingDTO> findByPage(String search, int page) {
        return buildingRepository.findByName(search, PageRequest.of(page - 1, 5)).stream()
                .map(mapper::fromBuildingToDTO).collect(Collectors.toList());
    }

    @Override
    public List<Apartment> getSectionApartments(long building_id, String section_name) {
        log.info("ID BUILDING: " + building_id + ", SECTION NAME: " + section_name);
        return buildingRepository.getSectionApartments(building_id, section_name);
    }

    @Override
    public Long countBuildings() {
        return buildingRepository.count();
    }

    @Override
    public void deleteById(Long id) {
        buildingRepository.deleteById(id);
    }

    @Override
    public Long getQuantity() {
        return buildingRepository.count();
    }

    @Override
    public Building saveBuildingImages(Long id, MultipartFile file1, MultipartFile file2, MultipartFile file3, MultipartFile file4, MultipartFile file5) throws IOException {
        Building newBuilding = new Building();
        Building oldBuilding = new Building();
        if (id > 0) {
            oldBuilding = buildingRepository.getReferenceById(id);
            newBuilding.setId(id);
        }
// file1
        String uploadPath = "";
        if (file1.getSize() > 0) {
            String FileNameUuid = UUID.randomUUID() + "-" + file1.getOriginalFilename();
            fileUploadUtil.saveFile(uploadPath, FileNameUuid, file1);
            newBuilding.setImg1(FileNameUuid);
            if (oldBuilding.getImg1() != null) {
                Files.deleteIfExists(Paths.get(uploadPath + oldBuilding.getImg1()));
            }
        } else if (oldBuilding.getImg1() != null) {
            newBuilding.setImg1(oldBuilding.getImg1());
        }
// file2
        if (file2.getSize() > 0) {
            String FileNameUuid = UUID.randomUUID() + "-" + file2.getOriginalFilename();
            fileUploadUtil.saveFile(uploadPath, FileNameUuid, file2);
            newBuilding.setImg2(FileNameUuid);
            if (oldBuilding.getImg2() != null) {
                Files.deleteIfExists(Paths.get(uploadPath + oldBuilding.getImg2()));
            }
        } else if (oldBuilding.getImg2() != null) {
            newBuilding.setImg2(oldBuilding.getImg2());
        }
// file3
        if (file3.getSize() > 0) {
            String FileNameUuid = UUID.randomUUID() + "-" + file3.getOriginalFilename();
            fileUploadUtil.saveFile(uploadPath, FileNameUuid, file3);
            newBuilding.setImg3(FileNameUuid);
            if (oldBuilding.getImg3() != null) {
                Files.deleteIfExists(Paths.get(uploadPath + oldBuilding.getImg3()));
            }
        } else if (oldBuilding.getImg3() != null) {
            newBuilding.setImg3(oldBuilding.getImg3());
        }
// file4
        if (file4.getSize() > 0) {
            String FileNameUuid = UUID.randomUUID() + "-" + file4.getOriginalFilename();
            fileUploadUtil.saveFile(uploadPath, FileNameUuid, file4);
            newBuilding.setImg4(FileNameUuid);
            if (oldBuilding.getImg4() != null) {
                Files.deleteIfExists(Paths.get(uploadPath + oldBuilding.getImg4()));
            }
        } else if (oldBuilding.getImg4() != null) {
            newBuilding.setImg4(oldBuilding.getImg4());
        }
// file5
        if (file5.getSize() > 0) {
            String FileNameUuid = UUID.randomUUID() + "-" + file5.getOriginalFilename();
            fileUploadUtil.saveFile(uploadPath, FileNameUuid, file5);
            newBuilding.setImg5(FileNameUuid);
            if (oldBuilding.getImg5() != null) {
                Files.deleteIfExists(Paths.get(uploadPath + oldBuilding.getImg5()));
            }
        } else if (oldBuilding.getImg5() != null) {
            newBuilding.setImg5(oldBuilding.getImg5());
        }

        return newBuilding;
    }

    @Override
    public List<BuildingDTO> convertBuildingToBuildingDTO(List<Building> buildingList) {
        return buildingList.stream().map(mapper::fromBuildingToDTO).collect(Collectors.toList());
    }


    @Override
    public BuildingDTO convertBuildingToBuildingDTO(Building building) {
        return mapper.fromBuildingToDTO(building);
    }

    @Override
    public Building findByName(String buildingName) {
        return buildingRepository.findByName(buildingName);
    }
    @Override
    public FieldError validateImg(MultipartFile file, String errName) {
        Locale locale = LocaleContextHolder.getLocale();
        if (file != null && !file.isEmpty()) {
            if (!file.getContentType().equals("image/jpg") && !file.getContentType().equals("image/jpeg") && !file.getContentType().equals("image/png")) {
                FieldError fileError = new FieldError("building", errName, messageSource.getMessage("imgValid", null, locale));
                return fileError;
            } else if (file.getSize() > 20 * 1024 * 1024) {
                FieldError fileError = new FieldError("building", errName, messageSource.getMessage("imgValidSize", null, locale));
                return fileError;
            }
        }
        return null;
    }
}