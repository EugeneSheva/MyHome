package com.example.myhome.service.registration;

import com.example.myhome.service.EmailService;
import com.example.myhome.model.Owner;
import com.example.myhome.repository.OwnerRepository;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ThreadPoolExecutor;

@Service
@Log
public class RegisterService {

    @Autowired private OwnerRepository ownerRepository;
    @Autowired private VerificationTokenService verificationTokenService;

    @Autowired private PasswordEncoder passwordEncoder;

    @Autowired private JavaMailSender mailSender;
    @Autowired private EmailService emailService;

    @Autowired private ThreadPoolExecutor executor;

    public void register(RegistrationRequest request) {

        log.info(passwordEncoder.toString());

        Owner owner = new Owner(request);
        String encoded_pass = passwordEncoder.encode(owner.getPassword());
        log.info(encoded_pass);
        owner.setPassword(encoded_pass);
        Owner savedOwner = ownerRepository.save(owner);

        VerificationToken savedToken = verificationTokenService.createToken(savedOwner);

        // отправка e-mail

        String html_template =
                "<a href='http://localhost:8890/cabinet/site/register/confirm?token="
                        +savedToken.getToken()+
                        "'>Press to confirm registration!</a>";

        executor.execute(() -> emailService.send(savedOwner.getEmail(), html_template));

    }

    @Transactional
    public boolean confirm (String token) {
        if(verificationTokenService.tokenExists(token)) {
            VerificationToken foundToken = verificationTokenService.getToken(token);
            if(foundToken != null) {
                if(foundToken.isValid()) {
                    log.info("Token valid");
                    verificationTokenService.enableOwner(foundToken);
                    return true;
                } else {
                    log.info("Token expired!");
                    verificationTokenService.deleteToken(foundToken);
                    return false;
                }
            } else {
                log.info("Token not found in DB for some reason");
                return false;
            }
        } else {
            log.info("Token doesn't exist in DB!");
            return false;
        }
    }

}
