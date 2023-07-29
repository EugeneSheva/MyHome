package com.example.myhome.services;

import com.example.myhome.dto.BuildingDTO;
import com.example.myhome.mapper.ApartmentDTOMapper;
import com.example.myhome.mapper.BuildingDTOMapper;
import com.example.myhome.model.Apartment;
import com.example.myhome.model.Building;
import com.example.myhome.model.filter.FilterForm;
import com.example.myhome.repository.BuildingRepository;
import com.example.myhome.service.BuildingService;
import com.example.myhome.util.FileUploadUtil;
import org.junit.jupiter.api.BeforeAll;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.parameters.P;
import org.springframework.validation.FieldError;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest
public class BuildingServiceTest {

    @Autowired
    private BuildingService buildingService;

    @MockBean private BuildingRepository buildingRepository;
    @MockBean private FileUploadUtil fileUploadUtil;

    static BuildingDTOMapper buildingDTOMapper;
    static ApartmentDTOMapper apartmentDTOMapper;

    static Building testBuilding;
    static BuildingDTO testDTO;

    static List<Building> buildingList;
    static Page<Building> buildingPage;

    static List<BuildingDTO> buildingDTOList;
    static Page<BuildingDTO> buildingDTOPage;

    @BeforeAll
    static void setupObjects() {
        testBuilding = new Building();
        testBuilding.setId(1L);
        testBuilding.setApartments(List.of(new Apartment(), new Apartment()));
        testBuilding.setAdmins(new ArrayList<>());
        testBuilding.setAddress("test");
        testBuilding.setFloors(List.of("test","test"));
        testBuilding.setSections(List.of("test","test"));
        testBuilding.setName("test");
        testBuilding.setImg1("test");
        testBuilding.setImg2("test");
        testBuilding.setImg3("test");
        testBuilding.setImg4("test");
        testBuilding.setImg5("test");

        buildingDTOMapper = new BuildingDTOMapper();

        testDTO = buildingDTOMapper.fromBuildingToDTO(testBuilding);

        apartmentDTOMapper = new ApartmentDTOMapper();

        buildingList = List.of(testBuilding, testBuilding, testBuilding);
        buildingDTOList = List.of(testDTO, testDTO, testDTO);

        buildingPage = new PageImpl<>(buildingList, PageRequest.of(1,1), 1);
        buildingDTOPage = new PageImpl<>(buildingDTOList, PageRequest.of(1,1),1);
    }

