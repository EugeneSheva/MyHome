package com.example.myhome.specification;


import com.example.myhome.model.*;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.time.LocalDate;

public class MeterSpecifications {

    public static Specification<MeterData> groupTest() {
        return (root, query, criteriaBuilder) -> {
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<MeterData> subRoot = subquery.from(MeterData.class);
            subquery.groupBy(subRoot.get(MeterData_.APARTMENT), subRoot.get(MeterData_.SERVICE));
            subquery.select(criteriaBuilder.max(subRoot.get(MeterData_.ID)));

            return root.get(MeterData_.ID).in(subquery);
        };
    }

    public static Specification<MeterData> hasId(Long id) {
        if(id == null) return (root, query, criteriaBuilder) -> null;
        return (root, query, cb) -> cb.equal(root.get(MeterData_.ID), id);
    }

    public static Specification<MeterData> hasStatus(MeterPaymentStatus status) {
        if(status == null) return (root, query, criteriaBuilder) -> null;
        return (root, query, cb) -> cb.equal(root.get(MeterData_.STATUS), status);
    }

    public static Specification<MeterData> datesBetween(LocalDate from, LocalDate to) {
        if(from == null || to == null) return (root, query, criteriaBuilder) -> null;
        return (root, query, cb) -> cb.between(root.get(MeterData_.DATE), from, to);
    }


    public static Specification<MeterData> hasBuilding(Building building) {
        if(building == null) return (root, query, criteriaBuilder) -> null;
        else return (root, query, cb) -> {
            Join<Apartment, MeterData> meterApartment = root.join("apartment");
            return cb.equal(meterApartment.get(Apartment_.BUILDING), building);
        };
    }

    public static Specification<MeterData> hasSection(String section) {
        if(section == null) return (root, query, criteriaBuilder) -> null;
        else return (root, query, cb) -> {
            Join<Apartment, MeterData> meterApartment = root.join("apartment");
            return cb.equal(meterApartment.get(Apartment_.SECTION), section);
        };
    }

    public static Specification<MeterData> hasApartment(Apartment apartment) {
        if(apartment == null) return (root, query, criteriaBuilder) -> null;
        else return (root, query, cb) -> cb.equal(root.get(MeterData_.APARTMENT), apartment);
    }

    public static Specification<MeterData> hasApartmentNumber(Long number) {
        if(number == null) return (root, query, criteriaBuilder) -> null;
        return (root, query, cb) -> {
            Join<Apartment, MeterData> meterApartment = root.join("apartment");
            return cb.equal(meterApartment.get(Apartment_.NUMBER), number);
        };
    }

    public static Specification<MeterData> hasService(Service service) {
        if(service == null) return (root, query, criteriaBuilder) -> null;
        else return (root, query, cb) -> cb.equal(root.get(MeterData_.SERVICE), service);
    }

}
