package com.example.myhome.specification;
import com.example.myhome.model.Apartment;
import com.example.myhome.model.Building;
import com.example.myhome.model.Owner;
import org.springframework.data.jpa.domain.Specification;
import javax.persistence.criteria.*;

public class ApartmentSpecifications {

    public static Specification<Apartment> numberContains(Long number) {
        return (root, query, builder) -> builder.equal(root.get("number"), number);
    }

    public static Specification<Apartment> buildingContains(String building) {
        return (root, query, builder) -> {
            Join<Apartment, Building> buildingJoin = root.join("building", JoinType.INNER);
            return builder.like(buildingJoin.get("name"), "%" + building + "%");
        };
    }

    public static Specification<Apartment> sectionContains(String section) {
        return (root, query, builder) -> builder.like(root.get("section"), "%" + section + "%");
    }

    public static Specification<Apartment> floorContains(String floor) {
        return (root, query, builder) -> builder.like(root.get("floor"), "%" + floor + "%");
    }

    public static Specification<Apartment> ownerContains(Long id) {
        return (root, query, builder) -> {
            Join<Apartment, Owner> ownerJoin = root.join("owner", JoinType.INNER);
            return builder.equal(ownerJoin.get("id"), id);
        };
    }

    public static Specification<Apartment> hasdebtContains() {
        return (root, query, builder) -> builder.lessThan(root.get("balance"), 0);
    }

    public static Specification<Apartment> hasNodebtContains() {
        return (root, query, builder) -> builder.greaterThanOrEqualTo(root.get("balance"), 0);
    }


}
