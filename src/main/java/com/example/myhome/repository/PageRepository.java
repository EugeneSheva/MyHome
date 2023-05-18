package com.example.myhome.repository;

import com.example.myhome.model.pages.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PageRepository extends JpaRepository<Page, Long> {

    @Query("SELECT p FROM MainPage p WHERE p.id=1")
    Optional<MainPage> getMainPage();
    @Query("SELECT p FROM AboutPage p WHERE p.id=1")
    Optional<AboutPage> getAboutPage();
    @Query("SELECT p FROM ContactsPage p WHERE p.id=1")
    Optional<ContactsPage> getContactsPage();
    @Query("SELECT p FROM ServicesPage p WHERE p.id=1")
    Optional<ServicesPage> getServicesPage();

}
