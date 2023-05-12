package com.example.myhome.home.specification;

import com.example.myhome.home.model.Admin;
import com.example.myhome.home.model.Admin_;
import com.example.myhome.util.UserRole;
import com.example.myhome.util.UserStatus;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

public class AdminSpecifications {

    public static Specification<Admin> hasNameLike(String s) {
        if(s == null) return (root, query, criteriaBuilder) -> null;
        return (root, query, cb) -> cb.like(root.get(Admin_.FULL_NAME), "%"+s+"%");
    }

    public static Specification<Admin> hasRole(String role) {
        if(role == null) return (root, query, criteriaBuilder) -> null;
        return (root, query, cb) -> {
            Join<Admin, UserRole> adminRoleJoin = root.join("role", JoinType.INNER);
            return    cb.like(adminRoleJoin.get("name"), role);
        };
    }


//    public static Specification<Admin> hasRole(UserRole role) {
//        if(role == null) return (root, query, criteriaBuilder) -> null;
//        return (root, query, cb) -> cb.equal(root.get(Admin_.ROLE), role);
//    }

    public static Specification<Admin> hasPhoneLike(String s) {
        if(s == null) return (root, query, criteriaBuilder) -> null;
        return (root, query, cb) -> cb.like(root.get(Admin_.PHONE_NUMBER), "%"+s+"%");
    }

    public static Specification<Admin> hasEmailLike(String s) {
        if(s == null) return (root, query, criteriaBuilder) -> null;
        return (root, query, cb) -> cb.like(root.get(Admin_.EMAIL), "%"+s+"%");
    }

    public static Specification<Admin> isActive(Boolean active) {
        if(active == null) return (root, query, criteriaBuilder) -> null;
        return (root, query, cb) -> cb.equal(root.get(Admin_.ACTIVE), active);
    }

}
