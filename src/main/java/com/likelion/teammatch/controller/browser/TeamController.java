package com.likelion.teammatch.controller.browser;

import com.likelion.teammatch.dto.team.TeamDraftDto;
import com.likelion.teammatch.dto.team.TeamInfoDto;
import com.likelion.teammatch.service.team.TeamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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
