package com.example.myhome.specifications;

import com.example.myhome.model.Apartment;
import com.example.myhome.model.ApartmentAccount;
import com.example.myhome.model.Building;
import com.example.myhome.model.Owner;
import com.example.myhome.repository.AccountRepository;
import com.example.myhome.specification.AccountSpecifications;
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
public class AccountSpecificationTest {

    @Autowired
    AccountRepository repository;

    @Autowired
    TestEntityManager em;

    @Test
    void idSpecTest() {
        Specification<ApartmentAccount> spec = AccountSpecifications.hasId(null);
        CriteriaBuilder cb = em.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ApartmentAccount> cq = cb.createQuery(ApartmentAccount.class);
        Root<ApartmentAccount> root = cq.from(ApartmentAccount.class);
        assertThat(spec.toPredicate(root, cq, cb)).isNull();

        spec = AccountSpecifications.hasId(1L);

        assertThat(spec.toPredicate(root, cq, cb)).isNotNull();
    }

    @Test
    void activeSpecTest() {
        Specification<ApartmentAccount> spec = AccountSpecifications.isActive(null);
        CriteriaBuilder cb = em.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ApartmentAccount> cq = cb.createQuery(ApartmentAccount.class);
        Root<ApartmentAccount> root = cq.from(ApartmentAccount.class);
        assertThat(spec.toPredicate(root, cq, cb)).isNull();

        spec = AccountSpecifications.isActive(true);

        assertThat(spec.toPredicate(root, cq, cb)).isNotNull();
    }

    @Test
    void apartmentSpecTest() {
        Specification<ApartmentAccount> spec = AccountSpecifications.hasApartment(null);
        CriteriaBuilder cb = em.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ApartmentAccount> cq = cb.createQuery(ApartmentAccount.class);
        Root<ApartmentAccount> root = cq.from(ApartmentAccount.class);
        assertThat(spec.toPredicate(root, cq, cb)).isNull();

        spec = AccountSpecifications.hasApartment(new Apartment());

        assertThat(spec.toPredicate(root, cq, cb)).isNotNull();
    }

    @Test
    void apartmentNumberSpecTest() {
        Specification<ApartmentAccount> spec = AccountSpecifications.hasApartmentNumber(null);
        CriteriaBuilder cb = em.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ApartmentAccount> cq = cb.createQuery(ApartmentAccount.class);
        Root<ApartmentAccount> root = cq.from(ApartmentAccount.class);
        assertThat(spec.toPredicate(root, cq, cb)).isNull();

        spec = AccountSpecifications.hasApartmentNumber(10L);

        assertThat(spec.toPredicate(root, cq, cb)).isNotNull();
    }

    @Test
    void buildingSpecTest() {
        Specification<ApartmentAccount> spec = AccountSpecifications.hasBuilding(null);
        CriteriaBuilder cb = em.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ApartmentAccount> cq = cb.createQuery(ApartmentAccount.class);
        Root<ApartmentAccount> root = cq.from(ApartmentAccount.class);
        assertThat(spec.toPredicate(root, cq, cb)).isNull();

        spec = AccountSpecifications.hasBuilding(new Building());

        assertThat(spec.toPredicate(root, cq, cb)).isNotNull();
    }

    @Test
    void sectionSpecTest() {
        Specification<ApartmentAccount> spec = AccountSpecifications.hasSection(null);
        CriteriaBuilder cb = em.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ApartmentAccount> cq = cb.createQuery(ApartmentAccount.class);
        Root<ApartmentAccount> root = cq.from(ApartmentAccount.class);
        assertThat(spec.toPredicate(root, cq, cb)).isNull();

        spec = AccountSpecifications.hasSection("test");

        assertThat(spec.toPredicate(root, cq, cb)).isNotNull();
    }

    @Test
    void ownerSpecTest() {
        Specification<ApartmentAccount> spec = AccountSpecifications.hasOwner(null);
        CriteriaBuilder cb = em.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ApartmentAccount> cq = cb.createQuery(ApartmentAccount.class);
        Root<ApartmentAccount> root = cq.from(ApartmentAccount.class);
        assertThat(spec.toPredicate(root, cq, cb)).isNull();

        spec = AccountSpecifications.hasOwner(new Owner());

        assertThat(spec.toPredicate(root, cq, cb)).isNotNull();
    }

    @Test
    void debtSpecTest() {
        Specification<ApartmentAccount> spec = AccountSpecifications.hasDebt(null);
        CriteriaBuilder cb = em.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ApartmentAccount> cq = cb.createQuery(ApartmentAccount.class);
        Root<ApartmentAccount> root = cq.from(ApartmentAccount.class);
        assertThat(spec.toPredicate(root, cq, cb)).isNull();

        spec = AccountSpecifications.hasDebt(true);

        assertThat(spec.toPredicate(root, cq, cb)).isNotNull();
    }


}
