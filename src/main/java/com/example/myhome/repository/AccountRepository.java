package com.example.myhome.repository;

import com.example.myhome.model.ApartmentAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<ApartmentAccount, Long>, JpaSpecificationExecutor<ApartmentAccount> {
    Long countAllBy();

    Optional<ApartmentAccount> findFirstByOrderByIdDesc();

    Optional<ApartmentAccount> findByApartmentId(long apartment_id);

    @Query("SELECT MAX(a.id) FROM ApartmentAccount a")
    Optional<Long> getMaxId();

    @Query("SELECT SUM(a.balance) FROM ApartmentAccount a")
    Double getSumOfAccountBalances();

    @Query("SELECT SUM(a.balance) FROM ApartmentAccount a WHERE a.balance <= 0")
    Double getSumOfAccountDebts();


}
