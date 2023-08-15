package com.likelion.teamservice.service;

import com.likelion.teammatch.entity.User;
import com.likelion.teammatch.repository.UserRepository;
import com.likelion.teammatch.service.UserService;
import com.likelion.teamservice.dto.TeamDto;
import com.likelion.teamservice.entity.Team;
import com.likelion.teamservice.exception.NotFoundException;
import com.likelion.teamservice.repository.TeamRepository;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeamService {
    private final TeamRepository teamRepository;
    private final UserService userService;

    public TeamService(TeamRepository teamRepository, UserService userService) {
        this.teamRepository = teamRepository;
        this.userService = userService;
    }

    // Team 단일 조회
    public List<TeamDto> getTeams() {
        return teamRepository.findAll().stream()
                .map(TeamDto::fromEntity)
                .collect(Collectors.toList());
    }

    // Team 전체 조회
    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    // Team 생성
    public Team createTeam(Team team) {
        User user = userService.loadUserByUsername(team);
        return teamRepository.save(team);
    }

    // Team 수정
    public Team updateTeam(Long teamId) {
        Team team = findTeamById(teamId);
        return teamRepository.save(team);
    }

    // Team 삭제
    public void deleteTeam(Long teamId, String userId) {
        teamRepository.deleteByIdAndAdminId(teamId, userId);
    }

    // 정확히 뭔지 모름.. updateTeam 에서 쓰이던데
    private Team findTeamById(Long teamId) {
        return teamRepository.findById(teamId).orElseThrow(NotFoundException.Team::new);
    }
}
