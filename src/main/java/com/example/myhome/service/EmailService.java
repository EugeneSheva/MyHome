package com.example.myhome.service;

import org.springframework.scheduling.annotation.Async;

public interface EmailService {

    @Async
    void send(String recipientAddress, String emailContent);
    @Async
    void sendWithAttachment(String recipientAddress, String fileName);
}
