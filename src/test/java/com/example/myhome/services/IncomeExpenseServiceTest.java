package com.example.myhome.services;

import com.example.myhome.exception.NotFoundException;
import com.example.myhome.model.IncomeExpenseItems;
import com.example.myhome.repository.IncomeExpenseRepository;
import com.example.myhome.service.IncomeExpenseItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.ConstraintViolation;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class IncomeExpenseServiceTest {

    @MockBean private IncomeExpenseRepository incomeExpenseRepository;
    @Autowired private IncomeExpenseItemService service;
    @Autowired private LocalValidatorFactoryBean validator;

    IncomeExpenseItems item;

    @BeforeEach
    void createItem() {
        item = new IncomeExpenseItems();
        item.setId(null);
        item.setName("Test");
    }

    @Test
    void sanityCheck() {
        assertThat(incomeExpenseRepository).isNotNull();
        assertThat(service).isNotNull();
    }

    @Test
    void canFindItemById() {
        given(incomeExpenseRepository.findById(1L)).willReturn(Optional.of(item));
        service.findById(1L);
        verify(incomeExpenseRepository).findById(1L);
        assertThat(service.findById(1L)).isEqualTo(item);
    }

    @Test
    void throwsExceptionIfItemNotFound() {
        assertThrows(NotFoundException.class, () -> service.findById(1L));
    }

    @Test
    void canSaveItem() {
        IncomeExpenseItems expectedItem = new IncomeExpenseItems();
        expectedItem.setId(1L);
        expectedItem.setName("test");
        given(incomeExpenseRepository.save(any(IncomeExpenseItems.class))).willReturn(expectedItem);
        IncomeExpenseItems savedItem = service.save(item);
        verify(incomeExpenseRepository).save(item);
        assertThat(savedItem).isEqualTo(expectedItem);
        assertThat(savedItem.getId()).isEqualTo(expectedItem.getId());
    }

    @Test
    void validatesItemName() {
        item.setName(null);
        Set<ConstraintViolation<IncomeExpenseItems>> violations = validator.validate(item);
        assertThat(violations.size()).isEqualTo(1);
    }
}
