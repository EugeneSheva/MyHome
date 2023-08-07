package com.example.myhome.service.impl;

import com.example.myhome.service.EmailService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Objects;
import java.util.Properties;

@Service
@Log
@PropertySource("classpath:mail.properties")
public class EmailServiceImpl implements EmailService {

    @Autowired
    Environment env;

    @Value("${spring.mail.username}")
    private String EMAIL_SENDER;

    @Value("${spring.mail.password}")
    private String EMAIL_PASS;

    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void send(String recipientAddress, String emailContent) {
        try {
            log.info("Trying to send email to " + recipientAddress);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setText(emailContent, true);
            helper.setTo(recipientAddress);
            helper.setFrom(Objects.requireNonNull(env.getProperty("spring.mail.username")));
            log.info("Message created, sending...");
            mailSender.send(message);

        } catch (MessagingException e) {
            log.severe("fail to send email, msg: ");
            log.severe(e.getMessage());
        }
    }

    public void sendWithAttachment(String recipientAddress, String fileName) {

        try {
            log.info("Trying to send email to " + recipientAddress);
            MimeMessage message = mailSender.createMimeMessage();
            message.setFrom(new InternetAddress(EMAIL_SENDER));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipientAddress));
            message.setSubject("Invoice");
            BodyPart messageBodyPartText = new MimeBodyPart();
            messageBodyPartText.setText("Sent invoice");
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPartText);
            BodyPart messageBodyPartFile = new MimeBodyPart();
            DataSource source = new FileDataSource(fileName);
            messageBodyPartFile.setDataHandler(new DataHandler(source));
            messageBodyPartFile.setFileName(fileName);
            multipart.addBodyPart(messageBodyPartFile);

            message.setContent(multipart);
            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
