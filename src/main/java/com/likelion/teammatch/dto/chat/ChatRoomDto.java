package com.likelion.teammatch.dto.chat;

import com.likelion.teammatch.entity.ChatRoom;
import com.likelion.teammatch.entity.User;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomDto {
    private Long id;
    private String roomName;

    public static ChatRoomDto fromEntity(ChatRoom chatRoom) {
        return new ChatRoomDto(
                chatRoom.getId(),
                chatRoom.getRoomName()
        );
    }
}