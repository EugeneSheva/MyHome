package com.example.myhome.service.impl;

import com.example.myhome.service.EmailService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Objects;

@Service
@Log
@PropertySource("classpath:mail.properties")
public class EmailServiceImpl implements EmailService {

    @Autowired
    Environment env;

    @Value("${spring.mail.username}")
    private String EMAIL_SENDER;

    @Autowired
    private JavaMailSender mailSender;

    public void send(String to, String email) {
        try {
            log.info("Trying to send email to " + to);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setText(email, true);
            helper.setTo(to);
            helper.setFrom(Objects.requireNonNull(env.getProperty("spring.mail.username")));
            log.info("Message created, sending...");
            mailSender.send(message);

        } catch (MessagingException e) {
            log.severe("fail to send email, msg: ");
            log.severe(e.getMessage());
        }
    }
}
