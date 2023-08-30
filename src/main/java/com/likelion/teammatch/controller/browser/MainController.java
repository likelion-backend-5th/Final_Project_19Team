package com.likelion.teammatch.controller.browser;

import com.likelion.teammatch.dto.RecruitDraftDto;
import com.likelion.teammatch.dto.RecruitInfoDto;
import com.likelion.teammatch.dto.team.TeamCreateDto;
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

    public MainController(TeamService teamService, RecruitService recruitService) {
        this.teamService = teamService;
        this.recruitService = recruitService;
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


}
