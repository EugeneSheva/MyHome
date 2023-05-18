package com.example.myhome.repository;

import com.example.myhome.model.InvoiceComponents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InvoiceComponentRepository extends JpaRepository<InvoiceComponents, Long> {

    List<InvoiceComponents> findByInvoice_Apartment_IdAndInvoice_DateBetween(Long apartmentId, LocalDate startDate, LocalDate endDate);

}
