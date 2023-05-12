package com.example.myhome.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;

@TestConfiguration
public class TestConfig {

    @Bean
    public MessageSource messageSource() {
        return new ResourceBundleMessageSource();
    }

}
