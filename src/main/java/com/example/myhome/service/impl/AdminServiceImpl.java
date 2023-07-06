package com.example.myhome.service.impl;

import com.example.myhome.dto.AdminDTO;
import com.example.myhome.mapper.AdminDTOMapper;
import com.example.myhome.model.Admin;

import com.example.myhome.model.filter.FilterForm;
import com.example.myhome.repository.AdminRepository;

import com.example.myhome.repository.UserRoleRepository;
import com.example.myhome.service.AdminService;
import com.example.myhome.specification.AdminSpecifications;
import com.example.myhome.model.UserRole;
import lombok.RequiredArgsConstructor;

import lombok.extern.java.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRoleRepository userRoleRepository;
    private final AdminDTOMapper mapper;

    @Override
    public Admin findAdminById (Long id) {
        log.info("Searching for admin with ID: " + id);
        Optional<Admin> opt = adminRepository.findById(id);
        if(opt.isPresent()) {
            log.info("Found admin! ");
            log.info(opt.get().toString());
            return opt.get();
        } else {
            log.severe("Admin not found!");
            return null;
        }
    }

    @Override
    public AdminDTO findAdminDTOById(Long id) {
        Admin admin = findAdminById(id);
        return mapper.fromAdminToDTO(admin);
    }

    @Override
    public Admin findAdminByLogin(String login) {
        log.info("Searching for admin with login: " + login);
        Optional<Admin> opt = adminRepository.findByEmail(login);
        if(opt.isPresent()) {
            log.info("Found admin!");
            log.info(opt.get().toString());
            return opt.get();
        } else {
            log.severe("Admin not found!");
            return null;
        }
    }

    @Override
    public List<Admin> findAll() {
        log.info("Getting all admins...");
        List<Admin> list = adminRepository.findAll();
        log.info("Found " + list.size() + " elements");
        return list;
    }

    @Override
    public Page<Admin> findAll(Pageable pageable) {
        log.info("Getting all admins...");
        Page<Admin> initialPage = adminRepository.findAll(pageable);
        log.info("Found " + initialPage.getContent().size() + " elements (page " + pageable.getPageNumber()+1 + "/" + initialPage.getTotalPages() + ")");
        return initialPage;
    }

    @Override
    public List<AdminDTO> findAllDTO(){
        log.info("Getting all admins...");
        List<Admin> initialList = adminRepository.findAll();
        log.info("Found " + initialList.size() + " elements");
        initialList.forEach(System.out::println);
        return initialList.stream().map(mapper::fromAdminToDTO).collect(Collectors.toList());
    }

    @Override
    public Page<AdminDTO> findAllDTO(Pageable pageable) {
        log.info("Getting all admins...");
        Page<Admin> initialPage = adminRepository.findAll(pageable);
        log.info("Found " + initialPage.getContent().size() + " elements (page " + (pageable.getPageNumber()+1) + "/" + initialPage.getTotalPages() + ")");
        List<AdminDTO> listDTO = initialPage.getContent().stream().map(mapper::fromAdminToDTO).collect(Collectors.toList());
        log.info("Turned admins from DB into DTOs");
        return new PageImpl<>(listDTO, pageable, initialPage.getTotalPages());
    }

    @Override
    public Page<AdminDTO> findAllByFiltersAndPage(FilterForm filters, Pageable pageable) throws IllegalAccessException {
        log.info("Getting admins by specified filters and page...");
        Specification<Admin> spec = buildSpecFromFilters(filters);
        if(spec != null) log.info(spec.toString());
        Page<Admin> initialPage = adminRepository.findAll(spec, pageable);
        log.info("Found " + initialPage.getContent().size() + " elements (page " + (pageable.getPageNumber()+1) + "/" + initialPage.getTotalPages() + ")");
        List<AdminDTO> listDTO = initialPage.getContent().stream().map(mapper::fromAdminToDTO).collect(Collectors.toList());
        log.info("Turned admins from DB into DTOs");
        return new PageImpl<>(listDTO, pageable, initialPage.getTotalPages());
    }

    @Override
    public Long countAllMasters() {

        Specification<Admin> spec = Specification.not(
                                                        AdminSpecifications.hasRole("Director")
                                                    .or(AdminSpecifications.hasRole("Manager"))
                                                    .or(AdminSpecifications.hasRole("Accountant"))
        );

        return adminRepository.count(spec);
    }

    @Override
    public Long countAllManagers() {
        Specification<Admin> spec = Specification.where(
                AdminSpecifications.hasRole("Director")
                        .or(AdminSpecifications.hasRole("Manager"))
                        .or(AdminSpecifications.hasRole("Accountant")));

        return adminRepository.count(spec);
    }

    @Override
    public List<UserRole> getAllRoles() {
        return userRoleRepository.findAll();
    }

    public List<UserRole> getMasterRoles() {
        return userRoleRepository.findAllMasterRoles();
    }

    public List<UserRole> getManagerRoles() {
        return userRoleRepository.findAllManagerRoles();
    }

    @Override
    @Transactional
    public Admin saveAdmin(Admin admin) {
        if(admin.getId() != null) {
            Optional<Admin> opt = adminRepository.findById(admin.getId());
            if(opt.isPresent()) {
                Admin originalAdmin = opt.get();
                if(admin.getPassword() == null) admin.setPassword(originalAdmin.getPassword());
            }
        }
        log.info("Trying to save admin...");
        log.info(admin.toString());
        if(admin.getDateOfRegistry() == null) admin.setDateOfRegistry(LocalDate.now());
        log.info("Encoded admin password for saving");
        try {
            Admin savedAdmin = adminRepository.save(admin);
            log.info("Save successful!");
            log.info(savedAdmin.toString());

            // Обновление роли юзера в контексте
            Admin loggedInAdmin = (Admin) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            log.info("LOGGED IN ADMIN DATA: ");
            log.info(loggedInAdmin.toString());
            if(Objects.equals(loggedInAdmin.getId(), savedAdmin.getId())) {
                log.info("Updated logged in user, changing permissions in security context...");
                SecurityContextHolder.getContext().setAuthentication(null);
                SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(savedAdmin, savedAdmin.getPassword(), savedAdmin.getAuthorities()));
            }

            return savedAdmin;
        } catch (Exception e) {
            log.severe("Something went wrong during saving!");
            log.severe(e.getMessage());
            return null;
        }
    }

    @Override
    public Admin saveAdmin(AdminDTO dto) {
        Admin admin = mapper.fromDTOToAdmin(dto);
//        if(admin.getPassword() == null) admin.setPassword(adminRepository.findById(admin.getId()).orElseThrow().getPassword());
        admin.setRole(userRoleRepository.getReferenceById(dto.getUserRoleID()));
        return saveAdmin(admin);
    }

    @Override
    public void deleteAdminById(Long id) {
        log.info("Trying to delete admin with ID: " + id);
        try {
            adminRepository.deleteById(id);
            log.info("Delete successful");
        } catch (Exception e) {
            log.severe("Something went wrong during deletion!");
            log.severe(e.getMessage());
        }
    }

    @Override
    public List<AdminDTO> findAllMasters(String search, Integer page) {
        Pageable pageable = PageRequest.of(page, 5);

        Specification<Admin> spec = Specification.not(
                AdminSpecifications.hasRole("Director")
            .or(AdminSpecifications.hasRole("Manager"))
            .or(AdminSpecifications.hasRole("Accountant"))
            .or(AdminSpecifications.hasRole("Admin")))
            .and(AdminSpecifications.hasNameLike(search));

        return adminRepository.findAll(spec, pageable)
                .stream()
                .map(mapper::fromAdminToDTO)
                .collect(Collectors.toList());
    }

    public List<AdminDTO> findAllMasters() {
        Specification<Admin> spec = Specification.not(AdminSpecifications.hasRole("Director")
                                                    .or(AdminSpecifications.hasRole("Manager"))
                                                    .or(AdminSpecifications.hasRole("Accountant"))
                                                    .or(AdminSpecifications.hasRole("Admin")));

        return adminRepository.findAll(spec).stream().map(mapper::fromAdminToDTO).collect(Collectors.toList());
    }

    @Override
    public List<AdminDTO> findAllManagers(String search, Integer page) {
        Pageable pageable = PageRequest.of(page, 5);

        Specification<Admin> spec = Specification.where(
                AdminSpecifications.hasRole("Director")
            .or(AdminSpecifications.hasRole("Manager"))
            .or(AdminSpecifications.hasRole("Accountant"))
            .or(AdminSpecifications.hasRole("Admin")))
            .and(AdminSpecifications.hasNameLike(search));

        return adminRepository.findAll(spec, pageable)
                .stream()
                .map(mapper::fromAdminToDTO)
                .collect(Collectors.toList());
    }

    public List<AdminDTO> findAllManagers() {
        Specification<Admin> spec = Specification.where(
                        AdminSpecifications.hasRole("Director")
                        .or(AdminSpecifications.hasRole("Manager"))
                        .or(AdminSpecifications.hasRole("Accountant"))
                        .or(AdminSpecifications.hasRole("Admin")));

        return adminRepository.findAll(spec).stream().map(mapper::fromAdminToDTO).collect(Collectors.toList());
    }

