package com.example.myhome.service.impl;

import com.example.myhome.dto.ApartmentDTO;
import com.example.myhome.dto.BuildingDTO;
import com.example.myhome.dto.OwnerDTO;
import com.example.myhome.exception.NotFoundException;
import com.example.myhome.mapper.ApartmentDTOMapper;
import com.example.myhome.mapper.OwnerDTOMapper;
import com.example.myhome.model.filter.FilterForm;
import com.example.myhome.repository.OwnerRepository;
import com.example.myhome.service.ApartmentService;
import com.example.myhome.service.BuildingService;
import com.example.myhome.service.OwnerService;
import com.example.myhome.specification.OwnerSpecifications;
import com.example.myhome.model.Apartment;
import com.example.myhome.model.ApartmentAccount;
import com.example.myhome.model.Owner;
import com.example.myhome.util.FileUploadUtil;
import com.example.myhome.util.UserStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log
public class OwnerServiceImpl implements OwnerService {

    @Value("${upload.path}")
    private String uploadPath;
    private final OwnerRepository ownerRepository;
    private final BuildingService buildingService;
    private final ApartmentService apartmentService;
    private final FileUploadUtil fileUploadUtil;

    private final ApartmentDTOMapper apartmentDTOMapper;
    private final OwnerDTOMapper ownerDTOMapper;

    @Override
    public OwnerDTO findByIdDTO(Long id) {
        Owner owner = ownerRepository.findById(id).orElseThrow(NotFoundException::new);
        return ownerDTOMapper.fromOwnerToDTO(owner);
    }

    @Override
    public Owner findById(Long id) { return ownerRepository.findById(id).orElseThrow(NotFoundException::new);}

    @Override
    public Owner findByLogin(String login) {return ownerRepository.findByEmail(login).orElseThrow(NotFoundException::new);}

    @Override
    public List<Owner> findAll() { return ownerRepository.findAll(); }

    @Override
    public Page<Owner> findAll(Pageable pageable) { return ownerRepository.findAll(pageable); }

    @Override
    public List<ApartmentDTO> findOwnerApartments(Long ownerID) {
        Owner owner = ownerRepository.findById(ownerID).orElseThrow();
        return owner.getApartments().stream().map(apartmentDTOMapper::fromApartmentToDTO).collect(Collectors.toList());
    }

    @Override
    public List<OwnerDTO> findAllDTO() {
        return ownerRepository.findAll().stream().map(ownerDTOMapper::fromOwnerToDTO).collect(Collectors.toList());
    }