    @BeforeEach
    void setupMocks() {
        when(buildingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(testBuilding));
        when(buildingRepository.save(any(Building.class))).thenReturn(testBuilding);
        when(buildingRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(buildingPage);
        when(buildingRepository.findAll(any(Pageable.class))).thenReturn(buildingPage);
        when(buildingRepository.findByName(anyString())).thenReturn(testBuilding);
        when(buildingRepository.findByName(anyString(), any())).thenReturn(buildingPage);
        when(buildingRepository.getSectionApartments(anyLong(), anyString())).thenReturn(testBuilding.getApartments());
        when(buildingRepository.count()).thenReturn(3L);
        when(buildingRepository.getReferenceById(anyLong())).thenReturn(testBuilding);
    }

    @Test
    void contextLoads() {
        assertThat(buildingService).isNotNull();
    }

    @Test
    void findByIdTest() {
        assertThat(buildingService.findById(testBuilding.getId())).isEqualTo(testBuilding);
    }

    @Test
    void findAllTest() {
        assertThat(buildingService.findAll()).isEqualTo(buildingList);
    }

    @Test
    void findAllPageTest() {
        assertThat(buildingService.findAll(PageRequest.of(1,1))).isEqualTo(buildingPage);
    }

    @Test
    void findAllDTOTest() {
        assertThat(buildingService.findAllDTO()).isEqualTo(buildingDTOList);
    }

    @Test
    void findDTOByIdTest() {
        assertThat(buildingService.findBuildingDTObyId(testBuilding.getId())).isEqualTo(testDTO);
    }

    @Test
    void saveTest() {
        assertThat(buildingService.save(testBuilding)).isEqualTo(testBuilding);
    }

    @Test
    void findAllBySpecificationTest() {
        assertThat(buildingService.findAllBySpecification(new FilterForm(), 1,1).getContent()).hasSameElementsAs(buildingDTOList);
    }

    @Test
    void findByPageTest() {
        assertThat(buildingService.findByPage("test", 2)).hasSameElementsAs(buildingDTOList);
    }

    @Test
    void getSectionApartmentsTest() {
        assertThat(buildingService.getSectionApartments(testBuilding.getId(), "test")).hasSize(testBuilding.getApartments().size());
    }

    @Test
    void countBuildingTest() {
        assertThat(buildingService.countBuildings()).isEqualTo(3L);
    }

    @Test
    void deleteByIdTest() {
        buildingService.deleteById(testBuilding.getId());
    }

    @Test
    void getQuantityTest() {
        assertThat(buildingService.getQuantity()).isEqualTo(3L);
    }

    @Test
    void saveBuildingImagesTest() throws IOException {
        MockMultipartFile file1 = new MockMultipartFile("file1", "file1.jpg", "multipart/form-data", new byte[1]);
        MockMultipartFile file2 = new MockMultipartFile("file2", "file2.jpg", "multipart/form-data", new byte[1]);
        MockMultipartFile file3 = new MockMultipartFile("file3", "file3.jpg", "multipart/form-data", new byte[1]);
        MockMultipartFile file4 = new MockMultipartFile("file4", "file4.jpg", "multipart/form-data", new byte[1]);
        MockMultipartFile file5 = new MockMultipartFile("file5", "file5.jpg", "multipart/form-data", new byte[1]);

        assertThat(buildingService.saveBuildingImages(1L,file1,file2,file3,file4,file5)).isInstanceOf(Building.class);
    }

    @Test
    void saveBuildingImagesTest_2() throws IOException {
        MockMultipartFile file1 = new MockMultipartFile("file1", "file1.jpg", "multipart/form-data", new byte[0]);
        MockMultipartFile file2 = new MockMultipartFile("file2", "file2.jpg", "multipart/form-data", new byte[0]);
        MockMultipartFile file3 = new MockMultipartFile("file3", "file3.jpg", "multipart/form-data", new byte[0]);
        MockMultipartFile file4 = new MockMultipartFile("file4", "file4.jpg", "multipart/form-data", new byte[0]);
        MockMultipartFile file5 = new MockMultipartFile("file5", "file5.jpg", "multipart/form-data", new byte[0]);

        assertThat(buildingService.saveBuildingImages(1L,file1,file2,file3,file4,file5)).isInstanceOf(Building.class);
    }

    @Test
    void convertBuildingToDTOTest() {
        assertThat(buildingService.convertBuildingToBuildingDTO(testBuilding)).isEqualTo(testDTO);
    }

    @Test
    void convertBuildingListToDTOTest() {
        assertThat(buildingService.convertBuildingToBuildingDTO(buildingList)).isEqualTo(buildingDTOList);
    }

    @Test
    void findByNameTest() {
        assertThat(buildingService.findByName(testBuilding.getName())).isEqualTo(testBuilding);
    }

    @Test
    void validateImgTest() {
        assertThat(buildingService.validateImg(null, "test")).isNull();

        MockMultipartFile testFile = new MockMultipartFile("test", "test", "text/html", new byte[1024]);
        assertThat(buildingService.validateImg(testFile, "test")).isInstanceOf(FieldError.class);

        testFile = new MockMultipartFile("test","test","image/jpg", new byte[21*1024*1024]);
        assertThat(buildingService.validateImg(testFile, "test")).isInstanceOf(FieldError.class);
    }
}
