package com.example.myhome.specifications;

import com.example.myhome.model.Owner;
import com.example.myhome.repository.OwnerRepository;
import com.example.myhome.specification.OwnerSpecifications;
import com.example.myhome.util.UserStatus;
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
public class OwnerSpecificationTest {

    @Autowired
    OwnerRepository repository;

    @Autowired
    TestEntityManager em;

    CriteriaBuilder cb;
    CriteriaQuery<Owner> cq;
    Root<Owner> root;

    @BeforeEach
    void criteria() {
        cb = em.getEntityManager().getCriteriaBuilder();
        cq = cb.createQuery(Owner.class);
        root = cq.from(Owner.class);
    }

    @Test
    void idSpecTest() {
        Specification<Owner> spec = OwnerSpecifications.idContains(1L);
        assertThat(spec.toPredicate(root, cq, cb)).isNotNull();
    }

    @Test
    void nameSpecTest() {
        Specification<Owner> spec = OwnerSpecifications.nameContains("test");
        assertThat(spec.toPredicate(root, cq, cb)).isNotNull();
    }

    @Test
    void phoneSpecTest() {
        Specification<Owner> spec = OwnerSpecifications.phonenumberContains("111111111");
        assertThat(spec.toPredicate(root, cq, cb)).isNotNull();
    }

    @Test
    void emailSpecTest() {
        Specification<Owner> spec = OwnerSpecifications.emailContains("test@email.com");
        assertThat(spec.toPredicate(root, cq, cb)).isNotNull();
    }

    @Test
    void buildingSpecTest() {
        Specification<Owner> spec = OwnerSpecifications.buildingContains("test");
        assertThat(spec.toPredicate(root, cq, cb)).isNotNull();
    }


    @Test
    void apartmentSpecTest() {
        Specification<Owner> spec = OwnerSpecifications.apartmentContains(1L);
        assertThat(spec.toPredicate(root, cq, cb)).isNotNull();
    }

    @Test
    void dateSpecTest() {
        Specification<Owner> spec = OwnerSpecifications.dateContains(LocalDate.now());
        assertThat(spec.toPredicate(root, cq, cb)).isNotNull();
    }

    @Test
    void statusSpecTest() {
        Specification<Owner> spec = OwnerSpecifications.statusContains(UserStatus.ACTIVE);
        assertThat(spec.toPredicate(root, cq, cb)).isNotNull();
    }

    @Test
    void debtSpecTest() {
        Specification<Owner> spec = OwnerSpecifications.hasDebt(true);
        assertThat(spec.toPredicate(root, cq, cb)).isNotNull();
    }




}
