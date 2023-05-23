package com.example.myhome.services;

import com.example.myhome.model.Service;
import com.example.myhome.model.Unit;
import com.example.myhome.repository.ServiceRepository;
import com.example.myhome.repository.UnitRepository;
import com.example.myhome.service.impl.ServiceServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class ServiceServiceTest {

    @MockBean private ServiceRepository serviceRepository;
    @MockBean private UnitRepository unitRepository;

    @Autowired private ServiceServiceImpl service;

    @Test
    void sanityCheck(){
        assertThat(serviceRepository).isNotNull();
        assertThat(unitRepository).isNotNull();
        assertThat(service).isNotNull();
    }

    @Test
    void canLoadAllServicesAndUnits(){
        List<Service> serviceList = List.of(new Service(), new Service());
        List<Unit> unitList = List.of(new Unit(), new Unit());

        given(serviceRepository.findAll()).willReturn(serviceList);
        given(unitRepository.findAll()).willReturn(unitList);

        assertThat(service.findAllServices()).isEqualTo(serviceList);
        assertThat(service.findAllUnits()).isEqualTo(unitList);
    }

    @Test
    void canLoadSingleServiceAndUnit(){
        Service service1 = new Service();
        service1.setId(1L);
        service1.setName("Test");
        Unit unit = new Unit();
        unit.setId(1L);
        unit.setName("Test");

        given(serviceRepository.findById(1L)).willReturn(Optional.of(service1));
        given(unitRepository.findById(1L)).willReturn(Optional.of(unit));

        assertThat(service.findServiceById(1L)).isEqualTo(service1);
        assertThat(service.getServiceNameById(1L)).isEqualTo("Test");
        assertThat(service.getUnitNameById(1L)).isEqualTo("Test");
    }

    @Test
    void canAddNewUnits() {
        String[] unit_names = new String[5];
        for (int i = 0; i < 5; i++) {
            unit_names[i] = "Test-"+(i+1);
        }

        List<Unit> initialList = new ArrayList<>();

        List<Unit> expected = List.of(new Unit("Test-1"), new Unit("Test-2"),new Unit("Test-3"),
                new Unit("Test-4"));

        given(unitRepository.saveAll(initialList)).willReturn(initialList);
        assertThat(service.addNewUnits(initialList, unit_names)).isEqualTo(expected);
    }

    @Test
    void doesntAddEmptyUnits() {
        String[] unit_names = null;
        List<Unit> initialList = new ArrayList<>();

        given(unitRepository.saveAll(new ArrayList<>())).willReturn(new ArrayList<>());
        assertThat(service.addNewUnits(initialList, unit_names)).isEqualTo(initialList);
    }

    @Test
    void canAddNewServices() {
        Unit testUnit = new Unit("TestUnit");
        String[] new_service_names = new String[5];
        String[] new_service_unit_names = new String[5];
        String[] new_service_show_in_meters = new String[5];

        List<Service> initialList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            new_service_names[i] = "Test-"+(i+1);
            new_service_unit_names[i] = "TestUnit";
            new_service_show_in_meters[i] = "";
        }

        List<Service> expected = List.of(new Service("Test-1", true, testUnit),
                                         new Service("Test-2", true, testUnit),
                                         new Service("Test-3", true, testUnit),
                                         new Service("Test-4", true, testUnit));

        given(unitRepository.findByName("TestUnit")).willReturn(Optional.of(testUnit));
        given(serviceRepository.saveAll(initialList)).willReturn(initialList);

        assertThat(service.addNewServices(initialList,
                new_service_names, new_service_unit_names,
                new_service_show_in_meters)).isEqualTo(expected);
    }

    @Test
    void doesntAddServicesWithEmptyNames() {
        Unit testUnit = new Unit("TestUnit");
        String[] new_service_names = new String[5];
        String[] new_service_unit_names = new String[5];
        String[] new_service_show_in_meters = new String[5];

        List<Service> initialList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            new_service_names[i] = "Test-"+(i+1);
            new_service_unit_names[i] = "TestUnit";
            new_service_show_in_meters[i] = "";
        }
        new_service_names[0] = "";
        new_service_names[1] = "";

        List<Service> expected = List.of(new Service("Test-3", true, testUnit),
                new Service("Test-4", true, testUnit));

        List<Service> notExpected = List.of(new Service("", true, testUnit),
                new Service("", true, testUnit),
                new Service("Test-3", true, testUnit),
                new Service("Test-4", true, testUnit));

        given(unitRepository.findByName("TestUnit")).willReturn(Optional.of(testUnit));
        given(serviceRepository.saveAll(initialList)).willReturn(initialList);

        assertThat(service.addNewServices(initialList,
                new_service_names, new_service_unit_names,
                new_service_show_in_meters)).isEqualTo(expected);

        assertThat(service.addNewServices(initialList,
                new_service_names, new_service_unit_names,
                new_service_show_in_meters)).isNotEqualTo(notExpected);
    }

    @Test
    void canSaveServiceTest() {
        service.saveService(new Service());
    }

    @Test
    void canDeleteServiceByIdTest() {
        service.deleteServiceById(1L);
    }

    @Test
    void canDeleteUnitByIdTest() {
        service.deleteUnitById(1L);
    }
}
