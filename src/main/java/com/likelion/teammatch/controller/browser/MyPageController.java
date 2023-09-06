package com.likelion.teammatch.controller.browser;

import com.likelion.teammatch.dto.UserProfileDto;
import com.likelion.teammatch.service.UserService;
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Controller
@Slf4j
public class MyPageController {
    private final UserService userService;

    private final TeamService teamService;
    public MyPageController(UserService userService, TeamService teamService) {
        this.userService = userService;
        this.teamService = teamService;
    }

    @GetMapping("/profile/{username}")
    public String getUserProfile(@PathVariable("username") String username, Model model){
        UserProfileDto profileOfUser = userService.getProfileOfUser(username);

        model.addAttribute("profile", profileOfUser);

        if (profileOfUser.getUsername().equals(SecurityContextHolder.getContext().getAuthentication().getName())){
            model.addAttribute("isOwner", true);
            model.addAttribute("teamList", teamService.getMyTeamList());

        }
        else
            model.addAttribute("isOwner", false);

        return "/html/mypage";
    }

    @GetMapping("/profile/{username}/edit")
    public String getUserProfileForm(@PathVariable("username") String username, Model model){
        UserProfileDto profileOfUser = userService.getProfileOfUser(username);

        if (!profileOfUser.getUsername().equals(SecurityContextHolder.getContext().getAuthentication().getName()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        model.addAttribute("profile", profileOfUser);

        return "/html/myprofile_edit";
    }

    @PostMapping("/profile/{username}/edit")
    public String updateProfile(
            @PathVariable("username") String username,
            UserProfileDto dto,
            @RequestParam("techStackInputString") String techStackString
    ) throws UnsupportedEncodingException {
        userService.updateProfile(dto, techStackString);
        String encodedUsername = URLEncoder.encode(username, "UTF-8");
        return "redirect:/profile/" + encodedUsername;
    }

}
