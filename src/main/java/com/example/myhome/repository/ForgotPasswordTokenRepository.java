package com.example.myhome.repository;

import com.example.myhome.model.ForgotPasswordToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ForgotPasswordTokenRepository extends JpaRepository<ForgotPasswordToken, Long> {
    boolean existsByToken(String token);
    Optional<ForgotPasswordToken> findByToken(String token);
    void deleteByToken(String token);
}
