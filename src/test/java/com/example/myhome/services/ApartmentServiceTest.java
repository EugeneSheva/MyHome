package com.example.myhome.services;

import com.example.myhome.dto.ApartmentDTO;
import com.example.myhome.mapper.ApartmentDTOMapper;
import com.example.myhome.model.*;
import com.example.myhome.repository.ApartmentRepository;
import com.example.myhome.service.ApartmentService;
import com.example.myhome.service.BuildingService;
import com.example.myhome.util.FileUploadUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ApartmentServiceTest {

    @Autowired
    private ApartmentService apartmentService;

    @MockBean
    private ApartmentRepository apartmentRepository;

    @MockBean
    private FileUploadUtil fileUploadUtil;

    @MockBean
    private BuildingService buildingService;

    static Apartment testApartment;
    static ApartmentDTO testDTO;
    static ApartmentDTOMapper apartmentMapper;
    static ObjectMapper jsonMapper;

    static List<Apartment> apartmentList;
    static Page<Apartment> apartmentPage;
    static List<ApartmentDTO> apartmentDTOList;
    static Page<ApartmentDTO> apartmentDTOPage;

    @BeforeAll
    static void setupObjects() {
        testApartment = new Apartment();
        testApartment.setId(1L);
        testApartment.setNumber(1L);
        testApartment.setAccount(new ApartmentAccount());
        testApartment.setBuilding(new Building());
        testApartment.setOwner(new Owner());
        testApartment.setBalance(100.0);
        testApartment.setTariff(new Tariff());
        testApartment.setSquare(100.0);

        apartmentMapper = new ApartmentDTOMapper();

        testDTO = apartmentMapper.fromApartmentToDTO(testApartment);

        jsonMapper = new JsonMapper();

        apartmentList = List.of(testApartment, testApartment, testApartment);
        apartmentDTOList = List.of(testDTO, testDTO, testDTO);
        apartmentPage = new PageImpl<>(apartmentList, PageRequest.of(1,1),1);
        apartmentDTOPage = new PageImpl<>(apartmentDTOList, PageRequest.of(1,1),1);
    }

    @BeforeEach
    void setupMocks() {
        when(apartmentRepository.findById(anyLong())).thenReturn(Optional.ofNullable(testApartment));
        when(apartmentRepository.save(any(Apartment.class))).thenReturn(testApartment);
        when(apartmentRepository.findAll()).thenReturn(apartmentList);
    }

    @Test
    void contextLoads() {
        assertThat(apartmentService).isNotNull();
    }

    @Test
    void findByIDTest() {
        assertThat(apartmentService.findById(testApartment.getId())).isEqualTo(testApartment);
    }

    @Test
    void findByNumberTest() {
        assertThat(apartmentService.findByNumber(testApartment.getNumber())).isEqualTo(testApartment);
    }

    @Test
    void saveTest() {
        assertThat(apartmentService.save(testApartment)).isEqualTo(testApartment);
    }

    @Test
    void saveDTOTest() {
        assertThat(apartmentService.save(testDTO)).isEqualTo(testApartment);
    }

    @Test
    void deleteByIdTest() {
        apartmentService.deleteById(testApartment.getId());
    }

    @Test
    void getNumberByIdTest() {
        assertThat(apartmentService.getNumberById(testApartment.getId())).isEqualTo(testApartment.getNumber());
    }

    @Test
    void findAllTest() {
        assertThat(apartmentService.findAll()).isEqualTo(apartmentList);
    }

    @Test
    void findAllPageTest() {
        assertThat(apartmentService.findAll(PageRequest.of(1,1))).isEqualTo(apartmentPage);
    }

    @Test
    void findAllDTOTest() {
        assertThat(apartmentService.findDtoApartments()).isEqualTo(apartmentDTOList);
    }

    @Test
    void convertApartmentsToApartmentsDTOTest() {
        assertThat(apartmentService.convertApartmentsToApartmentsDTO(apartmentList)).isEqualTo(apartmentDTOList);
    }

    @Test
    void convertSingleApartmentToDTOTest() {
        assertThat(apartmentService.convertApartmentsToApartmentsDTO(testApartment)).isEqualTo(testDTO);
    }

    @Test
    void findSingleDTOTest() {
        assertThat(apartmentService.findApartmentDto(testApartment.getId())).isEqualTo(testDTO);
    }

    @Test
    void findDTOWithDebtTest() {

    }

    @Test
    void getQuantityTest() {
        assertThat(apartmentService.getQuantity()).isEqualTo(3);
    }

}
