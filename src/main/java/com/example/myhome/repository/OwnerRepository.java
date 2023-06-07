package com.example.myhome.repository;


import com.example.myhome.model.Owner;
import com.example.myhome.specification.OwnerSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import com.example.myhome.util.UserStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.Optional;

import java.time.LocalDate;
public interface OwnerRepository extends JpaRepository<Owner, Long>, JpaSpecificationExecutor<Owner> {

    Long countAllBy();

    @Query(nativeQuery = true
            , value = "SELECT COUNT(*) FROM owners WHERE status='ACTIVE'")
    Long countActive();

    default Page<Owner> findByFilters(Long id, String name, String phoneNumber,
                                      String email, String buildingName, Long apartmentNumber,
                                      LocalDate added, UserStatus status, Boolean hasDebt, Pageable pageable) {
        Specification<Owner> spec = Specification.where(null);
        if (id != null ) {
            spec = spec.and(OwnerSpecifications.idContains(id));
        }
        if (name != null && !name.isEmpty()) {
            spec = spec.and(OwnerSpecifications.nameContains(name));
        }
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            spec = spec.and(OwnerSpecifications.phonenumberContains(phoneNumber));
        }
        if (email != null && !email.isEmpty()) {
            spec = spec.and(OwnerSpecifications.emailContains(email));
        }
      if (buildingName != null && !buildingName.isEmpty() && !buildingName.equalsIgnoreCase("-")) {
          spec = spec.and(OwnerSpecifications.buildingContains(buildingName));
      }
        if (apartmentNumber != null) {
            spec = spec.and(OwnerSpecifications.apartmentContains(apartmentNumber));
        }
        if (added != null ) {
            spec = spec.and(OwnerSpecifications.dateContains(added));
        }
        if (status != null ) {
            spec = spec.and(OwnerSpecifications.statusContains(status));
        }
//        if (is_debt != null && !is_debt.isEmpty() && is_debt.equalsIgnoreCase("true" )) {
//            spec = spec.and(OwnerSpecifications.hasDebtContains());
//        }
//        if (is_debt != null && !is_debt.isEmpty() && is_debt.equalsIgnoreCase("false" )) {
//            spec = spec.and(OwnerSpecifications.noDebtContains());
//        }
        spec = spec.and(OwnerSpecifications.hasDebt(hasDebt));
        return findAll(spec, pageable);
    }

    default Page<Owner> findByNameFragment(String name, Pageable pageable) {
        System.out.println("name"+name);
        Specification<Owner> spec = Specification.where(null);
        if (name != null && !name.isEmpty()) {
            spec = spec.and(OwnerSpecifications.nameContains(name));
        }
        return findAll(spec, pageable);
    }
    Page<Owner> findAll(Pageable pageable);

//    List<Owner> findByfirst_nameContainingOrlast_nameContainingOrfather_nameContaining(String firstName, String lastName, String fatherName, Pageable pageable);

    Optional<Owner> findByEmail(String email);
    Boolean existsByEmail(String email);

//    List<Owner> findByFirstNameContainingIgnoreCase(String firstName, Pageable pageable);

    @Query(value="SELECT o FROM Owner o WHERE o.first_name LIKE %:name% OR o.last_name LIKE %:name% OR o.fathers_name LIKE %:name%")
    Page<Owner> findByName(String name, Pageable pageable);
}
