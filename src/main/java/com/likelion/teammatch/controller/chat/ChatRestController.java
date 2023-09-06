package com.likelion.teammatch.controller.chat;

import com.likelion.teammatch.dto.chat.ChatRoomDto;
import com.likelion.teammatch.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("chat")
@RequiredArgsConstructor
public class ChatRestController {
    private final ChatService chatService;

    @GetMapping("rooms")
    public ResponseEntity<List<ChatRoomDto>> getRooms(){
        return ResponseEntity.ok(chatService.getChatRooms());
    }

    @PostMapping("rooms")
    public ResponseEntity<ChatRoomDto> createRoom(@RequestBody ChatRoomDto dto){
        return ResponseEntity.ok(chatService.createChatRoom(dto));
    }

    @GetMapping("rooms/{id}/name")
    public ResponseEntity<ChatRoomDto> getRoomName(@PathVariable("id") Long roomId) {
        return ResponseEntity.ok(chatService.findRoomById(roomId));
    }

}
