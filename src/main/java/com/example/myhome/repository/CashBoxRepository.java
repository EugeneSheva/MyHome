package com.example.myhome.repository;

import com.example.myhome.model.CashBox;
import com.example.myhome.model.IncomeExpenseType;
import com.example.myhome.specification.CashBoxSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CashBoxRepository extends JpaRepository<CashBox, Long>, JpaSpecificationExecutor<CashBox> {
    @Query("SELECT SUM(cb.amount) FROM CashBox cb WHERE cb.completed=true")
    Optional<Double> sumAmount();

    @Query("SELECT SUM(cb.amount) FROM CashBox cb WHERE MONTH(cb.date) = :month AND YEAR(cb.date) = :year AND (cb.incomeExpenseType) = :incomeExpenseType AND cb.completed=true")
    Double getSumByMonth(@Param("month") int month, @Param("year") int year, @Param("incomeExpenseType") IncomeExpenseType incomeExpenseType);

    @Query("SELECT max(c.id) FROM CashBox c")
    Optional<Long> getMaxId();

    List<CashBox>findAllByApartmentAccountId(Long id);
    default Page<CashBox> findByFilters(Long id, LocalDate from, LocalDate to, Boolean isCompleted, String incomeExpenseItem, Long ownerId, Long accountNumber, IncomeExpenseType incomeExpenseType, Pageable pageable) {
        Specification<CashBox> spec = Specification.where(null);

        if (id != null ) {
            spec = spec.and(CashBoxSpecifications.idContains(id));
        }
        if (isCompleted != null) {
            spec = spec.and(CashBoxSpecifications.isCompletedContains(isCompleted));
        }
        if (from != null && to != null) {
            spec = spec.and(CashBoxSpecifications.dateBetwenContains(from, to));
        }
        if (from == null && to != null) {
            spec = spec.and(CashBoxSpecifications.dateBeforeContains(to));
        }
        if (from != null && to == null) {
            spec = spec.and(CashBoxSpecifications.dateAfterContains(from));
        }
        if (incomeExpenseItem != null && !incomeExpenseItem.isEmpty() && !incomeExpenseItem.equalsIgnoreCase("-")) {
            spec = spec.and(CashBoxSpecifications.incExpItemContains(incomeExpenseItem));
        }
        if (ownerId != null && ownerId>0) {
            spec = spec.and(CashBoxSpecifications.ownerContains(ownerId));
        }
        if (accountNumber != null ) {
            spec = spec.and(CashBoxSpecifications.accountContains(accountNumber));
        }
        if (incomeExpenseType != null) {
            spec = spec.and(CashBoxSpecifications.incomeExpenseTypeContains(incomeExpenseType));
        }

        return findAll(spec, pageable);
    }
    Page<CashBox> findAll(Pageable pageable);

    }
