package com.example.myhome.home.repos;

import com.example.myhome.home.model.ApartmentAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<ApartmentAccount, Long> {

    Optional<ApartmentAccount> findFirstByOrderByIdDesc();

    Optional<ApartmentAccount> findByApartmentId(long apartment_id);
}