package com.likelion.teammatch.controller.chat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class ChatController {

    // 테스트용
    @GetMapping("chat")
    public String index(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        model.addAttribute("username", username);
        return "/html/Team_page";
    }

    @GetMapping("team/{roomId}/chatroom")   // roomId는 teamId
    public String enterRoom(){
        return "/html/chat-room";
    }
}
