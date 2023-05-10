package com.example.myhome.home.service.registration;

import com.example.myhome.home.model.Owner;
import com.example.myhome.home.repository.OwnerRepository;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Log
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
