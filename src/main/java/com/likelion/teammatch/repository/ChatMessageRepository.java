package com.likelion.teammatch.repository;

import com.likelion.teammatch.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    public List<ChatMessage> findTop10ByRoomIdOrderByIdDesc(Long id);

}
