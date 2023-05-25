package com.example.myhome.services;

import com.example.myhome.dto.MeterDataDTO;
import com.example.myhome.mapper.MeterDTOMapper;
import com.example.myhome.model.*;
import com.example.myhome.model.filter.FilterForm;
import com.example.myhome.repository.ApartmentRepository;
import com.example.myhome.repository.BuildingRepository;
import com.example.myhome.repository.MeterDataRepository;
import com.example.myhome.repository.ServiceRepository;
import com.example.myhome.service.ApartmentService;
import com.example.myhome.service.BuildingService;
import com.example.myhome.service.MeterDataService;
import com.example.myhome.service.ServiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class MeterDataServiceTest {

    @MockBean MeterDataRepository repository;
    @MockBean
    ApartmentRepository apartmentRepository;
    @MockBean
    BuildingRepository buildingRepository;
    @MockBean
    ServiceRepository serviceRepository;
    @MockBean
    ApartmentService apartmentService;
    @MockBean
    BuildingService buildingService;
    @MockBean
    ServiceService serviceService;
    @Autowired MeterDataService service;
    @Autowired MeterDTOMapper mapper;

    MeterData meterData = new MeterData();

    @BeforeEach
    void setupMeter() {
        meterData.setId(1L);
        Service service1 = new Service();
        service1.setId(1L);
        service1.setName("test");
        Unit unit = new Unit();
        unit.setId(1L);
        unit.setName("test");
        service1.setUnit(unit);
        meterData.setService(service1);
        Apartment apartment = new Apartment();
        apartment.setId(1L);
        apartment.setNumber(10L);
        Owner owner = new Owner();
        owner.setId(1L);
        apartment.setOwner(owner);
        meterData.setApartment(apartment);
        Building building = new Building();
        building.setId(1L);
        building.setName("test");
        meterData.setBuilding(building);
        meterData.setStatus(MeterPaymentStatus.PAID);
        meterData.setSection("test");
        meterData.setCurrentReadings(100.0);
    }

    @Test
    void findAllMetersTest() {
        List<MeterData> expected = List.of(new MeterData(), new MeterData());
        given(repository.findAll()).willReturn(expected);
        List<MeterData> list = service.findAllMeters();
        verify(repository).findAll();
        assertThat(list.size()).isEqualTo(2);
    }

    @Test
    void findSingleMeterDataByApartmentAndServiceId() {
        MeterData m = new MeterData();
        Apartment a = new Apartment();
        a.setId(1L);
        Service s = new Service();
        s.setId(1L);
        m.setApartment(a);
        m.setService(s);
        List<MeterData> expected = List.of(m,m,m);
        given(repository.findSingleMeterData(anyLong(), anyLong())).willReturn(expected);
        List<MeterData> testList = service.findSingleMeterData(1L, 1L);
        verify(repository).findSingleMeterData(1L,1L);
        assertThat(testList).isEqualTo(expected);
    }

    @Test
    void findSingleMeterDataByApartmentId() {
        MeterData m = new MeterData();
        Apartment a = new Apartment();
        a.setId(1L);
        m.setApartment(a);
        List<MeterData> expected = List.of(m,m,m);
        given(repository.findByApartmentId(anyLong())).willReturn(expected);
        List<MeterData> testList = service.findSingleMeterData(1L, null);
        verify(repository).findByApartmentId(1L);
        assertThat(testList).isEqualTo(expected);
    }

    @Test
    void testFindSingleMeterData() {
    }

    @Test
    void findMeterDataByIdTest() {
        MeterData expected = new MeterData();
        expected.setId(1L);

        given(repository.findById(1L)).willReturn(Optional.of(expected));

        MeterData m = service.findMeterDataById(1L);

        assertThat(m).isEqualTo(expected);
    }

    @Test
    void throwsExceptionOnMeterNotFound(){
        assertThrows(NoSuchElementException.class, () -> service.findMeterDataById(1L));
    }

    @Test
    void transformIntoDTOTest() {
        LocalDate now = LocalDate.now();
        MeterData m = new MeterData();
        m.setId(1L);
        m.setCurrentReadings(10.0);
        m.setSection("test");
        m.setStatus(MeterPaymentStatus.NEW);
        m.setDate(now);
        Apartment a = new Apartment();
        a.setId(1L);
        a.setNumber(10L);
        Building b = new Building();
        b.setId(1L);
        b.setName("test");
        Service s = new Service();
        s.setId(1L);
        s.setName("test");
        Unit u = new Unit();
        u.setId(1L);
        u.setName("test");
        s.setUnit(u);
        Owner o = new Owner();
        o.setId(1L);
        o.setFirst_name("test");
        o.setFathers_name("test");
        o.setLast_name("test");
        a.setOwner(o);

        m.setApartment(a);
        m.setBuilding(b);
        m.setService(s);

        MeterDataDTO dto = mapper.fromMeterToDTO(m);

        assertThat(dto.getId()).isEqualTo(m.getId());
        assertThat(dto.getReadings()).isEqualTo(m.getCurrentReadings());
        assertThat(dto.getApartmentID()).isEqualTo(m.getApartment().getId());
        assertThat(dto.getApartmentNumber()).isEqualTo(m.getApartment().getNumber());
        assertThat(dto.getBuildingID()).isEqualTo(m.getBuilding().getId());
        assertThat(dto.getBuildingName()).isEqualTo(m.getBuilding().getName());
        assertThat(dto.getServiceID()).isEqualTo(m.getService().getId());
        assertThat(dto.getServiceName()).isEqualTo(m.getService().getName());
        assertThat(dto.getServiceUnitName()).isEqualTo(m.getService().getUnit().getName());
        assertThat(dto.getSection()).isEqualTo(m.getSection());
        assertThat(dto.getStatus()).isEqualTo(m.getStatus().getName());
        assertThat(dto.getDate()).isEqualTo(m.getDate());
        assertThat(dto.getApartmentOwnerID()).isEqualTo(m.getApartment().getOwner().getId());
        assertThat(dto.getApartmentOwnerFullName()).isEqualTo(m.getApartment().getOwner().getFullName());
    }

    @Test
    void getMaxIdTest() {
        MeterData m1 = new MeterData();
        MeterData m2 = new MeterData();
        MeterData m3 = new MeterData();
        m1.setId(10L);
        m2.setId(20L);
        m3.setId(30L);
        List<MeterData> list = List.of(m1,m2,m3);
        Optional<Long> maxId = list.stream().map(MeterData::getId).max(Long::compareTo);

        given(repository.getMaxId()).willReturn(maxId);

        assertThat(service.getMaxId()).isEqualTo(30L);
    }

    @Test
    void getZeroAsMaxIdTest() {
        List<MeterData> list = List.of();
        Optional<Long> maxId = list.stream().map(MeterData::getId).max(Long::compareTo);
        given(repository.getMaxId()).willReturn(maxId);
        assertThat(service.getMaxId()).isEqualTo(0);
    }

    @Test
    void canFindSingleMeterDataPageTest() {
        FilterForm form = new FilterForm();
        form.setId(1L);
        form.setStatus("NEW");
        form.setDate("2022-11-11 to 2022-11-12");


        List<MeterData> list = List.of(meterData, meterData, meterData);
        Page<MeterData> page = new PageImpl<>(list, PageRequest.of(1,1),1);

        Apartment apartment1 = new Apartment();
        apartment1.setId(1L);
        Service service2 = new Service();
        service2.setId(1L);
        when(apartmentRepository.getReferenceById(anyLong())).thenReturn(apartment1);
        when(serviceRepository.getReferenceById(anyLong())).thenReturn(service2);
        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        assertThat(service.findSingleMeterData(form, PageRequest.of(1,1)).getContent().size()).isEqualTo(3);
    }

    @Test
    void canFindAllByFiltersAndPageTest() {
        FilterForm form = new FilterForm();
        form.setId(1L);
        form.setStatus("NEW");
        form.setDate("2022-11-11 to 2022-11-12");


        List<MeterData> list = List.of(meterData, meterData, meterData);
        Page<MeterData> page = new PageImpl<>(list, PageRequest.of(1,1),1);

        when(apartmentRepository.getReferenceById(anyLong())).thenReturn(new Apartment());
        when(serviceRepository.getReferenceById(anyLong())).thenReturn(new Service());
        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        assertThat(service.findAllByFiltersAndPage(form, PageRequest.of(1,1)).getContent().size()).isEqualTo(3);
    }

    @Test
    void buildSpecTest() {
        FilterForm form = new FilterForm();
        form.setId(1L);
        form.setStatus("NEW");
        form.setDate("2022-11-11 to 2022-11-12");
        form.setBuilding(1L);
        form.setApartment(1L);
        form.setSection("test");
        form.setService(1L);

        when(buildingService.findById(anyLong())).thenReturn(new Building());
        when(apartmentService.findById(anyLong())).thenReturn(new Apartment());
        when(serviceService.findServiceById(anyLong())).thenReturn(new Service());

        assertThat(service.buildSpecFromFilters(form)).isInstanceOf(Specification.class);
    }

    @Test
    void canFindAllBySpecTest() {
        FilterForm form = new FilterForm();
        form.setId(1L);
        form.setStatus("NEW");
        form.setDate("2022-11-11 to 2022-11-12");
        form.setBuilding(1L);
        form.setApartment(1L);
        form.setSection("test");
        form.setService(1L);

        when(buildingService.findById(anyLong())).thenReturn(new Building());
        when(apartmentService.findById(anyLong())).thenReturn(new Apartment());
        when(serviceService.findServiceById(anyLong())).thenReturn(new Service());

        List<MeterData> list = List.of(meterData, meterData, meterData);
        Page<MeterData> page = new PageImpl<>(list, PageRequest.of(1,1),1);

        when(repository.findAll(any(Specification.class),any(Pageable.class))).thenReturn(page);

        assertThat(service.findAllBySpecification(form, 1,1).getContent().size()).isEqualTo(3);
    }

    @Test
    void canFindMeterDataDTOTest() {
        when(repository.findById(anyLong())).thenReturn(Optional.ofNullable(meterData));
        assertThat(service.findMeterDataDTOById(1L)).isInstanceOf(MeterDataDTO.class);
    }

    @Test
    void canSaveMeterDataTest() {
        assertThat(service.saveMeterData(new MeterData())).isInstanceOf(MeterData.class);
    }

    @Test
    void canSaveMeterDataFromDTOTest() {
        MeterDataDTO dto = mapper.fromMeterToDTO(meterData);
        dto.setStatus(meterData.getStatus().name());
        when(apartmentRepository.getReferenceById(anyLong())).thenReturn(new Apartment());
        when(buildingRepository.getReferenceById(anyLong())).thenReturn(new Building());
        when(serviceRepository.getReferenceById(anyLong())).thenReturn(new Service());
        assertThat(service.saveMeterData(dto)).isInstanceOf(MeterData.class);
    }

    @Test
    void canSaveMeterDataFromAJAXTest() {
        when(buildingService.findById(anyLong())).thenReturn(new Building());
        when(apartmentService.findById(anyLong())).thenReturn(new Apartment());
        when(serviceService.findServiceById(anyLong())).thenReturn(new Service());

        assertThat(service.saveMeterDataAJAX(1L, "10", "test", "10", "100", "NEW", "10", "2022-11-11"))
                .isInstanceOf(MeterData.class);
    }

    @Test
    void canSaveMeterDataFromAJAXTest_2() {
        assertThat(service.saveMeterDataAJAX(1L, "10", "test", "10", "100", "Новое", "10", "2022-11-11"))
                .isInstanceOf(MeterData.class);
    }

    @Test
    void canDeleteMeterByIdTest() {
        service.deleteMeterById(1L);
    }

}