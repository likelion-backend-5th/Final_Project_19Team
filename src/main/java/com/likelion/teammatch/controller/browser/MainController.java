package com.likelion.teammatch.controller.browser;

import com.likelion.teammatch.dto.RecruitInfoDto;
import com.likelion.teammatch.service.RecruitService;
import com.likelion.teammatch.service.team.TeamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@Slf4j
public class MainController {

    private final TeamService teamService;
    private final RecruitService recruitService;

    public MainController(TeamService teamService, RecruitService recruitService) {
        this.teamService = teamService;
        this.recruitService = recruitService;
    }

    @GetMapping("/main")
    public String getMainPage(
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            Model model
    ){
        List<RecruitInfoDto> recruitInfoList = recruitService.getRecruitInfoList(page);
        model.addAttribute("recruitList", recruitInfoList);
        if (SecurityContextHolder.getContext().getAuthentication().getName().equals("anonymousUser")) model.addAttribute("logined", false);
        else model.addAttribute("logined", true);

        return "/html/index";
    }

    @GetMapping("/createTeam")
    public String getCreateTeamForm(Model model){
        return "/html/create";
    }

}