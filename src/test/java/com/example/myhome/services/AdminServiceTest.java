package com.example.myhome.services;

import com.example.myhome.dto.AdminDTO;
import com.example.myhome.mapper.AdminDTOMapper;
import com.example.myhome.model.Admin;
import com.example.myhome.model.UserRole;
import com.example.myhome.repository.AdminRepository;
import com.example.myhome.service.AdminService;
import com.example.myhome.validator.AdminValidator;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@SpringBootTest
class AdminServiceTest {

    @MockBean
    AdminRepository repository;

    @Autowired
    AdminService service;

    @Autowired
    AdminValidator validator;

    @Autowired
    AdminDTOMapper mapper;

    @MockBean
    EntityManager em;

    @Mock
    CriteriaQuery<Admin> cq;

    @Mock
    CriteriaBuilder cb;

    @Mock
    Root<Admin> root;

    @Test
    void canGetAllAdminsTest() {
        List<Admin> list = List.of(new Admin(), new Admin(), new Admin());
        given(repository.findAll()).willReturn(list);
        List<Admin> test = service.findAll();
        verify(repository).findAll();
        assertThat(test).isEqualTo(list);
    }

    @Test
    void canFindAdminByIdTest() {
        Admin expected = new Admin();
        expected.setId(1L);
        expected.setFull_name("test test");

        given(repository.findById(1L)).willReturn(Optional.of(expected));

        Admin test = service.findAdminById(1L);

        verify(repository).findById(1L);

        assertThat(test).isEqualTo(expected);

        Admin test2 = service.findAdminById(2L);

        verify(repository).findById(2L);

        assertThat(test2).isNull();
    }

    @Test
    void canFindAdminByLoginTest() {
        Admin expected = new Admin();
        expected.setId(1L);
        expected.setFull_name("test test");
        expected.setEmail("test@gmail.com");

        given(repository.findByEmail("test@gmail.com")).willReturn(Optional.of(expected));

        Admin test = service.findAdminByLogin("test@gmail.com");

        verify(repository).findByEmail("test@gmail.com");

        assertThat(test).isEqualTo(expected);
    }

    @Test
    void canLoadAdminOptional() {
        Admin expected = new Admin();
        expected.setId(1L);
        expected.setFull_name("test test");

        given(repository.findById(1L)).willReturn(Optional.of(expected));

        Admin test = service.findAdminById(1L);

        verify(repository).findById(1L);

        assertThat(service.findAdminById(1L)).isInstanceOf(Optional.class);
    }

    @Test
    void returnNullOnNoAdminFoundById() {
        assertThat(service.findAdminById(1L)).isNull();
    }

    @Test
    void returnNullOnNoAdminFoundByLogin() {assertThat(service.findAdminByLogin("test")).isNull();}

    @Test
    void returnNullOnNoAdminDTOFoundById() {assertThat(service.findAdminDTOById(1L)).isNull();}

    @Test
    void transformIntoDTOTest() {
        UserRole role = new UserRole();
        role.setId(1L);
        role.setName("TestRole");
        Admin admin = new Admin();
        admin.setId(1L);
        admin.setFirst_name("test");
        admin.setLast_name("test");
        admin.setPhone_number("11111");
        admin.setEmail("test@gmail.com");
        admin.setPassword("testPassword");
        admin.setRole(role);

        AdminDTO dto = mapper.fromAdminToDTO(admin);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getFullName()).isEqualTo("test test");
        assertThat(dto.getPhone_number()).isEqualTo("11111");
        assertThat(dto.getEmail()).isEqualTo("test@gmail.com");
        assertThat(dto.getRole()).isEqualTo("TestRole");
        assertThat(dto.getUserRoleID()).isEqualTo(1L);
    }

    @Test
    void dtoDoesntShowPassword() {
        Admin admin = new Admin();
        admin.setPassword("TEST");

        AdminDTO dto = mapper.fromAdminToDTO(admin);

        assertThat(dto.getPassword()).isNull();
    }

    @Test
    void transformDTOIntoAdminTest() {
        AdminDTO dto = new AdminDTO();
        dto.setId(1L);
        dto.setFirst_name("test");
        dto.setLast_name("test");
        dto.setEmail("test@gmail.com");
        dto.setRole("TestRole");
        dto.setUserRoleID(1L);
        dto.setPhone_number("11111");

        Admin admin = mapper.fromDTOToAdmin(dto);

        assertThat(admin.getId()).isEqualTo(1L);
        assertThat(admin.getFirst_name()).isEqualTo("test");
        assertThat(admin.getLast_name()).isEqualTo("test");
        assertThat(admin.getEmail()).isEqualTo("test@gmail.com");
        assertThat(admin.getPhone_number()).isEqualTo("11111");
    }

    @Test
    void createSpecTest() {
    }

    @Test
    void findMastersByType() {
    }

    @Test
    void testFindAdminById() {
    }

    @Test
    void findAllMasters() {
    }

    @Test
    void findAllManagers() {
    }

    @Test
    void countAllMasters() {
    }

    @Test
    void countAllManagers() {
    }

    @Test
    void getAllRoles() {
    }

    @Test
    void buildSpecFromFilters() {
    }

    @Test
    void testFindMastersByType() {
    }
}