package com.likelion.teammatch.service.team;


import com.likelion.teammatch.dto.team.TeamCreateDto;
import com.likelion.teammatch.dto.team.TeamDraftDto;
import com.likelion.teammatch.dto.team.TeamInfoDto;
import com.likelion.teammatch.entity.*;
import com.likelion.teammatch.repository.RecruitRepository;
import com.likelion.teammatch.repository.TeamTechStackRepository;
import com.likelion.teammatch.repository.TechStackRepository;
import com.likelion.teammatch.repository.team.UserTeamRepository;
import com.likelion.teammatch.repository.team.TeamRepository;
import com.likelion.teammatch.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamService {
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final UserTeamRepository userTeamRepository;
    private final RecruitRepository recruitRepository;
    private final TechStackRepository techStackRepository;
    private final TeamTechStackRepository teamTechStackRepository;
    //Team 생성
    //생성 후 teamId 리턴함
    public Long createTeam(TeamCreateDto dto){
        //현재 로그인한 유저의 이름 가져오기
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        //유저 엔티티 가져오기
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Team team = TeamCreateDto.getTeamEntity(dto);
        Recruit recruit = TeamCreateDto.getRecruitEntity(dto);

        team.setTeamMangerId(user.getId());
        team = teamRepository.save(team);

        for (String techStackName : dto.getTeamTechStackList()){
            TechStack techStack = techStackRepository.findByName(techStackName).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

            TeamTechStack teamTechStack = new TeamTechStack();
            teamTechStack.setTechStackId(techStack.getId());
            teamTechStack.setTeamId(team.getId());
            teamTechStackRepository.save(teamTechStack);
        }


        if (recruit != null){
            recruit.setTeamManagerId(user.getId());
            recruit.setTeamId(team.getId());

            recruitRepository.save(recruit);
        }

        return team.getId();
    }


    //Team 가입
    public void JoinTeamByTeamId(Long teamId, String username, String role){
        //유저 엔티티 가져오기
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        //팀 엔티티 가져오기.
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        
        //이미 가입된 유저인지 아닌지 확인하기
        if (userTeamRepository.existsByUserIdAndTeamId(user.getId(), team.getId())) throw new ResponseStatusException(HttpStatus.CONFLICT);

        //팀의 현재 멤버수 증가시키기
        team.setMemberNum(team.getMemberNum() + 1);
        teamRepository.save(team);
        //가입하기
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(user.getId());
        userTeam.setTeamId(team.getId());
        userTeam.setRole(role);
        userTeamRepository.save(userTeam);
    }

    //Team 정보 가져오기
    public TeamInfoDto getTeamInfo(Long teamId){
        //현재 로그인한 유저의 이름 가져오기
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        //유저 엔티티 가져오기
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        //팀 엔티티 가져오기.
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        //해당 team 정보를 볼 수 있는 사람인지 검증하기
        if (!userTeamRepository.existsByUserIdAndTeamId(user.getId(), team.getId())) throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        //매니저 이름 추가하기
        TeamInfoDto dto = TeamInfoDto.fromEntity(team);
        String managerUsername = userRepository.findById(team.getTeamMangerId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND)).getUsername();
        dto.setTeamManagerUsername(managerUsername);

        //멤버 이름 리스트 추가하기
        List<String> memberNameList = new ArrayList<>();
        for (UserTeam userTeam : userTeamRepository.findAllByTeamId(team.getId())){
            String memberUsername = userRepository.findById(userTeam.getUserId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND)).getUsername();
            memberNameList.add(memberUsername);
        }
        dto.setTeamMemberUsername(memberNameList);

        //techStack이름 가져오기
        List<String> techStackNameList = new ArrayList<>();
        for (TeamTechStack teamTechStack : teamTechStackRepository.findAllByTeamId(teamId)){
            String name = techStackRepository.findById(teamTechStack.getTechStackId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)).getName();
            techStackNameList.add(name);
        }
        dto.setTeamTechStackName(techStackNameList);

        return dto;
    }

    //내가 가입중인 Team 간략 정보 가져오기 (마이페이지에서 현재 참여중인 팀 목록 볼 때)
    public List<TeamDraftDto> getMyTeamList(){
        //현재 로그인한 유저의 이름 가져오기
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        //유저 엔티티 가져오기
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        List<UserTeam> myTeamList = userTeamRepository.findAllByUserId(user.getId());

        List<TeamDraftDto> draftList = new ArrayList<>();
        for (UserTeam userTeam : myTeamList){
            Team currentTeam = teamRepository.findById(userTeam.getTeamId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            TeamDraftDto dto = new TeamDraftDto();
            dto.setTeamName(currentTeam.getTeamName());
            dto.setIsFinished(currentTeam.getIsFinished());
            draftList.add(dto);
        }
        return draftList;
    }


    // Team 수정


    // Team 삭제

}
