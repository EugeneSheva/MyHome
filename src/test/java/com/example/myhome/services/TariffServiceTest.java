package com.example.myhome.services;

import com.example.myhome.exception.EmptyObjectException;
import com.example.myhome.model.Service;
import com.example.myhome.model.Tariff;
import com.example.myhome.repository.ServiceRepository;
import com.example.myhome.repository.TariffRepository;
import com.example.myhome.service.impl.TariffServiceImpl;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationManager;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TariffServiceTest {

    @MockBean private TariffRepository tariffRepository;
    @MockBean private ServiceRepository serviceRepository;
    @MockBean private AuthenticationManager authenticationManager;

    @Autowired private TariffServiceImpl tariffService;

    @Test
    @Order(1)
    void sanityCheck() {
        assertThat(tariffRepository).isNotNull();
        assertThat(tariffService).isNotNull();
    }

    @Test
    void canSaveTariff() {
        Service service = new Service();

        Tariff tariff = new Tariff();
        tariff.setComponents(new HashMap<>());
        tariff.getComponents().put(service, 0.0);
        tariff.setName("TEST");

        Tariff expected = new Tariff();
        expected.setId(1L);
        expected.setComponents(new HashMap<>());
        expected.getComponents().put(service, 0.0);
        expected.setName("TEST");

        given(tariffRepository.save(tariff)).willReturn(expected);

        tariffService.saveTariff(tariff);
        verify(tariffRepository).save(tariff);

        assertThat(tariffService.saveTariff(tariff)).isEqualTo(expected);
    }

    @Test
    void cantSaveTariffWithoutComponents() {
        Tariff tariff = new Tariff();
        tariff.setComponents(new HashMap<>());
        tariff.setName("TEST");

        given(tariffRepository.save(tariff)).willReturn(tariff);

        Exception exception = assertThrows(EmptyObjectException.class, () -> tariffService.saveTariff(tariff));

        String expectedMessage = "Can't save tariff without components";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

    }

    @Test
    void canLoadTariffs() {
        List<Tariff> tariffList = List.of(new Tariff(), new Tariff());

        given(tariffRepository.findAll()).willReturn(tariffList);

        tariffService.findAllTariffs();
        verify(tariffRepository).findAll();
        assertThat(tariffService.findAllTariffs())
                .isNotNull()
                .hasSize(2);
    }

    @Test
    void canLoadTariffsSortedTest() {
        List<Tariff> tariffList = List.of(new Tariff(), new Tariff());

        given(tariffRepository.findAll(any(Sort.class))).willReturn(tariffList);

        tariffService.findAllTariffsSorted(true);
        verify(tariffRepository).findAll();
        assertThat(tariffService.findAllTariffs())
                .isNotNull()
                .hasSize(2);

        tariffService.findAllTariffsSorted(false);
        verify(tariffRepository).findAll();
        assertThat(tariffService.findAllTariffs())
                .isNotNull()
                .hasSize(2);
    }

    @Test
    void canDeleteTariffTest() {
        tariffService.deleteTariffById(1L);
    }

    @Test
    void canBuildComponentsListTest() {
        assertThat(tariffService.buildComponentsList(new String[]{"test1","test1"}, new String[]{"1","1"}))
                .hasSize(2);
    }

    @Test
    void canBuildComponentsMapTest() {
        Service service = new Service();
        when(serviceRepository.findByName(anyString())).thenReturn(Optional.of(service));
        assertThat(tariffService.buildComponentsMap(new String[]{"test1","test1"}, new String[]{"1","1"}).entrySet()).hasSize(2);
    }

    @Test
    void canBuildComponentsMapTest_2() {
        assertThat(tariffService.buildComponentsMap(null, null)).isInstanceOf(HashMap.class);
    }

    @Test
    void canBuildComponentsMapTest_3() {
        Service service = new Service();
        when(serviceRepository.findByName(anyString())).thenReturn(Optional.of(service));
        assertThat(tariffService.buildComponentsMap(new String[]{"test1","test1"}, new String[]{"test","test"}).entrySet()).hasSize(0);
    }

    @Test
    void canLoadSingleTariffById() {
        Tariff tariff = new Tariff();
        tariff.setId(2L);
        tariff.setName("TEST");

        given(tariffRepository.findById(2L)).willReturn(Optional.of(tariff));
        tariffService.findTariffById(2L);
        verify(tariffRepository).findById(2L);
        assertThat(tariffService.findTariffById(2L)).isEqualTo(tariff);
    }

    @Test
    void canSaveAndThenLoadTariff() {
        Service service = new Service();

        Tariff tariff = new Tariff();
        tariff.setComponents(new HashMap<>());
        tariff.getComponents().put(service, 0.0);
        tariff.setName("TEST");

        Tariff expected = new Tariff();
        expected.setId(1L);
        expected.setComponents(new HashMap<>());
        expected.getComponents().put(service, 0.0);
        expected.setName("TEST");

        given(tariffRepository.save(tariff)).willReturn(expected);
        given(tariffRepository.findById(1L)).willReturn(Optional.of(expected));

        tariffService.saveTariff(tariff);
        verify(tariffRepository).save(tariff);

        assertThat(tariffService.findTariffById(1L)).isEqualTo(expected);
    }

    // todo: сделать тесты добавления компонентов в тарифы
}
