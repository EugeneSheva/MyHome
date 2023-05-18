package com.example.myhome.repository;

import com.example.myhome.model.Invoice;
import com.example.myhome.model.InvoiceStatus;
import com.example.myhome.specification.InvoiceSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long>, JpaSpecificationExecutor<Invoice> {

    @Query("SELECT MAX(i.id) FROM Invoice i")
    Optional<Long> getMaxId();

    @Query("SELECT SUM(i.total_price) FROM Invoice i WHERE MONTH(i.date) = :month AND YEAR(i.date) = :year AND i.completed=true")
    Double getSumIvoice(@Param("month") int month, @Param("year") int year);

    @Query("SELECT SUM(i.total_price) FROM Invoice i WHERE MONTH(i.date) = :month AND YEAR(i.date) = :year AND i.completed=true AND i.status= :invoiceStatus")
    Double getSumPaidIvoice(@Param("month") int month, @Param("year") int year, @Param("invoiceStatus") InvoiceStatus invoiceStatus);

    List<Invoice>findAllByApartmentId(Long id);
    List<Invoice>findAllByOwnerId(Long id);

    @Query("SELECT AVG(i.total_price) FROM Invoice i WHERE i.apartment.id = :apartment AND i.dateFrom BETWEEN :lastYear AND :today")
    Double getAverageTotalPriceForApartmentBetwenDate(Long apartment, LocalDate lastYear, LocalDate today);


    @Query("SELECT SUM(i.total_price) FROM Invoice i WHERE i.apartment.id = :apartmentId AND MONTH(i.dateFrom) = :month AND YEAR(i.dateFrom) = :year")
    Double getTotalPriceByApartmentIdAndMonthAndYear(@Param("apartmentId") Long apartmentId, @Param("month") Integer month, @Param("year") Integer year);

    default Page<Invoice> findByFilters(LocalDate localDate, InvoiceStatus invoiceStatus, Pageable pageable) {
        System.out.println("date in repo "+ localDate);
        Specification<Invoice> spec = Specification.where(null);

        if (localDate != null) {
            spec = spec.and(InvoiceSpecifications.dateContains(localDate));
        }

        if (invoiceStatus != null) {
            spec = spec.and(InvoiceSpecifications.hasStatus(invoiceStatus));
        }

        return findAll(spec,pageable);
    }





}
