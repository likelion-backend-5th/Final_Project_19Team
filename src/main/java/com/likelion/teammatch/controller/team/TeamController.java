package com.likelion.teammatch.controller.team;

import com.likelion.teammatch.dto.team.request.CreateTeamRequestDto;
import com.likelion.teammatch.dto.team.request.JoinTeamRequestDto;
import com.likelion.teammatch.dto.team.response.*;
import com.likelion.teammatch.service.team.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/team")
public class TeamController {
    private final TeamService teamService;

    @PostMapping
    public CreateTeamResponseDto create(@RequestBody CreateTeamRequestDto dto) {
        return teamService.create(dto);
    }

    @PostMapping("/join/{teamId}")
    public JoinTeamResponseDto join(
            @RequestBody JoinTeamRequestDto dto,
            @PathVariable Long teamId
    ) {
        return teamService.join(dto, teamId);
    }

    @GetMapping("/info/{teamId}")   // 팀 정보 조회
    public InfoResponseDto getTeamInfo(@PathVariable Long teamId) {
        return teamService.getTeamInfo(teamId);
    }

    @GetMapping("/view/{teamId}")
    public ViewTeamResponseDto viewTeam(@PathVariable Long teamId) {
        return teamService.viewTeam(teamId);
    }

    @GetMapping("/team/list")   // 팀 검색하기
    public List<SearchTeamResponseDto> searchTeamInfo(@RequestParam(required = false) String search) {
        return teamService.searchTeamInfo(search);
    }
}
