package com.example.myhome.services;

import com.example.myhome.dto.MeterDataDTO;
import com.example.myhome.mapper.MeterDTOMapper;
import com.example.myhome.model.*;
import com.example.myhome.repository.MeterDataRepository;
import com.example.myhome.service.MeterDataService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

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

@SpringBootTest
class MeterDataServiceTest {

    @MockBean MeterDataRepository repository;
    @Autowired MeterDataService service;
    @Autowired MeterDTOMapper mapper;

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
    void saveMeterData() {
    }

    @Test
    void buildSpecFromFiltersTest() {

    }
}