package com.example.myhome.services;

import com.example.myhome.model.Admin;
import com.example.myhome.model.PageRoleDisplay;
import com.example.myhome.model.UserRole;
import com.example.myhome.repository.UserRoleRepository;
import com.example.myhome.service.UserRoleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@SpringBootTest
class UserRoleServiceTest {

    @MockBean private UserRoleRepository repository;
    @Autowired private UserRoleService service;

    @Test
    void sanityCheck() {
        assertThat(repository).isNotNull();
        assertThat(service).isNotNull();
    }

    @Test
    void getRoleTest() {
        UserRole expectedRole = new UserRole();
        expectedRole.setId(1L);
        expectedRole.setName("test");
        expectedRole.setMaster(true);
        expectedRole.setPermissions(new HashSet<>());

        given(repository.findById(anyLong())).willReturn(Optional.of(expectedRole));

        UserRole testRole = service.getRole(1L);

        verify(repository).findById(any());

        assertThat(testRole).isEqualTo(expectedRole);
    }

    @Test
    void saveRoleTest() {
        UserRole expectedRole = new UserRole();
        expectedRole.setId(1L);
        expectedRole.setName("test");
        expectedRole.setMaster(true);
        expectedRole.setPermissions(new HashSet<>());

        given(repository.save(any(UserRole.class))).willReturn(expectedRole);

        UserRole testRole = new UserRole();
        testRole.setId(null);

        UserRole savedRole = service.saveRole(testRole);

        verify(repository).save(testRole);

        assertThat(savedRole).isEqualTo(expectedRole);
    }

    @Test
    void updateRolesTest() {
        UserRole testRole = new UserRole();
        testRole.setId(1L);
        testRole.setName("test");
        testRole.setMaster(false);
        testRole.setPermissions(new HashSet<>());

        Admin admin = new Admin();
        admin.setEmail("test");
        admin.setPassword("test");
        admin.setRole(testRole);

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(admin, admin.getPassword(), admin.getAuthorities()));

        given(repository.findByName(anyString())).willReturn(Optional.of(testRole));
        given(repository.findAll()).willReturn(List.of(testRole));

        PageRoleDisplay testPermission1 = new PageRoleDisplay();
        testPermission1.setCode("testPermission1");
        testPermission1.setRole_accountant(true);
        testPermission1.setRole_director(true);
        testPermission1.setRole_admin(true);
        testPermission1.setRole_manager(true);
        testPermission1.setRole_electrician(true);
        testPermission1.setRole_plumber(true);

        List<PageRoleDisplay> list = List.of(testPermission1);

        service.updateRoles(list);

        assertThat(testRole.getPermissions().size()).isEqualTo(2);
        assertThat(testRole.getPermissions().contains("testPermission1.read")).isTrue();
        assertThat(testRole.getPermissions().contains("testPermission1.write")).isTrue();

        assertThat(admin.getRole().getPermissions().size()).isEqualTo(2);
        assertThat(SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(new SimpleGrantedAuthority("testPermission1.read"))).isTrue();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(new SimpleGrantedAuthority("testPermission1.write"))).isTrue();
    }
}