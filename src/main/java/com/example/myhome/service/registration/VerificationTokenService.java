package com.example.myhome.service.registration;

import com.example.myhome.model.Owner;
import com.example.myhome.repository.OwnerRepository;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
public class VerificationTokenService {

    @Autowired private VerificationTokenRepository repository;
    @Autowired private OwnerRepository ownerRepository;

    public VerificationToken createToken(Owner owner) {
        String tokenUUID = UUID.randomUUID().toString();
        VerificationToken token = new VerificationToken(tokenUUID,owner);
        return repository.save(token);
    }

    public boolean tokenExists(String token) {
        return repository.existsByToken(token);
    }

    public VerificationToken getToken(String token) {
        return repository.findByToken(token).orElse(null);
    }

    public void deleteToken(VerificationToken token) {
        repository.delete(token);
    }

    @Transactional
    public void enableOwner(VerificationToken token) {
        Owner tokenOwner = token.getOwner();
        tokenOwner.setEnabled(true);
        ownerRepository.save(tokenOwner);
        log.info(tokenOwner.toString());
        deleteToken(token);
    }
}
