package com.example.myhome.specifications;

import com.example.myhome.model.*;
import com.example.myhome.repository.MeterDataRepository;
import com.example.myhome.specification.MeterSpecifications;
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
public class MeterSpecificationTest {

    @Autowired
    MeterDataRepository repository;

    @Autowired
    TestEntityManager em;

    CriteriaBuilder cb;
    CriteriaQuery<MeterData> cq;
    Root<MeterData> root;

    @BeforeEach
    void criteria() {
        cb = em.getEntityManager().getCriteriaBuilder();
        cq = cb.createQuery(MeterData.class);
        root = cq.from(MeterData.class);
    }

    @Test
    void idSpecTest() {
        Specification<MeterData> spec = MeterSpecifications.hasId(null);
        assertThat(spec.toPredicate(root, cq, cb)).isNull();
        spec = MeterSpecifications.hasId(1L);
        assertThat(spec.toPredicate(root, cq, cb)).isNotNull();
    }

    @Test
    void statusSpecTest() {
        Specification<MeterData> spec = MeterSpecifications.hasStatus(null);
        assertThat(spec.toPredicate(root, cq, cb)).isNull();
        spec = MeterSpecifications.hasStatus(MeterPaymentStatus.PAID);
        assertThat(spec.toPredicate(root, cq, cb)).isNotNull();
    }

    @Test
    void dateSpecTest() {
        Specification<MeterData> spec = MeterSpecifications.datesBetween(null, null);
        assertThat(spec.toPredicate(root, cq, cb)).isNull();
        spec = MeterSpecifications.datesBetween(LocalDate.now().minusDays(1L), LocalDate.now());
        assertThat(spec.toPredicate(root, cq, cb)).isNotNull();
    }

    @Test
    void buildingSpecTest() {
        Specification<MeterData> spec = MeterSpecifications.hasBuilding(null);
        assertThat(spec.toPredicate(root, cq, cb)).isNull();
        spec = MeterSpecifications.hasBuilding(new Building());
        assertThat(spec.toPredicate(root, cq, cb)).isNotNull();
    }

    @Test
    void sectionSpecTest() {
        Specification<MeterData> spec = MeterSpecifications.hasSection(null);
        assertThat(spec.toPredicate(root, cq, cb)).isNull();
        spec = MeterSpecifications.hasSection("test");
        assertThat(spec.toPredicate(root, cq, cb)).isNotNull();
    }

    @Test
    void apartmentSpecTest() {
        Specification<MeterData> spec = MeterSpecifications.hasApartment(null);
        assertThat(spec.toPredicate(root, cq, cb)).isNull();
        spec = MeterSpecifications.hasApartment(new Apartment());
        assertThat(spec.toPredicate(root, cq, cb)).isNotNull();
    }

    @Test
    void apartmentNumberSpecTest() {
        Specification<MeterData> spec = MeterSpecifications.hasApartmentNumber(null);
        assertThat(spec.toPredicate(root, cq, cb)).isNull();
        spec = MeterSpecifications.hasApartmentNumber(1L);
        assertThat(spec.toPredicate(root, cq, cb)).isNotNull();
    }

    @Test
    void serviceSpecTest() {
        Specification<MeterData> spec = MeterSpecifications.hasService(null);
        assertThat(spec.toPredicate(root, cq, cb)).isNull();
        spec = MeterSpecifications.hasService(new Service());
        assertThat(spec.toPredicate(root, cq, cb)).isNotNull();
    }



}
