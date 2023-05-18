package com.example.myhome.service.impl;

import com.example.myhome.exception.NotFoundException;
import com.example.myhome.model.filter.FilterForm;
import com.example.myhome.repository.MessageRepository;
import com.example.myhome.service.MessageService;
import com.example.myhome.model.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    @Value("${upload.path}")
    private String uploadPath;
    private final MessageRepository messageRepository;


    @Override
    public Message findById(Long id) { return messageRepository.findById(id).orElseThrow(() -> new NotFoundException());}

    @Override
    public List<Message> findAll() { return messageRepository.findAll(); }

    @Override
    public Message save(Message message) { return messageRepository.save(message); }

    @Override
    public void deleteById(Long id) { messageRepository.deleteById(id); }


    @Override
    public Page<Message> findAllBySpecification(FilterForm form, Integer page, Integer size, Long ownerId) {
        System.out.println("service");
        Pageable pageable = PageRequest.of(page-1, size);
        Page<Message>messageList = messageRepository.findByFilters(form.getDescription(), ownerId, pageable);
        System.out.println("messageList" +messageList);
        return messageList;
    }

    @Override
    public Page<Message> findAll(Pageable pageable) {
        return messageRepository.findAll(pageable);
    }
}
