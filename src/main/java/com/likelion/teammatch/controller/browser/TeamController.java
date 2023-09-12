package com.likelion.teammatch.controller.browser;

import com.likelion.teammatch.dto.team.TeamCreateDto;
import com.likelion.teammatch.dto.team.TeamDraftDto;
import com.likelion.teammatch.dto.team.TeamInfoDto;
import com.likelion.teammatch.service.team.TeamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @GetMapping("/createTeam")
    public String getCreateTeamForm(){
        return "html/create";
    }

    @PostMapping("/createTeam")
    public String createTeam(TeamCreateDto dto){
        Long teamId = teamService.createTeam(dto);

        return "redirect:/main";
    }

    // 팀 목록 페이지로 이동
    @GetMapping("/teamlist")
    public String teamList(Model model) {
        List<TeamDraftDto> teamList = teamService.getMyTeamList();
        model.addAttribute("teamList", teamList);
        return "html/Team_page";
    }

    // 팀 상세보기 페이지로 이동
    @GetMapping("/team/{teamId}")
    public String teamDetail(@PathVariable Long teamId, Model model) {
        TeamInfoDto teamInfo = teamService.getTeamInfo(teamId);
        model.addAttribute("team", teamInfo);
        model.addAttribute("isMember", teamService.isMember(teamId));
        model.addAttribute("isFinished", teamInfo.getIsFinished());
        model.addAttribute("isManager", teamInfo.getTeamManagerUsername().equals(SecurityContextHolder.getContext().getAuthentication().getName()));
        return "html/team_details";
    }

    @GetMapping("/team/{teamId}/edit")
    public String getTeamUpdateForm(@PathVariable("teamId") Long teamId, Model model){
        TeamInfoDto teamInfo = teamService.getTeamInfo(teamId);
        model.addAttribute("team", teamInfo);
        return "html/edit_team";
    }

    // 팀 정보 수정
    @PostMapping("/team/{teamId}/edit")
    public String updateTeamInfo(@PathVariable Long teamId, TeamCreateDto dto) {
        teamService.updateTeamInfo(teamId, dto);
        return "redirect:/team/" + teamId;
    }




    

    // 특정 인원 내보내기
    // 팀장이 특정 인원을 내보내는 기능은 아직 구현하지 않는다.





    // 팀 채팅방으로 이동
    @GetMapping("/team/{teamId}/chatroom")
    public String goToChatRoom(@PathVariable Long teamId, Model model) {
        // ChatRoom 으로 이동하는 URL 생성
        String chatRoomUrl = "/team/" + teamId + "/chatroom"; // 예: "/team/1/chatroom"

        // chatRoomUrl 을 모델에 추가
        model.addAttribute("chatRoomUrl", chatRoomUrl);
        model.addAttribute("username", SecurityContextHolder.getContext().getAuthentication().getName());

        // ChatRoom 템플릿 페이지로 이동
        return "html/chat-room";
    }
}
