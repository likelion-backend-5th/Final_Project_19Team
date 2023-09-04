package com.likelion.teammatch.controller.browser;

import com.likelion.teammatch.dto.JwtTokenDto;
import com.likelion.teammatch.dto.LoginDto;
import com.likelion.teammatch.dto.RegisterDto;
import com.likelion.teammatch.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
//1시 35분 접속해서 토큰 받음.
@Controller
@Slf4j
public class AuthController {
    private final UserService userService;


    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String getLoginForm() {
        return "/html/login";
    }

    @PostMapping("/login")
    public String login(HttpServletResponse response, LoginDto dto) {

        if (!userService.isLoginAttemptValid(dto)) {
            return "redirect:/login?error=true";
        }

        JwtTokenDto loginToken = userService.getLoginToken(dto);

        Cookie accessCookie = new Cookie("accessToken", loginToken.getAccessToken());
        Cookie refreshCookie = new Cookie("refreshToken", loginToken.getRefreshToken());

        accessCookie.setHttpOnly(true);
        refreshCookie.setHttpOnly(true);

        accessCookie.setMaxAge(3600 * 10);
        refreshCookie.setMaxAge(3600 * 10);

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);

        //테스트 단계에서만 test로 redirect한다!
        return "redirect:/main";

    }


    @GetMapping("/test")
    public String getTest(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        model.addAttribute("username", username);
        return "/html/test";
    }

    @GetMapping("/register")
    public String getRegisterForm() {
        return "/html/register";
    }

    @PostMapping("/register")
    public String register(RegisterDto dto) {
        if (userService.userRegisterConflicts(dto)) {
            return "redirect:/register?error=true";
        }
        userService.createUser(dto);

        return "redirect:/login";
    }
    @GetMapping("/declare")
    public String showDeclarePage() {
        return "html/declare";
    }

}
