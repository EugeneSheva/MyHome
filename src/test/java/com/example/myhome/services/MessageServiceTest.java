package com.example.myhome.services;

import com.example.myhome.model.Admin;
import com.example.myhome.model.Message;
import com.example.myhome.model.Owner;
import com.example.myhome.model.filter.FilterForm;
import com.example.myhome.repository.MessageRepository;
import com.example.myhome.service.MessageService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest
public class MessageServiceTest {

    @Autowired
    private MessageService service;

    @MockBean private MessageRepository repository;

    static Message message;
    static List<Message> messageList;
    static Page<Message> messagePage;

    @BeforeAll
    static void setupObjects() {
        message = new Message();
        message.setId(1L);
        message.setReceivers(List.of(new Owner(), new Owner()));
        message.setDate(LocalDateTime.now());
        message.setSender(new Admin());
        message.setText("test");
        message.setSubject("test");

        messageList = List.of(message, message, message);
        messagePage = new PageImpl<>(messageList, PageRequest.of(1,1), 1);
    }

    @BeforeEach
    void setupMocks() {
        when(repository.findById(anyLong())).thenReturn(Optional.ofNullable(message));
        when(repository.findAll()).thenReturn(messageList);
        when(repository.save(any(Message.class))).thenReturn(message);
        when(repository.findAll(any(Pageable.class))).thenReturn(messagePage);
        when(repository.findByFilters(anyString(), anyLong(), any(Pageable.class))).thenReturn(messagePage);
    }

    @Test
    void contextLoads() {

    }

    @Test
    void findByIdTest() {
        assertThat(service.findById(message.getId())).isEqualTo(message);
    }

    @Test
    void findAllTest() {
        assertThat(service.findAll()).isEqualTo(messageList);
    }

    @Test
    void saveTest() {
        assertThat(service.save(message)).isEqualTo(message);
    }

    @Test
    void deleteByIdTest() {
        service.deleteById(message.getId());
    }

    @Test
    void findAllBySpecificationTest() {
        assertThat(service.findAllBySpecification(new FilterForm(), 1,1,1L))
                .isEqualTo(messagePage);
    }

    @Test
    void findAllPageTest() {
        assertThat(service.findAll(PageRequest.of(1,1))).isEqualTo(messagePage);
    }


}
