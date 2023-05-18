package com.example.myhome.specification;


import com.example.myhome.model.*;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import java.time.LocalDateTime;

public class RequestSpecifications {

    public static Specification<RepairRequest> hasId(Long id) {
        if(id == null) return (root, query, criteriaBuilder) -> null;
        return (root, query, cb) -> cb.equal(root.get(RepairRequest_.ID), id);
    }

    public static Specification<RepairRequest> hasMasterType(UserRole type) {
        if(type == null) return (root, query, criteriaBuilder) -> null;
        return (root, query, cb) -> cb.equal(root.get(RepairRequest_.MASTER_TYPE), type);
    }

    public static Specification<RepairRequest> hasDescriptionLike(String description) {
        if(description == null) return (root, query, criteriaBuilder) -> null;
        return (root, query, cb) -> cb.like(root.get(RepairRequest_.DESCRIPTION), "%"+description+"%");
    }

    public static Specification<RepairRequest> hasApartment(Apartment apartment) {
        if(apartment == null) return (root, query, criteriaBuilder) -> null;
        return (root, query, cb) -> cb.equal(root.get(RepairRequest_.APARTMENT), apartment);
    }

    public static Specification<RepairRequest> hasOwner(Owner owner) {
        if(owner == null) return  (root, query, criteriaBuilder) -> null;
        return (root, query, cb) -> {
            Join<Apartment, RepairRequest> requestApartment = root.join("apartment");
            return cb.equal(requestApartment.get(Apartment_.OWNER), owner);
        };
    }

    public static Specification<RepairRequest> hasPhoneLike(String phone) {
        if(phone == null) return (root, query, criteriaBuilder) -> null;
        return (root, query, cb) -> {
            Join<Apartment, RepairRequest> requestApartment = root.join("apartment");
            Join<Owner, Apartment> apartmentOwner = requestApartment.join("owner");
            return cb.like(apartmentOwner.get(Owner_.PHONE_NUMBER), "%"+phone+"%");
        };
    }

    public static Specification<RepairRequest> hasMaster(Admin master) {
        if(master == null) return (root, query, criteriaBuilder) -> null;
        return (root, query, cb) -> cb.equal(root.get(RepairRequest_.MASTER), master);
    }

    public static Specification<RepairRequest> hasStatus(RepairStatus status) {
        if(status == null) return (root, query, criteriaBuilder) -> null;
        return (root, query, cb) -> cb.equal(root.get(RepairRequest_.STATUS), status);
    }

    public static Specification<RepairRequest> datesBetween(LocalDateTime from, LocalDateTime to) {
        if(from == null || to == null) return (root, query, criteriaBuilder) -> null;
        return (root, query, cb) -> cb.between(root.get(RepairRequest_.BEST_TIME_REQUEST), from, to);
    }


}
