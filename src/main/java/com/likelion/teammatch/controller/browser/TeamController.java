package com.likelion.teammatch.controller.browser;

import com.likelion.teammatch.dto.RecruitInfoDto;
import com.likelion.teammatch.dto.team.TeamDraftDto;
import com.likelion.teammatch.dto.team.TeamInfoDto;
import com.likelion.teammatch.service.team.TeamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
@Slf4j
public class TeamController {
    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    // 팀 목록 페이지로 이동
    @GetMapping("/teamlist")
    public String teamList(Model model) {
        List<TeamDraftDto> teamList = teamService.getMyTeamList();
        model.addAttribute("teamList", teamList);
        return "/html/Team_page";
    }

    // 팀 상세보기 페이지로 이동
    @GetMapping("/team/{teamId}/detail")
    public String teamDetail(@PathVariable Long teamId, Model model) {
        TeamInfoDto teamInfo = teamService.getTeamInfo(teamId);
        model.addAttribute("teamInfo", teamInfo);
        return "/html/detail_complete";
    }

    // 팀 정보 수정
    @PostMapping("/team/{teamId}/edit")
    public String updateTeamInfo(@PathVariable Long teamId, TeamInfoDto dto) {
        try {
            // 서비스 레이어의 팀 정보 수정 기능 호출
            teamService.updateTeamInfo(teamId, dto);

            // 성공한 경우 페이지 리다이렉트
            return "redirect:/create.html";
        } catch (ResponseStatusException e) {
            // 실패한 경우 에러 메시지를 포함한 페이지 리다이렉트
            return "redirect:/error?message=" + e.getReason();
        }
    }

    // 팀 삭제 (fetch api)
    @PostMapping("/team/{teamId}/delete")
    public String deleteTeam(@PathVariable Long teamId) {
        try {
            teamService.deleteTeam(teamId);

            return "redirect:/Team_page";
        } catch (ResponseStatusException e) {
            return "redirect:/error?message=" + e.getReason();
        }
    }

    // 신규 팀원 모집 생성
    @PostMapping("/team/{teamId}/recruit")
    public String createAddRecruit(@PathVariable Long teamId, RecruitInfoDto dto) {
        try {
            Long recruitId = teamService.createAddRecruit(teamId, dto);

            return "redirect:/recruit/" + recruitId;
        } catch (ResponseStatusException e) {
            return "redirect:/error?message=" + e.getReason();
        }
    }

    // 신규 팀원 모집 수정
    @PostMapping("/team/{teamId}/recruit/{recruitId}/edit")
    public String updateAddRecruit(@PathVariable Long recruitId, RecruitInfoDto dto) {
        try {
            teamService.updateAddRecruit(recruitId, dto);

            return "redirect:/recruit/" + recruitId;
        } catch (ResponseStatusException e) {
            return "redirect:/error?message=" + e.getReason();
        }
    }

    // 신규 팀원 모집 삭제
    @PostMapping("/team/{teamId}/recruit/{recruitId}/delete")
    public String deleteAddRecruit(@PathVariable Long teamId, @PathVariable Long recruitId) {
        try {
            teamService.deleteAddRecruit(recruitId);

            return "redirect:/team/" + teamId;
        } catch (ResponseStatusException e) {
            return "redirect:/error?message=" + e.getReason();
        }
    }

    // 특정 인원 내보내기
    @PostMapping("/team/{teamId}/remove-member/{memberId}")
    public String removeMemberFromTeam(@PathVariable Long teamId, @PathVariable Long memberId) {
        try {
            // 서비스 레이어의 특정 인원 내보내기 기능 호출
            teamService.removeMemberFromTeam(teamId, memberId);

            // 성공한 경우 페이지 리다이렉트
            return "redirect:/team/" + teamId + "/detail";
        } catch (ResponseStatusException e) {
            // 실패한 경우 에러 메시지를 포함한 페이지 리다이렉트
            return "redirect:/error?message=" + e.getReason();
        }
    }

    // 팀에서 탈퇴하기 (fetch api)
    @PostMapping("/team/{teamId}/leave")
    public String leaveTeam(@PathVariable Long teamId) {
        try {
            teamService.leaveTeam(teamId);

            return "redirect:/Team_page";
        } catch (ResponseStatusException e) {
            return "redirect:/error?message=" + e.getReason();
        }
    }

    // 팀 종료하기 (fetch api)
    @PostMapping("/team/{teamId}/end")
    public String endTeam(@PathVariable Long teamId) {
        try {
            teamService.endTeam(teamId);

            return "redirect:/Team_page";
        } catch (ResponseStatusException e) {
            return "redirect:/error?message=" + e.getReason();
        }
    }

    // 팀 채팅방으로 이동
    @GetMapping("/team/{teamId}/chatroom")
    public String goToChatRoom(@PathVariable Long teamId, Model model) {
        // ChatRoom 으로 이동하는 URL 생성
        String chatRoomUrl = "/team/" + teamId + "/chatroom"; // 예: "/team/1/chatroom"

        // chatRoomUrl 을 모델에 추가
        model.addAttribute("chatRoomUrl", chatRoomUrl);

        // ChatRoom 템플릿 페이지로 이동
        return "/html/chat-room";
    }
}
