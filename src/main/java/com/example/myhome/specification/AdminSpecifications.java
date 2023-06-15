package com.example.myhome.specification;

import com.example.myhome.model.Admin;

import com.example.myhome.model.Admin_;
import com.example.myhome.model.UserRole;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

public class AdminSpecifications {

    public static Specification<Admin> hasNameLike(String s) {
        if(s == null) return (root, query, criteriaBuilder) -> null;
        return (root, query, cb) -> cb.or(cb.like(root.get("first_name"), "%"+s+"%"),
                                          cb.like(root.get("last_name"), "%"+s+"%"));
    }

    public static Specification<Admin> hasRole(Long role) {
        if(role == null) return (root, query, criteriaBuilder) -> null;
        return (root, query, cb) -> {
            Join<Admin, UserRole> adminRoleJoin = root.join("role", JoinType.INNER);
            return cb.equal(adminRoleJoin.get("id"), role);
        };
    }

    public static Specification<Admin> hasRole(String role) {
        if(role == null) return (root, query, criteriaBuilder) -> null;
        return (root, query, cb) -> {
            Join<Admin, UserRole> adminRoleJoin = root.join("role", JoinType.INNER);
            return cb.equal(adminRoleJoin.get("name"), role);
        };
    }

//    public static Specification<Admin> hasRole(UserRole role) {
//        if(role == null) return (root, query, criteriaBuilder) -> null;
//        return (root, query, cb) -> cb.equal(root.get(Admin_.ROLE), role);
//    }

    public static Specification<Admin> hasPhoneLike(String s) {
        if(s == null) return (root, query, criteriaBuilder) -> null;
        return (root, query, cb) -> cb.like(root.get("phone_number"), "%"+s+"%");
    }

    public static Specification<Admin> hasEmailLike(String s) {
        if(s == null) return (root, query, criteriaBuilder) -> null;
        return (root, query, cb) -> cb.like(root.get("email"), "%"+s+"%");
    }

    public static Specification<Admin> isActive(Boolean active) {
        if(active == null) return (root, query, criteriaBuilder) -> null;
        return (root, query, cb) -> cb.equal(root.get("active"), active);
    }

}
