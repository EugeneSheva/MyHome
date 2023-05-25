package com.example.myhome.service.impl;

import com.example.myhome.dto.RepairRequestDTO;
import com.example.myhome.exception.NotFoundException;
import com.example.myhome.mapper.RepairRequestDTOMapper;
import com.example.myhome.model.*;
import com.example.myhome.model.filter.FilterForm;

import com.example.myhome.repository.*;
import com.example.myhome.service.AdminService;
import com.example.myhome.service.ApartmentService;
import com.example.myhome.service.OwnerService;
import com.example.myhome.service.RepairRequestService;
import com.example.myhome.specification.RequestSpecifications;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log
public class RepairRequestServiceImpl implements RepairRequestService {

    private final RepairRequestRepository repairRequestRepository;

    private final ApartmentService apartmentService;
    private final OwnerService ownerService;
    private final AdminService adminService;

    private final ApartmentRepository apartmentRepository;
    private final OwnerRepository ownerRepository;
    private final AdminRepository adminRepository;
    private final UserRoleRepository userRoleRepository;

    private final RepairRequestDTOMapper mapper;

    @Override
    public List<RepairRequest> findAllRequests() {
        log.info("Searching for requests");
        List<RepairRequest> list = repairRequestRepository.findAll();
        log.info("Found " + list.size() + " requests");
        return list;
    }

    @Override
    public Page<RepairRequest> findAllBySpecification(FilterForm filters) throws IllegalAccessException {
        log.info("Searching for requests using filters: " + filters.toString());
        Page<RepairRequest> page =
                (filters.filtersPresent()) ?
                        repairRequestRepository.findAll(buildSpecFromFilters(filters), PageRequest.of(filters.getPage()-1,10)) :
                        repairRequestRepository.findAll(PageRequest.of(filters.getPage()-1, 10));
        log.info("Found " + page.getTotalElements() + " requests, total pages " + page.getTotalPages());
        return page;
    }

    @Override
    public Page<RepairRequestDTO> findAllBySpecification(FilterForm filters, Integer page, Integer size) throws IllegalAccessException {

        Pageable pageable = PageRequest.of(page-1,size);

        Page<RepairRequest> initialPage = repairRequestRepository.findAll(buildSpecFromFilters(filters), pageable);

        List<RepairRequestDTO> listDTO = initialPage.getContent().stream()
                .map(mapper::fromRequestToDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(listDTO, pageable, initialPage.getTotalElements());
    }

    @Override
    public Specification<RepairRequest> buildSpecFromFilters(FilterForm filters) {

        Long id = filters.getId();
        String description = filters.getDescription();
        UserRole masterType = (filters.getMaster_type() != null && filters.getMaster_type() > 0) ? userRoleRepository.getReferenceById(filters.getMaster_type()) : null;
        String phone = filters.getPhone();
        RepairStatus status = (filters.getStatus() != null) ? RepairStatus.valueOf(filters.getStatus()) : null;
        Apartment apartment = (filters.getApartment() != null) ? apartmentService.findByNumber(filters.getApartment()) : null;
        Owner owner = (filters.getOwner() != null) ? ownerService.findById(filters.getOwner()) : null;
        Admin master = (filters.getMaster() != null) ? adminService.findAdminById(filters.getMaster()) : null;

        String datetime = filters.getDatetime();
        LocalDateTime from = null, to = null;

        if(datetime != null && !datetime.isEmpty()) {
            String datetime_from = datetime.split(" to ")[0];
            from =
                    LocalDateTime.of(LocalDate.parse(datetime_from.split(" ")[0]),
                            LocalTime.parse(datetime_from.split(" ")[1]));
            String datetime_to = datetime.split(" to ")[1];
            to =
                    LocalDateTime.of(LocalDate.parse(datetime_to.split(" ")[0]),
                            LocalTime.parse(datetime_to.split(" ")[1]));
        }

        return Specification.where(RequestSpecifications.hasId(id)
                .and(RequestSpecifications.hasMasterType(masterType))
                .and(RequestSpecifications.hasDescriptionLike(description))
                .and(RequestSpecifications.hasApartment(apartment))
                .and(RequestSpecifications.hasOwner(owner))
                .and(RequestSpecifications.hasPhoneLike(phone))
                .and(RequestSpecifications.hasMaster(master))
                .and(RequestSpecifications.hasStatus(status))
                .and(RequestSpecifications.datesBetween(from, to)));
    }

    @Override
    public Long getMaxId() {return repairRequestRepository.getMaxId().orElse(0L);}

    @Override
    public RepairRequest findRequestById(long request_id) {
        log.info("Looking for request with ID: " + request_id);
        try {
            RepairRequest req = repairRequestRepository.findById(request_id).orElseThrow(NotFoundException::new);
            log.info("Request found! " + req);
            return req;
        } catch (NotFoundException e) {
            log.warning("Request with ID " + request_id + " not found!");
            log.warning(e.getMessage());
            return null;
        }
    }

    @Override
    public RepairRequestDTO findRequestDTOById(Long request_id) {
        RepairRequest request = findRequestById(request_id);
        if(request != null) return mapper.fromRequestToDTO(request);
        else return null;
    }

    @Override
    public RepairRequest saveRequest(RepairRequest request) {
        log.info("Trying to save request...");
        log.info(request.toString());
        RepairRequest savedRequest;
        try {
            savedRequest = repairRequestRepository.save(request);
            log.info("Request successfully saved! Saved request: ");
            log.info(savedRequest.toString());
            return savedRequest;
        } catch (Exception e) {
            log.severe("Request couldn't be saved");
            log.severe(e.getMessage());
            return null;
        }
    }

    @Override
    public RepairRequest saveRequest(RepairRequestDTO dto) {

        log.info("Forming repair request to save from DTO");
        RepairRequest request = mapper.fromDTOToRequest(dto);
        request.setApartment(apartmentRepository.getReferenceById(dto.getApartmentID()));
        request.setOwner(ownerRepository.getReferenceById(dto.getOwnerID()));
        request.setMaster(adminRepository.getReferenceById(dto.getMasterID()));
        log.info("Request formation finished, trying to save...");
        log.info(request.toString());

        try {
            RepairRequest savedRequest = repairRequestRepository.save(request);
            log.info("Request successfully saved! Saved request: ");
            log.info(savedRequest.toString());
            return savedRequest;
        } catch (Exception e) {
            log.severe("Request couldn't be saved");
            log.severe(e.getMessage());
            return null;
        }
    }

    @Override
    public void deleteRequestById(long request_id) {
        log.info("Deleting request with ID: " + request_id);
        try {
            repairRequestRepository.deleteById(request_id);
        } catch (Exception e) {
            log.severe("Deletion failed!");
            log.severe(e.getMessage());
        }
        log.info("Request with ID " + request_id + " successfully deleted");
    }

    @Override
    public Page<RepairRequestDTO> findAll(Pageable pageable){
        List<RepairRequestDTO>listDTO= new ArrayList<>();
        Page<RepairRequest>repairRequests = repairRequestRepository.findAll(pageable);

        repairRequests.forEach(req -> listDTO.add(mapper.fromRequestToDTO(req)));

        System.out.println("service "+repairRequests.getTotalElements());

        return new PageImpl<>(listDTO, pageable, repairRequests.getTotalElements());
    }



}
