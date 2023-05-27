package com.example.myhome.service;

import com.example.myhome.dto.AdminDTO;
import com.example.myhome.model.Admin;
import com.example.myhome.model.UserRole;
import com.example.myhome.model.filter.FilterForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface AdminService extends UserDetailsService {

    Admin findAdminById(Long admin_id);
    Admin findAdminByLogin(String login);

    AdminDTO findAdminDTOById(Long admin_id);

    List<Admin> findAll();
    List<AdminDTO> findAllDTO();
    Page<Admin> findAll(Pageable pageable);
    Page<AdminDTO> findAllDTO(Pageable pageable);

    Page<AdminDTO> findAllByFiltersAndPage(FilterForm filters, Pageable pageable) throws IllegalAccessException;
    Page<AdminDTO> findAllBySpecification(FilterForm filters, Integer page, Integer size) throws IllegalAccessException;

    Admin saveAdmin(Admin admin);
    Admin saveAdmin(AdminDTO dto);

    void deleteAdminById(Long admin_id);

    List<AdminDTO> findAllMasters(String search, Integer page);
    List<AdminDTO> findAllManagers(String search, Integer page);

    List<AdminDTO> findAllMasters();
    List<AdminDTO> findAllManagers();

    Long countAllMasters();
    Long countAllManagers();

    List<UserRole> getAllRoles();
    List<UserRole> getMasterRoles();
    List<UserRole> getManagerRoles();

    Specification<Admin> buildSpecFromFilters(FilterForm filters) throws IllegalAccessException;

    List<AdminDTO> findMastersByType(Long typeID);
}
