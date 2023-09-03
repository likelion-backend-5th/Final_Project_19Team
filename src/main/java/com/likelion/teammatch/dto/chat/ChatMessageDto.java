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

    public static ChatMessageDto fromEntity(ChatMessage chatMessage) {
        return new ChatMessageDto(
                chatMessage.getRoomId(),
                chatMessage.getSender(),
                chatMessage.getMessage(),
                chatMessage.getTime()
        );
    }

    public ChatMessage newEntity() {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setRoomId(roomId);
        chatMessage.setSender(sender);
        chatMessage.setMessage(message);
        chatMessage.setTime(time);
        return chatMessage;
    }
}