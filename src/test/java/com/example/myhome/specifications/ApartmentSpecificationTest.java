package com.example.myhome.specifications;

import com.example.myhome.model.Apartment;
import com.example.myhome.repository.ApartmentRepository;
import com.example.myhome.specification.ApartmentSpecifications;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ApartmentSpecificationTest {

    @Autowired
    ApartmentRepository repository;

    @Autowired
    TestEntityManager em;

    CriteriaBuilder cb;
    CriteriaQuery<Apartment> cq;
    Root<Apartment> root;

    @BeforeEach
    void criteria() {
        cb = em.getEntityManager().getCriteriaBuilder();
        cq = cb.createQuery(Apartment.class);
        root = cq.from(Apartment.class);
    }

    @Test
    void numberSpecTest() {
        Specification<Apartment> spec = ApartmentSpecifications.numberContains(1L);
        assertThat(spec.toPredicate(root, cq, cb)).isNotNull();
    }

    @Test
    void buildingSpecTest() {
        Specification<Apartment> spec = ApartmentSpecifications.buildingContains("test");
        assertThat(spec.toPredicate(root, cq, cb)).isNotNull();
    }

    @Test
    void sectionSpecTest() {
        Specification<Apartment> spec = ApartmentSpecifications.sectionContains("test");
        assertThat(spec.toPredicate(root, cq, cb)).isNotNull();
    }

    @Test
    void floorSpecTest() {
        Specification<Apartment> spec = ApartmentSpecifications.floorContains("test");
        assertThat(spec.toPredicate(root, cq, cb)).isNotNull();
    }

    @Test
    void ownerSpecTest() {
        Specification<Apartment> spec = ApartmentSpecifications.ownerContains(1L);
        assertThat(spec.toPredicate(root, cq, cb)).isNotNull();
    }

    @Test
    void debtSpecTest() {
        Specification<Apartment> spec = ApartmentSpecifications.hasdebtContains();
        assertThat(spec.toPredicate(root, cq, cb)).isNotNull();
    }


}
