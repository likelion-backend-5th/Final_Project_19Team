package com.likelion.teammatch.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "chat_room")
@Data
public class ChatRoom extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String roomName;
}
