package com.example.myhome;

import com.example.myhome.home.exception.EmptyObjectException;
import com.example.myhome.home.model.Service;
import com.example.myhome.home.model.Tariff;
import com.example.myhome.home.repository.TariffRepository;
import com.example.myhome.home.service.TariffService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TariffServiceTests {

    @MockBean
    private TariffRepository tariffRepository;

    @Autowired
    private TariffService tariffService;

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
        tariff.setTariffComponents(new HashMap<>());
        tariff.getTariffComponents().put(service, 0.0);
        tariff.setName("TEST");

        Tariff expected = new Tariff();
        expected.setId(1L);
        expected.setTariffComponents(new HashMap<>());
        expected.getTariffComponents().put(service, 0.0);
        expected.setName("TEST");

        given(tariffRepository.save(tariff)).willReturn(expected);

        tariffService.saveTariff(tariff);
        verify(tariffRepository).save(tariff);

        assertThat(tariffService.saveTariff(tariff)).isEqualTo(expected);
    }

    @Test
    void cantSaveTariffWithoutComponents() {
        Tariff tariff = new Tariff();
        tariff.setTariffComponents(new HashMap<>());
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
        tariff.setTariffComponents(new HashMap<>());
        tariff.getTariffComponents().put(service, 0.0);
        tariff.setName("TEST");

        Tariff expected = new Tariff();
        expected.setId(1L);
        expected.setTariffComponents(new HashMap<>());
        expected.getTariffComponents().put(service, 0.0);
        expected.setName("TEST");

        given(tariffRepository.save(tariff)).willReturn(expected);
        given(tariffRepository.findById(1L)).willReturn(Optional.of(expected));

        tariffService.saveTariff(tariff);
        verify(tariffRepository).save(tariff);

        assertThat(tariffService.findTariffById(1L)).isEqualTo(expected);
    }

    // todo: сделать тесты добавления компонентов в тарифы
}
