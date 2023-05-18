package com.example.myhome.repository;

import com.example.myhome.model.PageRoleDisplay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PageRoleDisplayRepository extends JpaRepository<PageRoleDisplay, Long> {
}