    @Override
    public List<OwnerDTO> getOwnerDTOByPage(String name, int page_number) {

        Pageable pageable = PageRequest.of(page_number, 10);
        return ownerRepository.findByName(name, pageable)
                .stream()
                .map(ownerDTOMapper::fromOwnerToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<OwnerDTO> findAllDTO(Pageable pageable) {
        Page<Owner> ownerPage = ownerRepository.findAll(pageable);
        List<OwnerDTO> ownerDTOList = ownerPage.stream().map(ownerDTOMapper::fromOwnerToDTO).collect(Collectors.toList());
        return new PageImpl<>(ownerDTOList, pageable, ownerPage.getTotalElements());
    }

    @Override
    public Page<OwnerDTO> findAllBySpecification(FilterForm filters, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page-1, size);

        Page<Owner> initialPage = ownerRepository.findAll(buildSpecFromFilters(filters), pageable);

        List<OwnerDTO> listDTO = initialPage.getContent().stream()
                .map(ownerDTOMapper::fromOwnerToDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(listDTO, pageable, initialPage.getTotalElements());
    }

    @Override
    public Specification<Owner> buildSpecFromFilters(FilterForm filters) {
        Long id = filters.getId();
        String ownerName = filters.getOwnerName();
        String ownerPhoneNumber = filters.getPhone();
        String email = filters.getEmail();
        Long building = filters.getBuilding();
        Long apartmentNumber = filters.getApartment();
        LocalDate date = (filters.getDate() != null) ? LocalDate.parse(filters.getDate()) : null;
        UserStatus status = (filters.getStatus() != null) ? UserStatus.valueOf(filters.getStatus()) : null;
        Boolean has_debt = filters.getDebt();

        return Specification.where(OwnerSpecifications.idContains(id)
                .and(OwnerSpecifications.nameContains(ownerName))
                .and(OwnerSpecifications.phonenumberContains(ownerPhoneNumber))
                .and(OwnerSpecifications.emailContains(email))
                .and(OwnerSpecifications.hasBuilding(building))
                .and(OwnerSpecifications.apartmentContains(apartmentNumber))
                .and(OwnerSpecifications.dateContains(date))
                .and(OwnerSpecifications.statusContains(status))
                .and(OwnerSpecifications.hasDebt(has_debt)));
    }

    @Override
    public Page<OwnerDTO> findAllBySpecification2(FilterForm filters, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page-1, size);
        List<OwnerDTO> listDTO = new ArrayList<>();
        Page<Owner>ownerList = ownerRepository.findByFilters(
                filters.getId(),filters.getOwnerName(),filters.getPhone(),
                filters.getEmail(), filters.getBuildingName(),filters.getApartment(),
                filters.getDate() != null ? LocalDate.parse(filters.getDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")) : null,
                filters.getStatus() != null? UserStatus.valueOf(filters.getStatus()) : null,
                filters.getDebt(), pageable);
        for (Owner owner : ownerList) {
            List<ApartmentDTO> apartments = new ArrayList<>();
            List<BuildingDTO> buildings = new ArrayList<>();
            owner.getApartments().forEach(
                    apart -> {
                        buildings.add(BuildingDTO.builder().id(apart.getBuilding().getId()).name(apart.getBuilding().getName()).build());
                        apartments.add(ApartmentDTO.builder().id(apart.getId()).fullName("№"+apart.getNumber()+", " + apart.getBuilding().getName()).build());
                    }
            );
            OwnerDTO dto = ownerDTOMapper.fromOwnerToDTO(owner);
            dto.setBuildings(buildings);
            dto.setApartments(apartments);
            listDTO.add(dto);
        }
        return new PageImpl<>(listDTO, pageable, ownerList.getTotalElements());
    }

    @Override
    public Page<OwnerDTO> findByNameFragmentDTO(String name, Pageable pageable) {
        List<OwnerDTO> ownerDTOList = new ArrayList<>();
        Page<Owner> ownerPage = ownerRepository.findByNameFragment(name, pageable);
        for (Owner owner : ownerPage) {
            OwnerDTO newDTO = OwnerDTO.builder()
                    .id(owner.getId())
                    .first_name(owner.getFirst_name())
                    .last_name(owner.getLast_name())
                    .fathers_name(owner.getFathers_name())
                    .fullName(owner.getFirst_name()+" "+owner.getLast_name()+" "+owner.getFathers_name())
                    .build();
            ownerDTOList.add(newDTO);
        }
        return new PageImpl<>(ownerDTOList, pageable, ownerPage.getTotalElements());
    }

    @Override
    public Owner save(Owner owner) {
        if(owner.getId() == null || owner.getId() == 0 || owner.getAdded_at() == null) owner.setAdded_at(LocalDateTime.now());
        log.info("OWNER TO SAVE");
        log.info(owner.toString());
        return ownerRepository.save(owner);
    }

    @Override
    public Owner save(OwnerDTO dto) {
        return save(ownerDTOMapper.fromDTOToOwner(dto));
    }

    @Override
    public void deleteById(Long id) {
        ownerRepository.deleteById(id);
    }

    @Override
    public Long getQuantity() { return ownerRepository.count();}

    @Override
    public Long getActiveOwnersQuantity() {return ownerRepository.countActive();}

    @Override
    public List<Long> getOwnerApartmentAccountsIds(Long id) {
        Owner owner = ownerRepository.findById(id).orElseThrow(NotFoundException::new);
        return owner.getApartments().stream().map(Apartment::getAccount).map(ApartmentAccount::getId).collect(Collectors.toList());
    }

    @Override
    public String saveOwnerImage(Long id, MultipartFile file1) throws IOException {
        String fileName = "";
        Owner oldOwner = new Owner();
        if (id!=null) { oldOwner = ownerRepository.getReferenceById(id);
        }
// file1
        if(file1.getSize() > 0) {
            String FileNameUuid = UUID.randomUUID() + "-" + file1.getOriginalFilename();
            fileUploadUtil.saveFile(uploadPath, FileNameUuid, file1);
            fileName = (FileNameUuid);
            if(oldOwner.getProfile_picture() != null) {
            Files.deleteIfExists(Paths.get(uploadPath + oldOwner.getProfile_picture()));

            }
        } else if (oldOwner.getProfile_picture() != null) {
            fileName = oldOwner.getProfile_picture();

        }
    return fileName;
    }
    @Override
    public Boolean isHaveDebt(String debt) {
        Boolean isDebt = null;
        if (debt.equalsIgnoreCase("haveDebt")) {
            isDebt=true;
        }else if (debt.equalsIgnoreCase("noDebt")) {
            isDebt=false;
        }
        return isDebt;
    }

    @Override
    public UserStatus stringStatusConverter(String status) {
        UserStatus userstatus = null;
        if (status.equalsIgnoreCase("active")) {
            userstatus = UserStatus.ACTIVE;
        }else if (status.equalsIgnoreCase("new")) {
            userstatus = UserStatus.NEW;
        }else if (status.equalsIgnoreCase("disabled")) {
            userstatus = UserStatus.DISABLED;
        }
        return userstatus;
    }

    @Override
    public Long countAllOwners() {
        return ownerRepository.count();
    }


    @Override
    public OwnerDTO findOwnerDTObyEmail(String mail) {
        Owner owner = ownerRepository.findByEmail(mail).orElseThrow();
        return new OwnerDTO(owner.getId(),owner.getFirst_name(),owner.getLast_name(),owner.getFathers_name(), (owner.getFirst_name()+" "+owner.getLast_name()+" "+owner.getFathers_name()), apartmentService.convertApartmentsToApartmentsDTO(owner.getApartments()));
    }

    @Override
    public OwnerDTO findOwnerDTObyEmailWithMessages(String mail) {
        Owner owner = ownerRepository.findByEmail(mail).orElseThrow();
        return new OwnerDTO(owner.getId(),owner.getFirst_name(),owner.getLast_name(),owner.getFathers_name(), (owner.getFirst_name()+" "+owner.getLast_name()+" "+owner.getFathers_name()), apartmentService.convertApartmentsToApartmentsDTO(owner.getApartments()), owner.getMessages(), owner.getPhone_number(), owner.getEmail(), owner.getViber(), owner.getTelegram(), owner.getDescription(), owner.getProfile_picture());
    }


    @Override
    public OwnerDTO findOwnerDTObyEmailFull(String mail) {
        Owner owner = ownerRepository.findByEmail(mail).orElseThrow();
        return new OwnerDTO(owner.getId(),owner.getFirst_name(),owner.getLast_name(),owner.getFathers_name(), (owner.getFirst_name()+" "+owner.getLast_name()+" "+owner.getFathers_name()), apartmentService.convertApartmentsToApartmentsDTO(owner.getApartments()), owner.getMessages(), owner.getPhone_number(),owner.getEmail(),owner.getViber(),owner.getTelegram(), owner.getDescription(), owner.getProfile_picture());
    }

    @Override
    public OwnerDTO convertOwnerToOwnerDTO(Owner owner) {
        return new OwnerDTO(owner.getId(),owner.getFirst_name(),owner.getLast_name(),owner.getFathers_name(), (owner.getFirst_name()+" "+owner.getLast_name()+" "+owner.getFathers_name()), apartmentService.convertApartmentsToApartmentsDTO(owner.getApartments()));
    }


}
