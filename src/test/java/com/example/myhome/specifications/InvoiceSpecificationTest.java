package com.example.myhome.specifications;

import com.example.myhome.model.Apartment;
import com.example.myhome.model.Invoice;
import com.example.myhome.model.InvoiceStatus;
import com.example.myhome.model.Owner;
import com.example.myhome.repository.CashBoxRepository;
import com.example.myhome.specification.InvoiceSpecifications;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class InvoiceSpecificationTest {

    @Autowired
    CashBoxRepository repository;

    @Autowired
    TestEntityManager em;

    CriteriaBuilder cb;
    CriteriaQuery<Invoice> cq;
    Root<Invoice> root;

    @BeforeEach
    void criteria() {
        cb = em.getEntityManager().getCriteriaBuilder();
        cq = cb.createQuery(Invoice.class);
        root = cq.from(Invoice.class);
    }

    @Test
    void idSpecTest() {
        Specification<Invoice> spec = InvoiceSpecifications.hasId(1L);
        assertThat(spec.toPredicate(root, cq, cb)).isNotNull();
    }

    @Test
    void statusSpecTest() {
        Specification<Invoice> spec = InvoiceSpecifications.hasStatus(InvoiceStatus.PAID);
        assertThat(spec.toPredicate(root, cq, cb)).isNotNull();
    }

    @Test
    void apartmentSpecTest() {
        Specification<Invoice> spec = InvoiceSpecifications.hasApartment(new Apartment());
        assertThat(spec.toPredicate(root, cq, cb)).isNotNull();
    }

    @Test
    void apartmentNumberSpecTest() {
        Specification<Invoice> spec = InvoiceSpecifications.hasApartmentNumber(1L);
        assertThat(spec.toPredicate(root, cq, cb)).isNotNull();
    }

    @Test
    void ownerSpecTest() {
        Specification<Invoice> spec = InvoiceSpecifications.hasOwner(new Owner());
        assertThat(spec.toPredicate(root, cq, cb)).isNotNull();
    }

    @Test
    void completedSpecTest() {
        Specification<Invoice> spec = InvoiceSpecifications.isCompleted(true);
        assertThat(spec.toPredicate(root, cq, cb)).isNotNull();
    }

    @Test
    void dateSpecTest() {
        Specification<Invoice> spec = InvoiceSpecifications.datesBetween(LocalDate.now().minusDays(1), LocalDate.now());
        assertThat(spec.toPredicate(root, cq, cb)).isNotNull();
    }



}