//    @Override
//    public List<Admin> getAdminsByRole(UserRole role) { return adminRepository.getAdminsByRole(role);}

    @Override
    public Specification<Admin> buildSpecFromFilters(FilterForm filters) throws IllegalAccessException {

        if(!filters.filtersPresent()) return null;

        log.info("Building specification from filters: " + filters);

        String name = filters.getName();
        UserRole role = (filters.getRole() != null) ?
                userRoleRepository.findById(Long.parseLong(filters.getRole())).orElse(null) : null;
        String phone = filters.getPhone();
        String email = filters.getEmail();
        Boolean active = filters.getActive();
        log.info(name + ' ' + role + ' ' + phone + ' ' + email + ' ' + active);

        Specification<Admin> specification = Specification.where(AdminSpecifications.hasNameLike(name)
                                                            .and(AdminSpecifications.hasRole((role != null) ? role.getName() : null))
                                                            .and(AdminSpecifications.hasPhoneLike(phone))
                                                            .and(AdminSpecifications.hasEmailLike(email))
                                                            .and(AdminSpecifications.isActive(active)));

//        Specification<Admin> specification = Specification.where(AdminSpecifications.hasNameLike(name)
//                .and(AdminSpecifications.hasPhoneLike(phone)));

        log.info("Specification built! " + specification);

        return specification;
    }

    @Override
    public List<AdminDTO> findMastersByType(Long typeID) {
        UserRole role = userRoleRepository.getReferenceById(typeID);
        Specification<Admin> spec = AdminSpecifications.hasRole(role.getName());
        return adminRepository.findAll(spec).stream().map(mapper::fromAdminToDTO).collect(Collectors.toList());
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Searching for user with username " + username);
        Admin admin = adminRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("Такого пользователя не существует"));
        log.info("Admin found : " + admin.toString());
        return admin;
    }

    @Override
    public Page<AdminDTO> findAllBySpecification(FilterForm filters, Integer page, Integer size) throws IllegalAccessException {
        Pageable pageable = PageRequest.of(page-1, size);
        Page<Admin> ownerList = adminRepository.findAll(buildSpecFromFilters(filters), pageable);
        log.info("Page of found: " + ownerList + ", total elements " + ownerList.getTotalElements() + ", total pages " + ownerList.getTotalPages());
        List<AdminDTO> listDTO = ownerList.getContent().stream().map(mapper::fromAdminToDTO).collect(Collectors.toList());
        log.info("List of found: " + listDTO);
        return new PageImpl<>(listDTO, pageable, ownerList.getTotalElements());
    }
}
