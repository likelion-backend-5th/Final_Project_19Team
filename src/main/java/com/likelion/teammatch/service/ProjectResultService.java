package com.likelion.teammatch.service;

import com.likelion.teammatch.dto.ProjectResultInfoDto;
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

    //ProjectResult 생성
    public Long createProjectResult(Long teamId, String github, String projectResultDetails){
        //현재 사용자의 username 가져오기
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        //user Entity 가져오기
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        //Team entity 가져오기
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        //ProjectResult 권한 확인하기
        if (!team.getTeamMangerId().equals(user.getId())) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "팀 관리자만 프로젝트 결과물을 생성할 수 있습니다.");

        ProjectResult projectResult = new ProjectResult();
        projectResult.setDescription(projectResultDetails);
        projectResult.setGithub(github);
        projectResult.setTeamManagerId(user.getId());
        projectResult.setTeamId(team.getId());
        projectResult.setOpen(team.getIsOnline());
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
        if (!projectResult.getTeamManagerId().equals(user.getId())) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "프로젝트 결과물의 수정 권한이 없습니다.");

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
        if (!projectResult.getTeamManagerId().equals(user.getId())) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "프로젝트 결과물의 삭제 권한이 없습니다.");

        projectResultRepository.deleteById(projectResultId);
    }

    //ProjectResult 가져오기
    public ProjectResultInfoDto getProjectResultInfo(Long projectResultId) {
        //ProjectResult 가져오기
        ProjectResult projectResult = projectResultRepository.findById(projectResultId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "프로젝트 결과물을 찾을 수 없습니다."));

        //해당 ProjectResult 에 연결된 Team 가져오기
        Team team = teamRepository.findById(projectResult.getTeamId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        //해당 ProjectResult 에 연결된 TeamManager 가져오기
        User user = userRepository.findById(projectResult.getTeamManagerId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return ProjectResultInfoDto.fromEntity(team, user, projectResult);
    }

    // GitHub 주소 업로드 기능
    @Transactional
    public void uploadGitHubAddress(Long projectResultId, String github) {
        // 프로젝트 결과물 가져오기
        ProjectResult projectResult = projectResultRepository.findById(projectResultId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "프로젝트 결과물을 찾을 수 없습니다."));

        // 프로젝트 결과물의 팀 매니저인지 확인
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        if (!projectResult.getTeamManagerId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "팀 매니저만 GitHub 주소를 등록할 수 있습니다.");
        }

        // GitHub 주소 업로드
        projectResult.setGithub(github);
        projectResultRepository.save(projectResult);
    }

    // GitHub 주소 수정
    @Transactional
    public void updateGitHubAddress(Long projectResultId, String newGitHub) {
        ProjectResult projectResult = projectResultRepository.findById(projectResultId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "프로젝트 결과물을 찾을 수 없습니다."));

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        if (!projectResult.getTeamManagerId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "팀 매니저만 GitHub 주소를 수정할 수 있습니다.");
        }

        projectResult.setGithub(newGitHub);
        projectResultRepository.save(projectResult);
    }
}
