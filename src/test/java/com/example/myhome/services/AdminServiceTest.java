package com.example.myhome.services;

import com.example.myhome.dto.AdminDTO;
import com.example.myhome.exception.NotFoundException;
import com.example.myhome.mapper.AdminDTOMapper;
import com.example.myhome.model.Admin;
import com.example.myhome.model.UserRole;
import com.example.myhome.model.filter.FilterForm;
import com.example.myhome.repository.AdminRepository;
import com.example.myhome.repository.UserRoleRepository;
import com.example.myhome.service.AdminService;
import com.example.myhome.specification.AdminSpecifications;
import com.example.myhome.validator.AdminValidator;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@SpringBootTest
class AdminServiceTest {

    @MockBean
    AdminRepository repository;

    @MockBean
    UserRoleRepository userRoleRepository;

    @Autowired
    AdminService service;

    @Autowired
    AdminValidator validator;

    @Autowired
    AdminDTOMapper mapper;

    Admin testAdmin;

    @BeforeEach
    void createAdmin() {
        testAdmin = new Admin();
        testAdmin.setId(1L);
        testAdmin.setPassword("test");
        testAdmin.setActive(true);
        testAdmin.setEmail("test");
        testAdmin.setPhone_number("test");
        testAdmin.setFirst_name("test");
        testAdmin.setLast_name("test");
        UserRole role = new UserRole();
        role.setId(1L);
        role.setName("test");
        role.setPermissions(new HashSet<>());
        testAdmin.setRole(role);
        testAdmin.setDateOfRegistry(LocalDate.now());
        testAdmin.setBuildings(new ArrayList<>());
        testAdmin.setCashBoxList(new ArrayList<>());
        testAdmin.setMessageList(new ArrayList<>());
        testAdmin.setCashBoxListManager(new ArrayList<>());
    }

    @Test
    void canGetAllAdminsTest() {
        List<Admin> list = List.of(new Admin(), new Admin(), new Admin());
        given(repository.findAll()).willReturn(list);
        List<Admin> test = service.findAll();
        verify(repository).findAll();
        assertThat(test).isEqualTo(list);
    }

    @Test
    void canFindAllAdminsTest() {
        List<Admin> list = List.of(new Admin(), new Admin(), new Admin());
        Page<Admin> expected = new PageImpl<>(list, PageRequest.of(1,1), 1);
        given(repository.findAll(any(Pageable.class))).willReturn(expected);
        Page<Admin> test = service.findAll(PageRequest.of(1,1));
        verify(repository).findAll(PageRequest.of(1,1));
        assertThat(test).isEqualTo(expected);
    }

    @Test
    void canGetAllAdminsDTOTest() {
        Admin admin = testAdmin;
        List<Admin> list = List.of(admin,admin,admin);
        given(repository.findAll()).willReturn(list);
        List<AdminDTO> test = service.findAllDTO();
        verify(repository).findAll();
        assertThat(test.size()).isEqualTo(3);
    }

    @Test
    void canFindAllAdminsDTOTest() {
        Admin admin = testAdmin;
        AdminDTO dto = mapper.fromAdminToDTO(admin);
        List<Admin> list = List.of(admin,admin,admin);
        Page<Admin> expected = new PageImpl<>(list, PageRequest.of(1,1), 1);

        List<AdminDTO> dtoList = List.of(dto,dto,dto);
        Page<AdminDTO> expectedDTO = new PageImpl<>(dtoList,PageRequest.of(1,1), 1);
        given(repository.findAll(any(Pageable.class))).willReturn(expected);
        Page<AdminDTO> test = service.findAllDTO(PageRequest.of(1,1));
        verify(repository).findAll(PageRequest.of(1,1));
        assertThat(test).isEqualTo(expectedDTO);
    }

    @Test
    void canFindAllAdminsByFiltersAndPageTest() throws IllegalAccessException {
        Admin admin = testAdmin;
        AdminDTO dto = mapper.fromAdminToDTO(admin);
        List<Admin> list = List.of(admin,admin,admin);
        Page<Admin> expected = new PageImpl<>(list, PageRequest.of(1,1), 1);

        List<AdminDTO> dtoList = List.of(dto,dto,dto);
        Page<AdminDTO> expectedDTO = new PageImpl<>(dtoList,PageRequest.of(1,1), 1);

        given(repository.findAll(any(Specification.class),any(Pageable.class))).willReturn(expected);
        given(repository.findAll(nullable(Specification.class), any(Pageable.class))).willReturn(expected);
        Page<AdminDTO> test = service.findAllByFiltersAndPage(new FilterForm(), PageRequest.of(1,1));
        assertThat(test).isEqualTo(expectedDTO);
    }

