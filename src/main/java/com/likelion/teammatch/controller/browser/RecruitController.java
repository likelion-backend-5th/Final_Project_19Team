package com.likelion.teammatch.controller.browser;

import com.likelion.teammatch.dto.CreateRecruitDto;
import com.likelion.teammatch.dto.RecruitInfoDto;
import com.likelion.teammatch.service.CommentService;
import com.likelion.teammatch.service.RecruitService;
import com.likelion.teammatch.service.team.TeamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;


@Controller
@Slf4j
public class RecruitController {

    private final TeamService teamService;
    private final RecruitService recruitService;
    private final CommentService commentService;

    public RecruitController(TeamService teamService, RecruitService recruitService, CommentService commentService) {
        this.teamService = teamService;
        this.recruitService = recruitService;
        this.commentService = commentService;
    }

    @GetMapping("/recruit/{recruitId}")
    public String getRecruitInfo(@PathVariable("recruitId") Long recruitId, Model model){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        RecruitInfoDto recruitDto = recruitService.getRecruitInfo(recruitId);

        model.addAttribute("recruit", recruitDto);
        model.addAttribute("comments", commentService.getCommentsForRecruit(recruitId));
        model.addAttribute("isManager", username.equals(recruitDto.getTeamManagerUsername()));
        model.addAttribute("isFinished", recruitDto.getIsRecruitFinished());
        model.addAttribute("alreadyApplied", false);
        return "/html/detail_recruiting";
    }

    @PostMapping("/recruit/{recruitId}/comment")
    public String createCommentForRecruit(@PathVariable("recruitId") Long recruitId, @RequestParam String commentInput){
        log.info("reached here");
        commentService.createComment(recruitId, commentInput, true);

        return "redirect:/recruit/" + recruitId;
    }

    @GetMapping("/recruit/{recruitId}/edit")
    public String getUpdateRecruit(@PathVariable("recruitId") Long recruitId, Model model){
        RecruitInfoDto dto = recruitService.getRecruitInfo(recruitId);

        model.addAttribute("teamName", dto.getTeamName());
        model.addAttribute("teamRecruitName", dto.getRecruitTitle());
        model.addAttribute("teamRecruitDetails", dto.getTeamRecruitDetails());

        return "/html/create_recruit";
    }

    @PostMapping("/recruit/{recruitId}/edit")
    public String updateRecruit(@PathVariable("recruitId") Long recruitId, CreateRecruitDto dto){
        recruitService.updateRecruit(recruitId, dto.getTeamRecruitName(), dto.getMemberNum(),dto.getTeamRecruitDetails());

        return "redirect:/recruit/" + recruitId;
    }

    @PostMapping("/team/{teamId}/recruit")
    public String createRecruit(@PathVariable("teamId") Long teamId){
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }

}
