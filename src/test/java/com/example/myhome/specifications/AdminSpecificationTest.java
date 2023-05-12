package com.example.myhome.specifications;

import com.example.myhome.home.model.Admin;
import com.example.myhome.home.model.UserRole;
import com.example.myhome.home.repository.AdminRepository;
import com.example.myhome.home.specification.AdminSpecifications;
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
public class AdminSpecificationTest {

    @Autowired
    AdminRepository repository;

    @Autowired
    TestEntityManager em;

    @Test
    void roleSpecTest() {
        Specification<Admin> spec = AdminSpecifications.hasRole(null);
        CriteriaBuilder cb = em.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Admin> cq = cb.createQuery(Admin.class);
        Root<Admin> root = cq.from(Admin.class);
        assertThat(spec.toPredicate(root, cq, cb)).isNull();

        UserRole role = new UserRole();

        spec = AdminSpecifications.hasRole(role.getName());

        assertThat(spec.toPredicate(root, cq, cb)).isNotNull();
    }

    @Test
    void nameSpecTest() {
        Specification<Admin> spec = AdminSpecifications.hasNameLike(null);
        CriteriaBuilder cb = em.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Admin> cq = cb.createQuery(Admin.class);
        Root<Admin> root = cq.from(Admin.class);
        assertThat(spec.toPredicate(root, cq, cb)).isNull();

        spec = AdminSpecifications.hasNameLike("test");

        assertThat(spec.toPredicate(root, cq, cb)).isNotNull();
    }

    @Test
    void phoneSpecTest() {
        Specification<Admin> spec = AdminSpecifications.hasPhoneLike(null);
        CriteriaBuilder cb = em.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Admin> cq = cb.createQuery(Admin.class);
        Root<Admin> root = cq.from(Admin.class);
        assertThat(spec.toPredicate(root, cq, cb)).isNull();

        spec = AdminSpecifications.hasPhoneLike("+380501111111");

        assertThat(spec.toPredicate(root, cq, cb)).isNotNull();
    }

    @Test
    void emailSpecTest() {
        Specification<Admin> spec = AdminSpecifications.hasEmailLike(null);
        CriteriaBuilder cb = em.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Admin> cq = cb.createQuery(Admin.class);
        Root<Admin> root = cq.from(Admin.class);
        assertThat(spec.toPredicate(root, cq, cb)).isNull();

        spec = AdminSpecifications.hasEmailLike("test@gmail.com");

        assertThat(spec.toPredicate(root, cq, cb)).isNotNull();
    }


    @Test
    void activeSpecTest() {
        Specification<Admin> spec = AdminSpecifications.isActive(null);
        CriteriaBuilder cb = em.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Admin> cq = cb.createQuery(Admin.class);
        Root<Admin> root = cq.from(Admin.class);
        assertThat(spec.toPredicate(root, cq, cb)).isNull();

        spec = AdminSpecifications.isActive(true);

        assertThat(spec.toPredicate(root, cq, cb)).isNotNull();
    }
}
