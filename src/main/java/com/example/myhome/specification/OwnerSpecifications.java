package com.example.myhome.specification;

import com.example.myhome.model.Apartment;
import com.example.myhome.model.Building;
import com.example.myhome.model.Owner;
import com.example.myhome.util.UserStatus;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;


public class OwnerSpecifications {

    public static Specification<Owner> idContains(Long id) {
        if(id == null) return (root, query, criteriaBuilder) -> null;
        return (root, query, builder) -> builder.equal(root.get("id"), id);
    }

    public static Specification<Owner> nameContains(String name) {
        if(name == null) return (root, query, criteriaBuilder) -> null;
        return (root, query, builder) -> {
            String[] names = name.split("\\s+");
            Predicate[] predicates = new Predicate[names.length];
            for (int i = 0; i < names.length; i++) {
                predicates[i] = builder.or(
                        builder.like(root.get("first_name"), "%" + names[i] + "%"),
                        builder.like(root.get("last_name"), "%" + names[i] + "%"),
                        builder.like(root.get("fathers_name"), "%" + names[i] + "%")
                );
            }
            return builder.and(predicates);
        };
    }

    public static Specification<Owner> phonenumberContains(String phoneNumber) {
        if(phoneNumber == null) return (root, query, criteriaBuilder) -> null;
        return (root, query, builder) -> builder.like(root.get("phone_number"), "%" + phoneNumber + "%");
    }

    public static Specification<Owner> emailContains(String email) {
        if(email == null) return (root, query, criteriaBuilder) -> null;
        return (root, query, builder) -> builder.like(root.get("email"), "%" + email + "%");
    }

    public static Specification<Owner> buildingContains(String name) {
        if(name == null) return (root, query, criteriaBuilder) -> null;
        return (root, query, builder) -> {
            Join<Owner, Apartment> ownerJoin = root.join("apartments", JoinType.INNER);
            Join<Owner, Building> thirdTableJoin = ownerJoin.join("building", JoinType.INNER);
            return builder.like(thirdTableJoin.get("name"), "%" + name + "%");
        };
    }

    public static Specification<Owner> hasBuilding(Long building_id) {
        if(building_id == null) return (root, query, criteriaBuilder) -> null;
        return (root, query, cb) -> {
            Join<Owner, Apartment> ownerJoin = root.join("apartments", JoinType.INNER);
            Join<Owner, Building> thirdTableJoin = ownerJoin.join("building", JoinType.INNER);
            return cb.equal(thirdTableJoin.get("building_id"), building_id);
        };
    }

    public static Specification<Owner> apartmentContains(Long number) {
        if(number == null) return (root, query, criteriaBuilder) -> null;
        return (root, query, builder) -> {
            Join<Owner, Apartment> apartmentJoin = root.join("apartments", JoinType.INNER);
            return builder.equal(apartmentJoin.get("number"), number);
        };
    }
    public static Specification<Owner> dateContains(LocalDate date) {
        if(date == null) return (root, query, criteriaBuilder) -> null;
        return (root, query, builder) -> {
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
            return builder.between(root.get("added_at"), startOfDay, endOfDay);
        };
    }
    public static Specification<Owner> statusContains(UserStatus userStatus) {
        if(userStatus == null) return (root, query, criteriaBuilder) -> null;
        return (root, query, builder) -> builder.equal(root.get("status"), userStatus);
    }

    public static Specification<Owner> hasDebt(Boolean hasDebt) {
        if(hasDebt == null) return (root, query, criteriaBuilder) -> null;
        return (root, query, cb) -> cb.equal(root.get("has_debt"), hasDebt);
    }

    public static Specification<Owner> hasDebtContains() {
        return (root, query, builder) -> builder.equal(root.get("has_debt"), true);
    }
    public static Specification<Owner> noDebtContains() {
        return (root, query, builder) -> builder.equal(root.get("has_debt"), false);
    }

}
