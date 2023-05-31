package com.example.myhome.services;

import com.example.myhome.exception.NotFoundException;
import com.example.myhome.service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.internet.MimeMessage;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@SpringBootTest
public class EmailServiceTest {

    @Autowired
    EmailService emailService;

    @MockBean
    JavaMailSender mailSender;

    @Test
    void contextLoads() {

    }

    @Test
    void sendTest() {
        emailService.send("test", "test");
    }

    @Test
    void failSendTest() {
        doThrow(new NotFoundException()).when(mailSender).send((MimeMessage) any());
        emailService.send("test", "test");
    }
}
