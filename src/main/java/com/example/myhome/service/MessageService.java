package com.example.myhome.service;

import com.example.myhome.model.Message;
import com.example.myhome.model.filter.FilterForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MessageService {
    Message findById(Long id);

    List<Message> findAll();

    Message save(Message message);

    void deleteById(Long id);

    Page<Message> findAllBySpecification(FilterForm form, Integer page, Integer size, Long ownerId);

    Page<Message> findAll(Pageable pageable);
}
