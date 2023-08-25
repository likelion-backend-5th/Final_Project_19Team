package com.likelion.teammatch.service;

import com.likelion.teammatch.dto.ProjectResultInfoDto;
import com.likelion.teammatch.dto.team.TeamCreateDto;
import com.likelion.teammatch.entity.ProjectResult;
import com.likelion.teammatch.entity.Team;
import com.likelion.teammatch.entity.User;
import com.likelion.teammatch.repository.ProjectResultRepository;
import com.likelion.teammatch.repository.UserRepository;
import com.likelion.teammatch.repository.team.TeamRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProjectResultService {
    private final ProjectResultRepository projectResultRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;

    public ProjectResultService(ProjectResultRepository projectResultRepository, TeamRepository teamRepository, UserRepository userRepository) {
        this.projectResultRepository = projectResultRepository;
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
    }

    //ProjectResult 를 따로 추가하는 메소드
    public Long createProjectResult(Long teamId, String github, String projectResultDetails){
        //현재 사용자의 username 가져오기
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        //user Entity 가져오기
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        //Team entity 가져오기
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        //ProjectResult 권한 확인하기
        if (!team.getTeamMangerId().equals(user.getId())) throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        ProjectResult projectResult = new ProjectResult();
        projectResult.setDescription(projectResultDetails);
        projectResult.setGithub(github);
        projectResult.setTeamManagerId(user.getId());
        projectResult.setTeamId(team.getId());
        projectResult.setOpen(team.getOpen());
        projectResult = projectResultRepository.save(projectResult);

        return projectResult.getId();
    }

    //ProjectResult 수정
    public Long updateProjectResult(Long projectResultId, String projectResultDetails, String github) {
        //현재 사용자의 username 가져오기
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        //user Entity 가져오기
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        //projectResultEntity 가져오기
        ProjectResult projectResult = projectResultRepository.findById(projectResultId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        //ProjectResult 수정 권한 확인하기
        if (!projectResult.getTeamManagerId().equals(user.getId())) throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        projectResult.setDescription(projectResultDetails);
        projectResult.setGithub(github);
        projectResult = projectResultRepository.save(projectResult);

        return projectResult.getId();
    }

    //ProjectResult 삭제
    @Transactional
    public void deleteProjectResult(Long projectResultId) {
        //현재 사용자의 username 가져오기
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        //user Entity 가져오기
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        //projectResultEntity 가져오기
        ProjectResult projectResult = projectResultRepository.findById(projectResultId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        //ProjectResult 삭제 권한 확인하기
        if (!projectResult.getTeamManagerId().equals(user.getId())) throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        projectResultRepository.deleteById(projectResultId);
    }

    //ProjectResult 가져오기
    public ProjectResultInfoDto getProjectResultInfo(Long projectResultId) {

        //ProjectResult 가져오기
        ProjectResult projectResult = projectResultRepository.findById(projectResultId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        //해당 ProjectResult 에 연결된 Team 가져오기
        Team team = teamRepository.findById(projectResult.getTeamId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        //해당 Review 에 연결된 TeamManager 가져오기
        User user = userRepository.findById(projectResult.getTeamManagerId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return ProjectResultInfoDto.fromEntity(team, user, projectResult);
    }
}
