package com.example.myhome.service.registration;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    boolean existsByToken(String token);
    Optional<VerificationToken> findByToken(String token);
    void deleteByToken(String token);
}
