package com.likelion.teammatch.controller.browser;

import com.likelion.teammatch.dto.RecruitDraftDto;
import com.likelion.teammatch.dto.RecruitInfoDto;
import com.likelion.teammatch.dto.team.TeamCreateDto;
import com.likelion.teammatch.service.CommentService;
import com.likelion.teammatch.service.RecruitService;
import com.likelion.teammatch.service.team.TeamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Slf4j
public class MainController {

    private final TeamService teamService;
    private final RecruitService recruitService;
    private final CommentService commentService;

    public MainController(TeamService teamService, RecruitService recruitService, CommentService commentService) {
        this.teamService = teamService;
        this.recruitService = recruitService;
        this.commentService = commentService;
    }

    @GetMapping("/main")
    public String getMainPage(
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            Model model
    ){
        List<RecruitDraftDto> recruitInfoList = recruitService.getRecruitDraftList(page);
        model.addAttribute("recruitList", recruitInfoList);
        if (SecurityContextHolder.getContext().getAuthentication().getName().equals("anonymousUser")) model.addAttribute("logined", false);
        else model.addAttribute("logined", true);

        return "/html/index";
    }

    @GetMapping("/createTeam")
    public String getCreateTeamForm(Model model){
        return "/html/create";
    }

    @PostMapping("/createTeam")
    public String createTeam(TeamCreateDto dto){
        Long teamId = teamService.createTeam(dto);

        return "redirect:/main";
    }

    @GetMapping("/team/{teamId}")
    public String getTeamInfo(@PathVariable("teamId") Long teamId){
        return "redirect:/main";//todo 임시로 main으로 보냄.
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



}
