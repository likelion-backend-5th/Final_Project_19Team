package com.likelion.teammatch.controller.browser;


import com.likelion.teammatch.service.team.TeamService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@RequestMapping("/team")
@Controller
public class TeamController {
    private final TeamService teamService;


    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @GetMapping("/team")
    public String getMyTeamList(Model model){
        model.addAttribute("teamList", teamService.getMyTeamList());
        return "/html/teamList";
    }
}
