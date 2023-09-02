package com.likelion.teammatch.dto.chat;

import com.likelion.teammatch.entity.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {
    private Long roomId;
    private String sender;
    private String message;
    private String time;

    public static ChatMessageDto fromEntity(ChatMessage messageEntity) {
        return new ChatMessageDto(
                messageEntity.getRoomId(),
                messageEntity.getSender(),
                messageEntity.getMessage(),
                messageEntity.getTime()
        );
    }

    public ChatMessage newEntity() {
        ChatMessage messageEntity = new ChatMessage();
        messageEntity.setRoomId(roomId);
        messageEntity.setSender(sender);
        messageEntity.setMessage(message);
        messageEntity.setTime(time);
        return messageEntity;
    }
}