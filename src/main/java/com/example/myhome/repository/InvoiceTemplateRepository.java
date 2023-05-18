package com.example.myhome.repository;

import com.example.myhome.model.InvoiceTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvoiceTemplateRepository extends JpaRepository<InvoiceTemplate, Long> {
    @Query("SELECT t FROM InvoiceTemplate t WHERE t._default_=true")
    Optional<InvoiceTemplate> getDefaultTemplate();
}
