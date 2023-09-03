package com.likelion.teammatch.socket;


import com.likelion.teammatch.service.ChatService;
import com.likelion.teammatch.dto.chat.ChatMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WebSocketMapping {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ChatService chatService;

    @MessageMapping("/chat")
    public void sendChat(
            @Payload ChatMessageDto chatMessageDto,
            @Headers Map<String, Objects> headers,
            @Header("nativeHeaders") Map<String, String> nativeHeaders
    ){
        log.info(chatMessageDto.toString());
        log.info(headers.toString());
        log.info(nativeHeaders.toString());

        String time = new SimpleDateFormat("HH:mm").format(new Date());
        chatMessageDto.setTime(time);

        chatService.saveChatMessage(chatMessageDto);
        simpMessagingTemplate.convertAndSend(
                String.format("/topic/%s", chatMessageDto.getRoomId()),
                chatMessageDto
        );
    }

    @SubscribeMapping("/{roomId}")
    public List<ChatMessageDto> sendGreet(@DestinationVariable("roomId") Long roomId) {
        log.info("new subscription to {}", roomId);
        List<ChatMessageDto> last10Messages = chatService.getLast10Messages(roomId);
        return last10Messages;
    }
}