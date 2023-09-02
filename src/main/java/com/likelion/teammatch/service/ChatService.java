package com.likelion.teammatch.service;

import com.likelion.teammatch.dto.chat.ChatMessageDto;
import com.likelion.teammatch.entity.ChatMessage;
import com.likelion.teammatch.dto.chat.ChatRoomDto;
import com.likelion.teammatch.entity.ChatRoom;
import com.likelion.teammatch.repository.ChatMessageRepository;
import com.likelion.teammatch.repository.ChatRoomRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;


    public ChatService(ChatRoomRepository chatRoomRepository, ChatMessageRepository chatMessageRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatMessageRepository = chatMessageRepository;
    }

    //채팅방 생성
    public ChatRoomDto createChatRoom(ChatRoomDto chatRoomDto) {
        ChatRoom chatRoomEntity = new ChatRoom();
        chatRoomEntity.setRoomName(chatRoomDto.getRoomName());

        return ChatRoomDto.fromEntity(chatRoomRepository.save(chatRoomEntity));
    }

    //채팅방 전체 목록 조회
    public List<ChatRoomDto> getChatRooms() {
        List<ChatRoomDto> chatRoomList = new ArrayList<>();
        for (ChatRoom chatRoomEntity: chatRoomRepository.findAll())
            chatRoomList.add(ChatRoomDto.fromEntity(chatRoomEntity));
        Collections.reverse(chatRoomList);
        return chatRoomList;
    }

    //채팅방 조회
    public ChatRoomDto findRoomById(Long id) {
        Optional<ChatRoom> optionalChatRoom = chatRoomRepository.findById(id);
        if (optionalChatRoom.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return ChatRoomDto.fromEntity(optionalChatRoom.get());
    }

    public void saveChatMessage(ChatMessageDto chatMessageDto) {
        chatMessageRepository.save(chatMessageDto.newEntity());
    }

    //최근 메시지 10개
    public List<ChatMessageDto> getLast10Messages(Long roomId) {
        List<ChatMessageDto> chatMessages = new ArrayList<>();
        List<ChatMessage> chatMessageEntities = chatMessageRepository.findTop10ByRoomIdOrderByIdDesc(roomId);
        Collections.reverse(chatMessageEntities);
        for (ChatMessage messageEntity: chatMessageEntities) {
            chatMessages.add(ChatMessageDto.fromEntity(messageEntity));
        }
        return chatMessages;
    }

}