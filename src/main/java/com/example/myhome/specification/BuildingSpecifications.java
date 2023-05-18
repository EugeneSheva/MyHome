package com.example.myhome.specification;


import com.example.myhome.model.Building;

import com.example.myhome.model.Building_;
import org.springframework.data.jpa.domain.Specification;


public class BuildingSpecifications {

    public static Specification<Building> hasId(Long id) {
        if(id == null) return (root, query, criteriaBuilder) -> null;
        return (root, query, cb) -> cb.equal(root.get(Building_.ID), id);
    }

    public static Specification<Building> hasAddressLike(String address) {
        if(address == null) return (root, query, criteriaBuilder) -> null;
        return (root, query, cb) -> cb.like(root.get(Building_.ADDRESS), "%"+address+"%");
    }

    public static Specification<Building> hasNameLike(String name) {
        if(name == null) return (root, query, criteriaBuilder) -> null;
        return (root, query, cb) -> cb.like(root.get(Building_.NAME), "%"+name+"%");
    }

}
