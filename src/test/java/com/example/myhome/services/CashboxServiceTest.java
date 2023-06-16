//package com.example.myhome.services;
//
//import com.example.myhome.dto.CashBoxDTO;
//import com.example.myhome.mapper.CashboxDTOMapper;
//import com.example.myhome.model.*;
//import com.example.myhome.model.filter.FilterForm;
//import com.example.myhome.repository.CashBoxRepository;
//import com.example.myhome.service.CashBoxService;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//
//import java.time.LocalDate;
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.when;
//
//@SpringBootTest
//public class CashboxServiceTest {
//
//    @Autowired
//    private CashBoxService cashBoxService;
//
//    @MockBean private CashBoxRepository cashBoxRepository;
//
//    static CashBox testBox;
//    static CashBoxDTO testDTO;
//
//    static List<CashBox> boxList;
//    static Page<CashBox> boxPage;
//
//    static List<CashBoxDTO> dtoList;
//    static Page<CashBoxDTO> dtoPage;
//
//    static CashboxDTOMapper mapper;
//
//    @BeforeAll
//    static void setupObjects() {
//        testBox = new CashBox();
//        testBox.setId(1L);
//        testBox.setOwner(new Owner());
//        testBox.setNumber(100L);
//        testBox.setAmount(100.0);
//        testBox.setDate(LocalDate.now());
//        testBox.setApartmentAccount(new ApartmentAccount());
//        testBox.setIncomeExpenseType(IncomeExpenseType.INCOME);
//        testBox.setIncomeExpenseItems(new IncomeExpenseItems());
//        testBox.setCompleted(true);
//        testBox.setManager(new Admin());
//        testBox.setDescription("test");
//
//        mapper = new CashboxDTOMapper();
//
//        testDTO = mapper.fromCashboxToDTO(testBox);
//
//        boxList = List.of(testBox, testBox);
//        dtoList = List.of(testDTO, testDTO);
//
//        boxPage = new PageImpl<>(boxList, PageRequest.of(1,1), 1);
//        dtoPage = new PageImpl<>(dtoList, PageRequest.of(1,1),1);
//    }
//
//    @BeforeEach
//    void setupMocks() {
//        when(cashBoxRepository.findById(anyLong())).thenReturn(Optional.of(testBox));
//        when(cashBoxRepository.findAll(any(Pageable.class))).thenReturn(boxPage);
//        when(cashBoxRepository.save(any(CashBox.class))).thenReturn(testBox);
//        when(cashBoxRepository.findAll()).thenReturn(boxList);
//
//        when(cashBoxRepository.findByFilters(anyLong(), any(LocalDate.class), any(LocalDate.class),
//                any(Boolean.class),anyString(), anyLong(), anyLong(), any(IncomeExpenseType.class),
//                any(Pageable.class))).thenReturn(boxPage);
//    }
//
//    @Test
//    void contextLoads(){
//
//    }
//
//    @Test
//    void findByIdTest() {
//        assertThat(cashBoxService.findById(testBox.getId())).isEqualTo(testBox);
//    }
//
//    @Test
//    void findAllTest() {
//        assertThat(cashBoxService.findAll()).isEqualTo(boxList);
//    }
//
//    @Test
//    void findAllPageTest() {
//        assertThat(cashBoxService.findAll(PageRequest.of(1,1))).isEqualTo(boxPage);
//    }
//
//    @Test
//    void saveTest() {
//        assertThat(cashBoxService.save(testBox)).isEqualTo(testBox);
//    }
//
//    @Test
//    void findAllBySpecificationTest() {
//        FilterForm filters = new FilterForm();
//        filters.setDate("2022-11-11 - 2022-13-11");
//        assertThat(cashBoxService.findAllBySpecification2(filters, 1,1)).isInstanceOf(Page.class);
//        assertThat(cashBoxService.findAllBySpecification2(filters, 1,1).getContent()).hasSize(3);
//        assertThat(cashBoxService.findAllBySpecification2(filters, 1,1).getContent()).hasAtLeastOneElementOfType(CashBoxDTO.class);
//        assertThat(cashBoxService.findAllBySpecification2(filters, 1,1).getContent().get(0)).isEqualTo(testDTO);
//    }
//
//    @Test
//    void deleteByIdTest() {
//        cashBoxService.deleteById(testBox.getId());
//    }
//
//    @Test
//    void getMaxIdTest() {
//        assertThat(cashBoxService.getMaxId()).isEqualTo(testBox.getId());
//    }
//
//    @Test
//    void calculateBalanceTest() {
//        assertThat(cashBoxService.calculateBalance()).isEqualTo(100);
//    }
//
//    @Test
//    void getListSumIncomeByMonthTest() {
//        assertThat(cashBoxService.getListSumIncomeByMonth()).hasSize(12);
//        assertThat(cashBoxService.getListSumIncomeByMonth()).element(0).isEqualTo(100.0);
//    }
//
//    @Test
//    void getListSumExpenseByMonthTest() {
//        assertThat(cashBoxService.getListSumExpenceByMonth()).hasSize(12);
//        assertThat(cashBoxService.getListSumExpenceByMonth()).element(0).isEqualTo(100.0);
//    }
//
//    @Test
//    void getListOfMonthNameTest() {
//        assertThat(cashBoxService.getListOfMonthName()).hasSize(12);
//    }
//
//    @Test
//    void getIncomeExpenseTypeFromStringTest() {
//        assertThat(cashBoxService.getIncomeExpenseTypeFromString("income")).isEqualTo(IncomeExpenseType.INCOME);
//        assertThat(cashBoxService.getIncomeExpenseTypeFromString("expense")).isEqualTo(IncomeExpenseType.EXPENSE);
//    }
//
//    @Test
//    void getCompleteFromStringTest() {
//        assertThat(cashBoxService.getIsCompleteFromString("completed")).isTrue();
//        assertThat(cashBoxService.getIsCompleteFromString("notComplete")).isFalse();
//    }
//
//    @Test
//    void findAllByApartmentAccountIdTest() {
//        assertThat(cashBoxService.findAllByApartmentAccountId(1L)).isEqualTo(boxList);
//    }
//
//    @Test
//    void getSumAmountTest() {
//        assertThat(cashBoxService.getSumAmount()).isEqualTo(100.0);
//    }
//
//}
