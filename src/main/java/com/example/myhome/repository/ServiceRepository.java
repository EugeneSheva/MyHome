package com.example.myhome.repository;

import com.example.myhome.model.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {
    Optional<Service> findByName(String name);
    Optional<String> findNameById(long service_id);
}
