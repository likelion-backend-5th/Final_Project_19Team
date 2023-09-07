package com.likelion.teammatch.controller.api;

import com.likelion.teammatch.service.team.TeamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Slf4j
public class ApiTeamController {
    private final TeamService teamService;

    public ApiTeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @PostMapping("/team/{teamId}/delete")
    public String deleteTeam(@PathVariable("teamId") Long teamId){
        teamService.deleteTeam(teamId);
        return "done";
    }


    @PostMapping("/team/{teamId}/finish")
    public void finishTeam(@PathVariable("teamId") Long teamId){

        teamService.finishTeam(teamId);
    }


    @PostMapping("/team/{teamId}/leave")
    public void leaveTeam(@PathVariable Long teamId) {
        teamService.leaveTeam(teamId);
    }
}