    @Test
    void canSaveAdminTest() {
        Admin admin = testAdmin;
        when(repository.save(any(Admin.class))).thenReturn(admin);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(admin, admin.getPassword(), admin.getAuthorities()));
        assertThat(service.saveAdmin(admin)).isEqualTo(admin);
    }

    @Test
    void canReturnNullOnSaveErrorTest() {
        assertThat(service.saveAdmin(new Admin())).isNull();
    }

    @Test
    void canSaveAdminFromDTOTest() {
        Admin admin = testAdmin;
        AdminDTO dto = mapper.fromAdminToDTO(admin);
        dto.setPassword("Test");
        when(userRoleRepository.getReferenceById(anyLong())).thenReturn(new UserRole());
        assertThat(service.saveAdmin(dto)).isNull();
    }

    @Test
    void canDeleteAdminTest() {
        service.deleteAdminById(1L);
    }

    @Test
    void canGiveErrorOnDeleteAdminTest() {
        doThrow(new NotFoundException()).when(repository).deleteById(anyLong());
        service.deleteAdminById(1L);
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

//        assertThat(service.findAdminById(1L)).isInstanceOf(Optional.class);
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
        Admin admin = testAdmin;

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
        dto.setPassword("test");

        Admin admin = mapper.fromDTOToAdmin(dto);

        assertThat(admin.getId()).isEqualTo(1L);
        assertThat(admin.getFirst_name()).isEqualTo("test");
        assertThat(admin.getLast_name()).isEqualTo("test");
        assertThat(admin.getEmail()).isEqualTo("test@gmail.com");
        assertThat(admin.getPhone_number()).isEqualTo("11111");
    }

    @Test
    void createEmptySpecTest() throws IllegalAccessException {
        FilterForm testForm = new FilterForm();
        assertThat(service.buildSpecFromFilters(testForm)).isNull();
    }

    @Test
    void createSpecTest() throws IllegalAccessException {
        FilterForm testForm = new FilterForm();
        testForm.setId(1L);
        testForm.setName("test");
        testForm.setPhone("test");
        testForm.setEmail("test");
        testForm.setActive(true);
        UserRole role = new UserRole();
        role.setId(1L);
        role.setName("TestRole");

        when(userRoleRepository.findByName(anyString())).thenReturn(Optional.of(role));

        Specification<Admin> expected = Specification.where(AdminSpecifications.hasNameLike(testForm.getName())
                                                        .and(AdminSpecifications.hasRole(role.getName()))
                                                        .and(AdminSpecifications.hasPhoneLike(testForm.getPhone()))
                                                        .and(AdminSpecifications.hasEmailLike(testForm.getEmail()))
                                                        .and(AdminSpecifications.isActive(testForm.getActive())));

        assertThat(service.buildSpecFromFilters(testForm)).isInstanceOf(Specification.class);
    }

    @Test
    void canFindMastersByTypeTest() {
        UserRole testRole = new UserRole();
        testRole.setId(1L);
        testRole.setName("test");
        Admin testAdmin = new Admin();
        testAdmin.setId(1L);
        testAdmin.setFull_name("test test");
        testAdmin.setEmail("test@gmail.com");
        testAdmin.setRole(testRole);
        testAdmin.setPhone_number("test");
        AdminDTO expectedDTO = new AdminDTO();
        expectedDTO.setId(testAdmin.getId());
        expectedDTO.setFirst_name("test");
        expectedDTO.setLast_name("test");
        expectedDTO.setEmail("test@gmail.com");
        expectedDTO.setRole(testRole.getName());
        expectedDTO.setUserRoleID(testRole.getId());
        expectedDTO.setPassword(testAdmin.getPhone_number());

        when(userRoleRepository.getReferenceById(anyLong())).thenReturn(testRole);
        when(repository.findAll(any(Specification.class))).thenReturn(List.of(testAdmin));

        List<AdminDTO> list = service.findMastersByType(1L);

//        verify(repository).findAll();

//        assertThat(list).isEqualTo(List.of(expectedDTO));
    }

    @Test
    void canFindAllMastersTest() {
        UserRole testRole = new UserRole();
        testRole.setId(1L);
        testRole.setName("test");
        Admin testAdmin = new Admin();
        testAdmin.setId(1L);
        testAdmin.setFull_name("test test");
        testAdmin.setEmail("test@gmail.com");
        testAdmin.setRole(testRole);
        testAdmin.setPhone_number("test");
        AdminDTO expectedDTO = new AdminDTO();
        expectedDTO.setId(testAdmin.getId());
        expectedDTO.setFirst_name("test");
        expectedDTO.setLast_name("test");
        expectedDTO.setEmail("test@gmail.com");
        expectedDTO.setRole(testRole.getName());
        expectedDTO.setUserRoleID(testRole.getId());
        expectedDTO.setPassword(testAdmin.getPhone_number());

        Page<Admin> expectedPage = new PageImpl<>(List.of(testAdmin), PageRequest.of(1,1),1);

        when(userRoleRepository.getReferenceById(anyLong())).thenReturn(testRole);
        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);
        when(repository.findAll(any(Specification.class))).thenReturn(List.of(testAdmin));

        List<AdminDTO> list = service.findAllMasters();
        List<AdminDTO> list2 = service.findAllMasters("test", 5);


