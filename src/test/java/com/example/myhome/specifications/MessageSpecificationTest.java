package com.example.myhome.specifications;

import com.example.myhome.model.Message;
import com.example.myhome.repository.MessageRepository;
import com.example.myhome.specification.MessageSpecifications;
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
public class MessageSpecificationTest {

    @Autowired
    MessageRepository repository;

    @Autowired
    TestEntityManager em;

    CriteriaBuilder cb;
    CriteriaQuery<Message> cq;
    Root<Message> root;

    @BeforeEach
    void criteria() {
        cb = em.getEntityManager().getCriteriaBuilder();
        cq = cb.createQuery(Message.class);
        root = cq.from(Message.class);
    }

    @Test
    void textSpecTest() {
        Specification<Message> spec = MessageSpecifications.textContains("test");
        assertThat(spec.toPredicate(root, cq, cb)).isNotNull();
    }

    @Test
    void receiverSpecTest() {
        Specification<Message> spec = MessageSpecifications.receiverContains(1L);
        assertThat(spec.toPredicate(root, cq, cb)).isNotNull();
    }




}
