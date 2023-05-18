package com.example.myhome.specification;

import com.example.myhome.model.*;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import java.time.LocalDate;


public class CashBoxSpecifications {

    public static Specification<CashBox> idContains(Long id) {
        return (root, query, builder) -> builder.equal(root.get("id"), id);
    }

    public static Specification<CashBox> dateBetwenContains(LocalDate startOfDay, LocalDate endOfDay) {
        return (root, query, builder) ->  builder.between(root.get("date"), startOfDay, endOfDay);
    }
    public static Specification<CashBox> dateAfterContains(LocalDate date) {
        return (root, query, builder) ->  builder.greaterThanOrEqualTo(root.get("date"), date);
    }
    public static Specification<CashBox> dateBeforeContains(LocalDate date) {
        return (root, query, builder) ->  builder.lessThanOrEqualTo(root.get("date"), date);
    }

    public static Specification<CashBox> isCompletedContains(Boolean isComplete) {
        return (root, query, builder) -> builder.equal(root.get("completed"), isComplete);
    }

    public static Specification<CashBox> incExpItemContains(String incExpItem) {
        return (root, query, builder) -> {
            Join<CashBox, IncomeExpenseItems> Join = root.join("incomeExpenseItems", JoinType.INNER);
            return builder.equal(Join.get("name"), incExpItem);
        };
    }

    public static Specification<CashBox> ownerContains(Long userId) {
        return (root, query, builder) -> {
            Join<CashBox, Owner> cashBoxOwnerJoin = root.join("owner", JoinType.INNER);
            return builder.equal(cashBoxOwnerJoin.get("id"), userId);
        };
    }

    public static Specification<CashBox> accountContains(Long acc) {
        return (root, query, builder) -> {
            Join<CashBox, ApartmentAccount> cashBoxOwnerJoin = root.join("apartmentAccount", JoinType.INNER);
            return builder.equal(cashBoxOwnerJoin.get("id"), acc);
        };
    }

    public static Specification<CashBox> incomeExpenseTypeContains(IncomeExpenseType incomeExpenseType) {
        return (root, query, builder) -> builder.equal(root.get("incomeExpenseType"), incomeExpenseType);
    }

}
