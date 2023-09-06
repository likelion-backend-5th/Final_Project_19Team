package com.likelion.teammatch.controller.chat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
@RequestMapping("chat")
public class ChatController {

    @GetMapping
    public String index(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        model.addAttribute("username", username);
        return "/html/chat-lobby";
    }

    @GetMapping("/{roomId}/{userId}")
    public String enterRoom(){
        return "/html/chat-room";
    }
}