//        verify(repository).findAll();

//        assertThat(list).isEqualTo(List.of(expectedDTO));
//        assertThat(list2).isEqualTo(List.of(expectedDTO));
    }

    @Test
    void canFindAllManagersTest() {
        UserRole testRole = new UserRole();
        testRole.setId(1L);
        testRole.setName("test");
        Admin testAdmin = new Admin();
        testAdmin.setId(1L);
        testAdmin.setFull_name("test test");
        testAdmin.setEmail("test@gmail.com");
        testAdmin.setRole(testRole);
        testAdmin.setPhone_number("test");
        AdminDTO expectedDTO = new AdminDTO();
        expectedDTO.setId(testAdmin.getId());
        expectedDTO.setFirst_name("test");
        expectedDTO.setLast_name("test");
        expectedDTO.setEmail("test@gmail.com");
        expectedDTO.setRole(testRole.getName());
        expectedDTO.setUserRoleID(testRole.getId());
        expectedDTO.setPassword(testAdmin.getPhone_number());

        Page<Admin> expectedPage = new PageImpl<>(List.of(testAdmin), PageRequest.of(1,1),1);

        when(userRoleRepository.getReferenceById(anyLong())).thenReturn(testRole);
        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);
        when(repository.findAll(any(Specification.class))).thenReturn(List.of(testAdmin));

        List<AdminDTO> list = service.findAllManagers();
        List<AdminDTO> list2 = service.findAllManagers("test", 5);

//        verify(repository).findAll();

//        assertThat(list).isEqualTo(List.of(expectedDTO));
//        assertThat(list2).isEqualTo(List.of(expectedDTO));

    }

    @Test
    void canLoadUserByUsernameTest() {
        Admin admin = new Admin();
        admin.setId(1L);
        admin.setEmail("test");
        when(repository.findByEmail(anyString())).thenReturn(Optional.of(admin));
        assertThat(service.loadUserByUsername(admin.getEmail())).isEqualTo(admin);
    }

    @Test
    void canFindAllAdminsBySpecificationTest() throws IllegalAccessException {
        Admin admin = testAdmin;
        AdminDTO dto = mapper.fromAdminToDTO(admin);
        List<Admin> list = List.of(admin,admin,admin);
        Page<Admin> expected = new PageImpl<>(list, PageRequest.of(1,1), 1);

        List<AdminDTO> dtoList = List.of(dto,dto,dto);
        Page<AdminDTO> expectedDTO = new PageImpl<>(dtoList,PageRequest.of(1,1), 1);

        FilterForm form = new FilterForm();
        form.setName("test");
        form.setRole("test");
        form.setPhone("test");
        form.setEmail("test");
        form.setStatus("test");

        given(repository.findByFilters(anyString(),anyString(), anyString(), anyString(), anyString(), any(Pageable.class))).willReturn(expected);
        Page<AdminDTO> test = service.findAllBySpecification(form, 1,1);
//        assertThat(test).isEqualTo(expectedDTO);
    }

    @Test
    void canCountAllMastersTest() {
        when(repository.count(any(Specification.class))).thenReturn(100L);
        assertThat(service.countAllMasters()).isEqualTo(100L);
    }

    @Test
    void canCountAllManagersTest() {
        when(repository.count(any(Specification.class))).thenReturn(100L);
        assertThat(service.countAllManagers()).isEqualTo(100L);
    }

    @Test
    void getAllRoles() {
        when(userRoleRepository.findAll()).thenReturn(List.of(new UserRole()));
        assertThat(service.getAllRoles().size()).isEqualTo(1);
    }

    @Test
    void canGetMasterRolesTest(){
        when(userRoleRepository.findAllMasterRoles()).thenReturn(List.of(new UserRole()));
        assertThat(service.getMasterRoles().size()).isEqualTo(1);
    }

    @Test
    void canGetManagerRolesTest(){
        when(userRoleRepository.findAllManagerRoles()).thenReturn(List.of(new UserRole()));
        assertThat(service.getManagerRoles().size()).isEqualTo(1);
    }
}