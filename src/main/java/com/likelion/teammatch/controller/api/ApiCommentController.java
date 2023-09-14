package com.likelion.teammatch.controller.api;

import com.likelion.teammatch.service.CommentService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api")
public class ApiCommentController {
    private final CommentService commentService;

    public ApiCommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/recruit/{recruitId}/comment/{commentId}")
    public boolean editComment(@PathVariable("commentId") Long commentId, @RequestBody String content){
        commentService.updateComment(commentId, content);
        return true;
    }

    @Transactional
    @DeleteMapping("/recruit/{recruitId}/comment/{commentId}")
    public boolean deleteComment(@PathVariable("commentId") Long commentId){
        commentService.deleteComment(commentId);
        return true;
    }
}
