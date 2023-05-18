package com.example.myhome.specifications;

import com.example.myhome.model.CashBox;
import com.example.myhome.model.IncomeExpenseType;
import com.example.myhome.repository.CashBoxRepository;
import com.example.myhome.specification.CashBoxSpecifications;
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
public class CashBoxSpecificationTest {

    @Autowired
    CashBoxRepository repository;

    @Autowired
    TestEntityManager em;

    CriteriaBuilder cb;
    CriteriaQuery<CashBox> cq;
    Root<CashBox> root;

    @BeforeEach
    void criteria() {
        cb = em.getEntityManager().getCriteriaBuilder();
        cq = cb.createQuery(CashBox.class);
        root = cq.from(CashBox.class);
    }

    @Test
    void idSpecTest() {
        Specification<CashBox> spec = CashBoxSpecifications.idContains(1L);
        assertThat(spec.toPredicate(root, cq, cb)).isNotNull();
    }

    @Test
    void dateSpecTest() {
        Specification<CashBox> spec = CashBoxSpecifications.dateBeforeContains(LocalDate.now());
        assertThat(spec.toPredicate(root, cq, cb)).isNotNull();
    }

    @Test
    void completedSpecTest() {
        Specification<CashBox> spec = CashBoxSpecifications.isCompletedContains(true);
        assertThat(spec.toPredicate(root, cq, cb)).isNotNull();
    }

    @Test
    void transactionSpecTest() {
        Specification<CashBox> spec = CashBoxSpecifications.incExpItemContains("test");
        assertThat(spec.toPredicate(root, cq, cb)).isNotNull();
    }

    @Test
    void ownerSpecTest() {
        Specification<CashBox> spec = CashBoxSpecifications.ownerContains(1L);
        assertThat(spec.toPredicate(root, cq, cb)).isNotNull();
    }

    @Test
    void accountSpecTest() {
        Specification<CashBox> spec = CashBoxSpecifications.accountContains(1L);
        assertThat(spec.toPredicate(root, cq, cb)).isNotNull();
    }

    @Test
    void transactionTypeSpecTest() {
        Specification<CashBox> spec = CashBoxSpecifications.incomeExpenseTypeContains(IncomeExpenseType.INCOME);
        assertThat(spec.toPredicate(root, cq, cb)).isNotNull();
    }

}
