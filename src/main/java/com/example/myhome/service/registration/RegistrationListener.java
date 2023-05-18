package com.example.myhome.service.registration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;

public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {

//    @Autowired
//    private IUserService service;

    @Autowired
    private MessageSource source;

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {

    }

//    private void confirmRegistration(OnRegistrationCompleteEvent event) {
//        UserDetails user = event.getUserDetails();
//        String token = UUID.randomUUID().toString();
//        service.createVerificationToken(user, token);
//
//        String recipientAddress = user.getEmail();
//        String subject = "Registration Confirmation";
//        String confirmationUrl
//                = event.getAppUrl() + "/regitrationConfirm?token=" + token;
//        String message = messages.getMessage("message.regSucc", null, event.getLocale());
//
//        SimpleMailMessage email = new SimpleMailMessage();
//        email.setTo(recipientAddress);
//        email.setSubject(subject);
//        email.setText(message + "\r\n" + "http://localhost:8080" + confirmationUrl);
//        mailSender.send(email);
//    }
}
