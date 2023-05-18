package com.example.myhome.specifications;

import com.example.myhome.model.Building;
import com.example.myhome.repository.BuildingRepository;
import com.example.myhome.specification.BuildingSpecifications;
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
public class BuildingSpecificationTest {

    @Autowired
    BuildingRepository repository;

    @Autowired
    TestEntityManager em;

    CriteriaBuilder cb;
    CriteriaQuery<Building> cq;
    Root<Building> root;

    @BeforeEach
    void criteria() {
        cb = em.getEntityManager().getCriteriaBuilder();
        cq = cb.createQuery(Building.class);
        root = cq.from(Building.class);
    }

    @Test
    void idSpecTest() {
        Specification<Building> spec = BuildingSpecifications.hasId(1L);
        assertThat(spec.toPredicate(root, cq, cb)).isNotNull();
    }

    @Test
    void addressSpecTest() {
        Specification<Building> spec = BuildingSpecifications.hasAddressLike("test");
        assertThat(spec.toPredicate(root, cq, cb)).isNotNull();
    }

    @Test
    void nameSpecTest() {
        Specification<Building> spec = BuildingSpecifications.hasNameLike("test");
        assertThat(spec.toPredicate(root, cq, cb)).isNotNull();
    }


}
