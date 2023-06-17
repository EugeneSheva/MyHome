package com.example.myhome.service;

public interface EmailService {

    void send(String to, String email);
    void sendWithAttachment(String to, String fileName);
}
