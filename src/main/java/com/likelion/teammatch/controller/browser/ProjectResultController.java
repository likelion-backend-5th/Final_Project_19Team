package com.likelion.teammatch.controller.browser;

import com.likelion.teammatch.service.CommentService;
import com.likelion.teammatch.service.ProjectResultService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Slf4j
public class ProjectResultController {
    private final ProjectResultService projectResultService;
    private final CommentService commentService;

    public ProjectResultController(ProjectResultService projectResultService, CommentService commentService) {
        this.projectResultService = projectResultService;
        this.commentService = commentService;
    }

    // git hub 업로드

    // git hub 수정

    // 결과물에 댓글 달기
    @PostMapping("/projectResult/{projectResultId}/comment")
    public String createCommentForProjectResult(@PathVariable("projectResultId") Long projectResultId, @RequestParam String commentInput) {
        log.info("reached here");
        commentService.createComment(projectResultId, commentInput, false);

        return "redirect:/projectResult" + projectResultId;
    }
}
