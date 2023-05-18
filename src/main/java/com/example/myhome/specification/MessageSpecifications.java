package com.example.myhome.specification;

import com.example.myhome.model.Message;
import com.example.myhome.model.Owner;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;


public class MessageSpecifications {


    public static Specification<Message> textContains(String text) {
        if(text == null) return (root, query, criteriaBuilder) -> null;
        return (root, query, builder) -> {
            Predicate[] predicates = new Predicate[2];
            for (int i = 0; i < 2; i++) {
                predicates[i] = builder.or(
                        builder.like(root.get("subject"), "%" + text + "%"),
                        builder.like(root.get("text"), "%" + text + "%")
                );
            }
            return builder.and(predicates);
        };
    }
    public static Specification<Message> receiverContains(Long id) {
        return (root, query, builder) -> {
            Join<Message, Owner> receiverJoin = root.join("receivers", JoinType.INNER);
            return builder.equal(receiverJoin.get("id"), id);
        };
    }

}
