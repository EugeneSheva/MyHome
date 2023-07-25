package com.example.myhome.service;

import org.springframework.scheduling.annotation.Async;

public interface EmailService {

    void send(String recipientAddress, String emailContent);
    void sendWithAttachment(String recipientAddress, String fileName);
}
