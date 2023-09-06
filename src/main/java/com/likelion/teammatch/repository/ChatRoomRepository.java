package com.likelion.teammatch.repository;

import com.likelion.teammatch.entity.ChatRoom;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    // TeamId에 해당하는 모든 채팅방 삭제
    @Transactional
    void deleteAllByTeamId(Long teamId);
}
