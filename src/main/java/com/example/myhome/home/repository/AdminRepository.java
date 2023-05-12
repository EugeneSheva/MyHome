package com.example.myhome.home.repository;

import com.example.myhome.home.model.Admin;
import com.example.myhome.home.model.Owner;
import com.example.myhome.home.specification.AdminSpecifications;
import com.example.myhome.home.specification.OwnerSpecifications;
import com.example.myhome.util.UserRole;
import com.example.myhome.util.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long>, JpaSpecificationExecutor<Admin> {

    List<Admin> getAdminsByRole(UserRole role);
    Optional<Admin> findByEmail(String email);
    Boolean existsByEmail(String email);

    @Query(value="SELECT a FROM Admin a WHERE a.first_name LIKE %:name% OR a.last_name LIKE %:name%")
    Page<Admin> findByName(String name, Pageable pageable);

    default Page<Admin> findByFilters(String name, String role, String phoneNumber, String email, String active, Pageable pageable) {
        Specification<Admin> spec = Specification.where(null);

        if (name != null && !name.isEmpty()) {
            spec = spec.and(AdminSpecifications.hasNameLike(name));
        }

        if (role != null && !role.isEmpty()) {
            spec = spec.and(AdminSpecifications.hasRole(role));
        }

        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            spec = spec.and(AdminSpecifications.hasPhoneLike(phoneNumber));
        }

        if (email != null && !email.isEmpty()) {
            spec = spec.and(AdminSpecifications.hasEmailLike(email));
        }

        if (active != null && !active.isEmpty()) {
            spec = spec.and(AdminSpecifications.isActive(Boolean.valueOf(active)));
        }

        return findAll(spec, pageable);
    }
}
