package com.likelion.teammatch.controller.api;

import com.likelion.teammatch.dto.JwtTokenDto;
import com.likelion.teammatch.dto.LoginDto;
import com.likelion.teammatch.dto.RegisterDto;
import com.likelion.teammatch.service.UserService;
import com.likelion.teammatch.utils.JwtTokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

//이제 더이상 사용하지 않음.
@RestController
@Slf4j
@RequestMapping("/api")
public class ApiAuthController {

    private final JwtTokenUtils jwtTokenUtils;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public ApiAuthController(JwtTokenUtils jwtTokenUtils, UserService userService, PasswordEncoder passwordEncoder) {
        this.jwtTokenUtils = jwtTokenUtils;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }


    @PostMapping("/issue")
    public JwtTokenDto issueJwt(@RequestBody LoginDto dto){
        UserDetails userDetails = userService.loadUserByUsername(dto.getUsername());
        if (!passwordEncoder.matches(dto.getPassword(), userDetails.getPassword())) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);


        JwtTokenDto response = new JwtTokenDto();
        response.setAccessToken(jwtTokenUtils.generateToken(userDetails));
        response.setRefreshToken(jwtTokenUtils.generateRefreshTokenByUsername(userDetails.getUsername()));
        return response;
    }


    @PostMapping("/register")
    public String register(@RequestBody RegisterDto dto){
        userService.createUser(dto);

        log.info("{} 가입 완료", dto.getUsername());
        return "가입처리되었습니다!";
    }


}
