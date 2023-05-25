package com.example.myhome.services;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@SpringBootTest
public class RequestServiceTest {

    @Autowired
    RepairRequestService repairRequestService;

    @MockBean
    RepairRequestRepository repairRequestRepository;

    @MockBean private ApartmentService apartmentService;
    @MockBean private OwnerService ownerService;
    @MockBean private AdminService adminService;

    @MockBean private ApartmentRepository apartmentRepository;
    @MockBean private OwnerRepository ownerRepository;
    @MockBean private AdminRepository adminRepository;
    @MockBean private UserRoleRepository userRoleRepository;

    RepairRequest request = new RepairRequest();
    RepairRequestDTOMapper mapper = new RepairRequestDTOMapper();

    @BeforeEach
    void setupRequest() {
        request.setId(1L);
        request.setBest_time_request(LocalDateTime.now());
        request.setOwner(new Owner());
        request.setApartment(new Apartment());
        request.setMaster(new Admin());
        UserRole role = new UserRole();
        role.setName("test");
        request.setMaster_type(role);
        request.setDescription("test");
        request.setRequest_date(LocalDateTime.now());
        request.setStatus(RepairStatus.ACCEPTED);
        request.setComment("test");
    }

    @Test
    void canFindAllRequestsTest() {
        when(repairRequestRepository.findAll()).thenReturn(List.of(new RepairRequest(), new RepairRequest()));
        assertThat(repairRequestService.findAllRequests()).hasSize(2);
    }

