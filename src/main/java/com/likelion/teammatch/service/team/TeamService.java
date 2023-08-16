package com.likelion.teammatch.service.team;

import com.likelion.teammatch.dto.team.request.CreateTeamRequestDto;
import com.likelion.teammatch.dto.team.request.JoinTeamRequestDto;
import com.likelion.teammatch.dto.team.response.*;
import com.likelion.teammatch.entity.Team;
import com.likelion.teammatch.entity.User;
import com.likelion.teammatch.repository.team.TeamRepository;
import com.likelion.teammatch.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamService {
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    // Team 생성
    public CreateTeamResponseDto create(CreateTeamRequestDto dto) {
        Team team = new Team(dto.getTeamName());
        teamRepository.save(team);

        return new CreateTeamResponseDto(team.getTeamName(), team.getId());
    }

    public JoinTeamResponseDto join(JoinTeamRequestDto dto, Long teamId) {  // teamId = PK
        // teamId 로 team 조회
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new RuntimeException());

        // userId 로 user 조회
        User user = userRepository.findById(dto.getUserId()).orElseThrow(() -> new RuntimeException());

        // user 의 team 필드에 team 값을 넣는다
        user.joinTeam(team);

        return new JoinTeamResponseDto(team.getTeamName(), user.getUsername());
    }

    public InfoResponseDto getTeamInfo(Long teamId) {
        // 1. teamName 으로 조회
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new RuntimeException());

        // 2. 해당 팀의 모든 유저 조회
        List<String> memberNames = userRepository.findUserNamesByTeam(team);

        // 3. responseDto 에 담아서 보내기
        return new InfoResponseDto(team.getTeamName(), memberNames);
    }

    public ViewTeamResponseDto viewTeam(Long teamId) {
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new RuntimeException());
        return new ViewTeamResponseDto(team.getId(), team.getTeamName());
    }

    public List<SearchTeamResponseDto> searchTeamInfo(String keyword) {
        if (keyword == null) {  // 키워드 없으면 모든 팀 보여주기
            List<SearchTeamResponseDto> teams = teamRepository.findAllTeamInfo();
            return teams;
        } else {
            List<SearchTeamResponseDto> teams = teamRepository.findByKeyword("%" + keyword + "%");
            return teams;
        }
    }

    // Team 수정


    // Team 삭제

}
