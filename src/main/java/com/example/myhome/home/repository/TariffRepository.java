package com.example.myhome.home.repository;

import com.example.myhome.home.model.Tariff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TariffRepository extends JpaRepository<Tariff, Long> {
    List<Tariff> findAllByOrderByNameAsc();
    List<Tariff> findAllByOrderByNameDesc();
}
