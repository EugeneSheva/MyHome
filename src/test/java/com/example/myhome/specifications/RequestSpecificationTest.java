package com.example.myhome.specifications;

import com.example.myhome.model.*;
import com.example.myhome.repository.RepairRequestRepository;
import com.example.myhome.specification.RequestSpecifications;
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
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RequestSpecificationTest {

    @Autowired
    RepairRequestRepository repository;

    @Autowired
    TestEntityManager em;

    CriteriaBuilder cb;
    CriteriaQuery<RepairRequest> cq;
    Root<RepairRequest> root;

    @BeforeEach
    void criteria() {
        cb = em.getEntityManager().getCriteriaBuilder();
        cq = cb.createQuery(RepairRequest.class);
        root = cq.from(RepairRequest.class);
    }

    @Test
    void idSpecTest() {
        Specification<RepairRequest> spec = RequestSpecifications.hasId(null);
        assertThat(spec.toPredicate(root, cq, cb)).isNull();
        spec = RequestSpecifications.hasId(1L);
        assertThat(spec.toPredicate(root, cq, cb)).isNotNull();
    }

    @Test
    void masterTypeSpecTest() {
        Specification<RepairRequest> spec = RequestSpecifications.hasMasterType(null);
        assertThat(spec.toPredicate(root, cq, cb)).isNull();
        spec = RequestSpecifications.hasMasterType(new UserRole());
        assertThat(spec.toPredicate(root, cq, cb)).isNotNull();
    }

    @Test
    void descriptionSpecTest() {
        Specification<RepairRequest> spec = RequestSpecifications.hasDescriptionLike("test");
        assertThat(spec.toPredicate(root, cq, cb)).isNull();
        spec = RequestSpecifications.hasDescriptionLike("test");
        assertThat(spec.toPredicate(root, cq, cb)).isNotNull();
    }

    @Test
    void apartmentSpecTest() {
        Specification<RepairRequest> spec = RequestSpecifications.hasApartment(null);
        assertThat(spec.toPredicate(root, cq, cb)).isNull();
        spec = RequestSpecifications.hasApartment(new Apartment());
        assertThat(spec.toPredicate(root, cq, cb)).isNotNull();
    }

    @Test
    void ownerSpecTest() {
        Specification<RepairRequest> spec = RequestSpecifications.hasOwner(null);
        assertThat(spec.toPredicate(root, cq, cb)).isNull();
        spec = RequestSpecifications.hasOwner(new Owner());
        assertThat(spec.toPredicate(root, cq, cb)).isNotNull();
    }

    @Test
    void phoneSpecTest() {
        Specification<RepairRequest> spec = RequestSpecifications.hasPhoneLike(null);
        assertThat(spec.toPredicate(root, cq, cb)).isNull();
        spec = RequestSpecifications.hasPhoneLike("+38050111111");
        assertThat(spec.toPredicate(root, cq, cb)).isNotNull();
    }

    @Test
    void masterSpecTest() {
        Specification<RepairRequest> spec = RequestSpecifications.hasMaster(null);
        assertThat(spec.toPredicate(root, cq, cb)).isNull();
        spec = RequestSpecifications.hasMaster(new Admin());
        assertThat(spec.toPredicate(root, cq, cb)).isNotNull();
    }

    @Test
    void statusSpecTest() {
        Specification<RepairRequest> spec = RequestSpecifications.hasStatus(null);
        assertThat(spec.toPredicate(root, cq, cb)).isNull();
        spec = RequestSpecifications.hasStatus(RepairStatus.ACCEPTED);
        assertThat(spec.toPredicate(root, cq, cb)).isNotNull();
    }

    @Test
    void dateSpecTest() {
        Specification<RepairRequest> spec = RequestSpecifications.datesBetween(null, null);
        assertThat(spec.toPredicate(root, cq, cb)).isNull();
        spec = RequestSpecifications.datesBetween(LocalDateTime.now(),LocalDateTime.now());
        assertThat(spec.toPredicate(root, cq, cb)).isNotNull();
    }



}
