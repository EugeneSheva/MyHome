package com.example.myhome.specification;


import com.example.myhome.model.*;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;

public class AccountSpecifications {

    public static Specification<ApartmentAccount> hasId(Long id) {
        if(id == null) return (root, query, criteriaBuilder) -> null;
        return (root, query, cb) -> cb.equal(root.get(ApartmentAccount_.ID), id);
    }

    public static Specification<ApartmentAccount> isActive(Boolean isActive) {
        if(isActive == null) return (root, query, criteriaBuilder) -> null;
        return (root, query, cb) -> cb.equal(root.get(ApartmentAccount_.IS_ACTIVE), isActive);
    }

    public static Specification<ApartmentAccount> hasApartment(Apartment apartment) {
        if(apartment == null) return (root, query, criteriaBuilder) -> null;
        else return (root, query, cb) -> cb.equal(root.get(ApartmentAccount_.APARTMENT), apartment);
    }

    public static Specification<ApartmentAccount> hasApartmentNumber(Long number) {
        if(number == null) return (root, query, criteriaBuilder) -> null;
        return (root, query, cb) -> {
            Join<Apartment, ApartmentAccount> accountApartment = root.join("apartment");
            return cb.equal(accountApartment.get(Apartment_.NUMBER), number);
        };
    }

    public static Specification<ApartmentAccount> hasBuilding(Building building) {
        if(building == null) return (root, query, criteriaBuilder) -> null;
        else return (root, query, cb) -> {
            Join<Apartment, ApartmentAccount> accountApartment = root.join("apartment");
            return cb.equal(accountApartment.get(Apartment_.BUILDING), building);
        };
    }

    public static Specification<ApartmentAccount> hasSection(String section) {
        if(section == null) return (root, query, criteriaBuilder) -> null;
        else return (root, query, cb) -> {
            Join<Apartment, ApartmentAccount> accountApartment = root.join("apartment");
            return cb.equal(accountApartment.get(Apartment_.SECTION), section);
        };
    }

    public static Specification<ApartmentAccount> hasOwner(Owner owner) {
        if(owner == null) return  (root, query, criteriaBuilder) -> null;
        return (root, query, cb) -> {
            Join<Apartment, ApartmentAccount> accountApartment = root.join("apartment");
            return cb.equal(accountApartment.get(Apartment_.OWNER), owner);
        };
    }

    public static Specification<ApartmentAccount> hasDebt(Boolean hasDebt) {
        if(hasDebt == null) return (root, query, criteriaBuilder) -> null;
        return (root, query, cb) -> hasDebt ? cb.lessThan(root.get(ApartmentAccount_.BALANCE), 0) : cb.greaterThanOrEqualTo(root.get(ApartmentAccount_.BALANCE), 0);
    }

}