    @Test
    void canFindAllBySpecificationTest() throws IllegalAccessException {
        when(userRoleRepository.getReferenceById(anyLong())).thenReturn(new UserRole());
        when(apartmentService.findByNumber(anyLong())).thenReturn(new Apartment());
        when(ownerService.findById(anyLong())).thenReturn(new Owner());
        when(adminService.findAdminById(anyLong())).thenReturn(new Admin());

        FilterForm form = new FilterForm();
        form.setId(1L);
        form.setDescription("test");
        form.setMaster_type(1L);
        form.setPhone("test");
        form.setStatus(RepairStatus.ACCEPTED.name());
        form.setApartment(1L);
        form.setOwner(1L);
        form.setMaster(1L);
        form.setDatetime("2022-11-12 10:30 to 2022-11-13 12:00");
        form.setPage(1);

        List<RepairRequest> list = List.of(request,request,request);
        Page<RepairRequest> page = new PageImpl<>(list, PageRequest.of(1,1), 1);

        when(repairRequestRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
        when(repairRequestRepository.findAll(any(Pageable.class))).thenReturn(page);

        assertThat(repairRequestService.findAllBySpecification(form)).isEqualTo(page);
    }

    @Test
    void canFindAllBySpecificationTest_2() throws IllegalAccessException {
        when(userRoleRepository.getReferenceById(anyLong())).thenReturn(new UserRole());
        when(apartmentService.findByNumber(anyLong())).thenReturn(new Apartment());
        when(ownerService.findById(anyLong())).thenReturn(new Owner());
        when(adminService.findAdminById(anyLong())).thenReturn(new Admin());

        FilterForm form = new FilterForm();
        form.setPage(1);

        List<RepairRequest> list = List.of(request,request,request);
        Page<RepairRequest> page = new PageImpl<>(list, PageRequest.of(1,1), 1);

        when(repairRequestRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
        when(repairRequestRepository.findAll(any(Pageable.class))).thenReturn(page);

        assertThat(repairRequestService.findAllBySpecification(form)).isEqualTo(page);
    }

    @Test
    void canFindAllDTOBySpecificationTest() throws IllegalAccessException {

        when(userRoleRepository.getReferenceById(anyLong())).thenReturn(new UserRole());
        when(apartmentService.findByNumber(anyLong())).thenReturn(new Apartment());
        when(ownerService.findById(anyLong())).thenReturn(new Owner());
        when(adminService.findAdminById(anyLong())).thenReturn(new Admin());

        FilterForm form = new FilterForm();
        form.setId(1L);
        form.setDescription("test");
        form.setMaster_type(1L);
        form.setPhone("test");
        form.setStatus(RepairStatus.ACCEPTED.name());
        form.setApartment(1L);
        form.setOwner(1L);
        form.setMaster(1L);
        form.setDatetime("2022-11-12 10:30 to 2022-11-13 12:00");
        form.setPage(1);

        List<RepairRequest> list = List.of(request,request,request);
        Page<RepairRequest> page = new PageImpl<>(list, PageRequest.of(1,1), 1);

        when(repairRequestRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
        when(repairRequestRepository.findAll(any(Pageable.class))).thenReturn(page);

        assertThat(repairRequestService.findAllBySpecification(form, 1,1)).isInstanceOf(Page.class);

    }

    @Test
    void buildSpecTest() {
        when(userRoleRepository.getReferenceById(anyLong())).thenReturn(new UserRole());
        when(apartmentService.findByNumber(anyLong())).thenReturn(new Apartment());
        when(ownerService.findById(anyLong())).thenReturn(new Owner());
        when(adminService.findAdminById(anyLong())).thenReturn(new Admin());

        FilterForm form = new FilterForm();
        form.setId(1L);
        form.setDescription("test");
        form.setMaster_type(1L);
        form.setPhone("test");
        form.setStatus(RepairStatus.ACCEPTED.name());
        form.setApartment(1L);
        form.setOwner(1L);
        form.setMaster(1L);
        form.setDatetime("2022-11-12 10:30 to 2022-11-13 12:00");

        assertThat(repairRequestService.buildSpecFromFilters(form)).isInstanceOf(Specification.class);
    }

    @Test
    void getMaxIdTest() {
        when(repairRequestRepository.getMaxId()).thenReturn(Optional.of(1L));
        assertThat(repairRequestService.getMaxId()).isEqualTo(1L);
    }

    @Test
    void canFindRequestByIdTest() {
        when(repairRequestRepository.findById(anyLong())).thenReturn(Optional.ofNullable(request));
        assertThat(repairRequestService.findRequestById(1L)).isEqualTo(request);
    }

    @Test
    void canGiveErrorOnFindRequestTest() {
        when(repairRequestRepository.findById(anyLong())).thenThrow(new NotFoundException());
        assertThat(repairRequestService.findRequestById(1L)).isNull();
    }

    @Test
    void canFindRequestDTOByIdTest() {
        when(repairRequestRepository.findById(anyLong())).thenReturn(Optional.ofNullable(request));
        assertThat(repairRequestService.findRequestDTOById(1L)).isInstanceOf(RepairRequestDTO.class);
    }

    @Test
    void canGiveErrorOnFindRequestDTOTest() {
        when(repairRequestRepository.findById(anyLong())).thenThrow(new NotFoundException());
        assertThat(repairRequestService.findRequestDTOById(1L)).isNull();
    }

    @Test
    void canSaveRequestTest() {
        when(repairRequestRepository.save(any(RepairRequest.class))).thenReturn(request);
        assertThat(repairRequestService.saveRequest(request)).isEqualTo(request);
    }

    @Test
    void canGiveErrorOnSaveRequestTest() {
        when(repairRequestRepository.save(any(RepairRequest.class))).thenThrow(new NotFoundException());
        assertThat(repairRequestService.saveRequest(request)).isNull();
    }

    @Test
    void canSaveRequestDTOTest() {
        RepairRequestDTO dto = mapper.fromRequestToDTO(request);

        when(apartmentRepository.getReferenceById(anyLong())).thenReturn(request.getApartment());
        when(ownerRepository.getReferenceById(anyLong())).thenReturn(request.getOwner());
        when(adminRepository.getReferenceById(anyLong())).thenReturn(request.getMaster());

        when(repairRequestRepository.save(any())).thenReturn(request);

        assertThat(repairRequestService.saveRequest(dto)).isInstanceOf(RepairRequest.class);
    }

    @Test
    void canSaveRequestDTOTest_2() {
        RepairRequestDTO dto = mapper.fromRequestToDTO(request);

        when(apartmentRepository.getReferenceById(anyLong())).thenReturn(request.getApartment());
        when(ownerRepository.getReferenceById(anyLong())).thenReturn(request.getOwner());
        when(adminRepository.getReferenceById(anyLong())).thenReturn(request.getMaster());

        when(repairRequestRepository.save(any())).thenThrow(new NotFoundException());

        assertThat(repairRequestService.saveRequest(dto)).isNull();
    }

    @Test
    void canDeleteRequestByIdTest() {
        repairRequestService.deleteRequestById(1L);
    }

    @Test
    void canGiveErrorOnDeleteRequestByIdTest() {
        doThrow(new NotFoundException()).when(repairRequestRepository).deleteById(anyLong());
        repairRequestService.deleteRequestById(1L);
    }

    @Test
    void canFindAllDTOTest() {
        List<RepairRequest> list = List.of(request, request, request);
        Page<RepairRequest> page = new PageImpl<>(list, PageRequest.of(1,1),1);

        when(repairRequestRepository.findAll(any(Pageable.class))).thenReturn(page);

        assertThat(repairRequestService.findAll(PageRequest.of(1,1)).getContent()).hasSize(3);
    }

}
