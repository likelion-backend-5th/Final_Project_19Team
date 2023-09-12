package com.likelion.teammatch.controller.browser;

import com.likelion.teammatch.dto.CreateRecruitDto;
import com.likelion.teammatch.dto.RecruitInfoDto;
import com.likelion.teammatch.dto.team.TeamInfoDto;
import com.likelion.teammatch.service.CommentService;
import com.likelion.teammatch.service.RecruitService;
import com.likelion.teammatch.service.team.TeamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


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

    //모집 공고 상세정보 보기
    @GetMapping("/recruit/{recruitId}")
    public String getRecruitInfo(@PathVariable("recruitId") Long recruitId, Model model){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        RecruitInfoDto recruitDto = recruitService.getRecruitInfo(recruitId);

        model.addAttribute("recruit", recruitDto);
        model.addAttribute("comments", commentService.getCommentsForRecruit(recruitId));
        model.addAttribute("isManager", username.equals(recruitDto.getTeamManagerUsername()));
        model.addAttribute("isFinished", recruitDto.getIsRecruitFinished());
        model.addAttribute("applyList", recruitService.getApplyListForRecruit(recruitId));
        return "html/detail_recruiting";
    }

    //모집 공고 댓글 달기
    @PostMapping("/recruit/{recruitId}/comment")
    public String createCommentForRecruit(@PathVariable("recruitId") Long recruitId, @RequestParam String commentInput){
        log.info("reached here");
        commentService.createComment(recruitId, commentInput, true);

        return "redirect:/recruit/" + recruitId;
    }

    //모집 공고 수정 form 가져오기
    @GetMapping("/recruit/{recruitId}/edit")
    public String getUpdateRecruit(@PathVariable("recruitId") Long recruitId, Model model){
        RecruitInfoDto dto = recruitService.getRecruitInfo(recruitId);

        model.addAttribute("isEdit", true);
        model.addAttribute("recruitId", recruitId);
        model.addAttribute("teamName", dto.getTeamName());
        model.addAttribute("teamRecruitName", dto.getRecruitTitle());
        model.addAttribute("teamRecruitDetails", dto.getTeamRecruitDetails());
        model.addAttribute("memberNum", dto.getRecruitMemberNum());

        return "html/create_recruit";
    }

    //모집 공고 수정하기
    @PostMapping("/recruit/{recruitId}/edit")
    public String updateRecruit(@PathVariable("recruitId") Long recruitId, CreateRecruitDto dto){
        recruitService.updateRecruit(recruitId, dto.getTeamRecruitName(), dto.getMemberNum(),dto.getTeamRecruitDetails(), dto.getTechStackWanted());

        return "redirect:/recruit/" + recruitId;
    }

    //모집공고 추가 폼 가져오기
    @GetMapping("/team/{teamId}/recruit")
    public String getCreateRecruitForm(@PathVariable("teamId") Long teamId, Model model){
        TeamInfoDto teamInfo = teamService.getTeamInfo(teamId);
        model.addAttribute("isEdit", false);
        model.addAttribute("teamId", teamId);
        model.addAttribute("teamName", teamInfo.getTeamName());
        return "html/create_recruit";
    }
    //팀에 모집 공고 추가하기
    @PostMapping("/team/{teamId}/recruit")
    public String createRecruit(@PathVariable("teamId") Long teamId, CreateRecruitDto dto){
        Long recruitId = recruitService.createRecruit(teamId, dto.getTeamRecruitName(), dto.getMemberNum(), dto.getTeamRecruitDetails(), dto.getTechStackWanted());
        return "redirect:/recruit/" + recruitId;
    }

    //모집 공고 신청하기
    @PostMapping("/recruit/{recruitId}/apply")
    public String applyToRecruit(@PathVariable("recruitId") Long recruitId, @RequestBody String introduction){
        recruitService.applyToRecruit(recruitId, introduction);
        return "redirect:/recruit/" + recruitId;

    }
    
    //모집 공고 수락하기
    //모집 공고 거절하기
    @PostMapping("/recruit/{recruitId}/apply/{applyId}")
    public String acceptOrDenyApply(@PathVariable("recruitId") Long recruitId, @PathVariable("applyId") Long applyId, @RequestParam("status") String status){
        recruitService.acceptOrDenyApply(recruitId, applyId, status);
        return "redirect:/recruit/" + recruitId;
    }
    

    
    //모집 공고 마감하기
    @PostMapping("/recruit/{recruitId}/finish")
    public String finishRecruit(@PathVariable("recruitId") Long recruitId){
        recruitService.recruitFinish(recruitId);
        return "redirect:/main";
    }
}
