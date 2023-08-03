package com.example.myhome.repository;

import com.example.myhome.model.Tariff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TariffRepository extends JpaRepository<Tariff, Long> {
    Tariff findByName(String name);
    boolean existsByName(String name);
    List<Tariff> findAllByOrderByNameAsc();
    List<Tariff> findAllByOrderByNameDesc();
}
