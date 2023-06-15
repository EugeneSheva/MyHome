package com.example.myhome.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

// --- СООБЩЕНИЯ ---

@Data
@Entity
@Table(name = "message")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //отправитель

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private Admin sender;

    //получатель(-и)
    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "messages_users",
            joinColumns = @JoinColumn(name = "message_id"),
            inverseJoinColumns = @JoinColumn(name = "owner_id"))
    private List<Owner> receivers;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "unread_messages_users",
            joinColumns = @JoinColumn(name = "message_id"),
            inverseJoinColumns = @JoinColumn(name = "owner_id"))
    private List<Owner> unreadReceivers;

    private String receiversName;
    //Тема сообщения
    private String subject;

    //Содержание
    @Column(columnDefinition = "TEXT")
    private String text;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime date;

    @Override

    public String toString() {
        return "Message{" +
                "id=" + id +
                ", subject='" + subject + '\'' +
                ", text='" + text + '\'' +
                ", date=" + date +
                '}';
    }
}
