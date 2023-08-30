package com.likelion.teammatch.service;

import com.likelion.teammatch.dto.RecruitDraftDto;
import com.likelion.teammatch.dto.RecruitInfoDto;
import com.likelion.teammatch.entity.*;
import com.likelion.teammatch.repository.RecruitRepository;
import com.likelion.teammatch.repository.TeamTechStackRepository;
import com.likelion.teammatch.repository.TechStackRepository;
import com.likelion.teammatch.repository.UserRepository;
import com.likelion.teammatch.repository.team.TeamRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class RecruitService {
    private final RecruitRepository recruitRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final TeamTechStackRepository teamTechStackRepository;

    private final TechStackRepository techStackRepository;
    public RecruitService(RecruitRepository recruitRepository, UserRepository userRepository, TeamRepository teamRepository, TeamTechStackRepository teamTechStackRepository, TechStackRepository techStackRepository) {
        this.recruitRepository = recruitRepository;
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.teamTechStackRepository = teamTechStackRepository;
        this.techStackRepository = techStackRepository;
    }
    
    //모집 공고를 따로 추가하는 메소드
    public Long createRecruit(Long teamId, String title,Integer recruitMemberNum, String recruitDetails){
        //현재 사용자의 username 가져오기
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        //user Entity 가져오기
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        //Team entity 가져오기
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        //모집 공고 권한 확인하기
        if (!team.getTeamMangerId().equals(user.getId())) throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        Recruit recruit = new Recruit();
        recruit.setTitle(title);
        recruit.setRecruitMemberNum(recruitMemberNum);
        recruit.setTeamRecruitDetails(recruitDetails);
        recruit.setTeamManagerId(user.getId());
        recruit.setTeamId(team.getId());
        recruit.setIsFinished(false);
        recruit = recruitRepository.save(recruit);

        return recruit.getId();//이 모집 공고 상세보기를 하고 싶다.
    }

    //모집 공고 수정
    public Long updateRecruit(Long recruitId, String title, Integer recruitMemberNum, String recruitDetails){
        //현재 사용자의 username 가져오기
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        //user Entity 가져오기
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        //recruitEntity 가져오기
        Recruit recruit = recruitRepository.findById(recruitId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        //모집 공고 수정 권한 확인하기
        if (!recruit.getTeamManagerId().equals(user.getId())) throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        recruit.setTitle(title);
        recruit.setRecruitMemberNum(recruitMemberNum);
        recruit.setTeamRecruitDetails(recruitDetails);
        recruit = recruitRepository.save(recruit);

        return recruit.getId();
    }

    //모집 공고 해제
    public void recruitFinish(Long recruitId){
        //현재 사용자의 username 가져오기
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        //user Entity 가져오기
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        //recruitEntity 가져오기
        Recruit recruit = recruitRepository.findById(recruitId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        //모집 공고 수정 권한 확인하기
        if (!recruit.getTeamManagerId().equals(user.getId())) throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        recruit.setIsFinished(true);
        recruitRepository.save(recruit);
    }

    //모집 공고 삭제
    @Transactional
    public void deleteRecruit(Long recruitId){
        //현재 사용자의 username 가져오기
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        //user Entity 가져오기
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        //recruitEntity 가져오기
        Recruit recruit = recruitRepository.findById(recruitId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        //모집 공고 삭제 권한 확인하기
        if (!recruit.getTeamManagerId().equals(user.getId())) throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        recruitRepository.deleteById(recruitId);
    }
    
    //모집 공고 가져오기 in RecruitInfoDto (자세히 상세보기 할 때)
    public RecruitInfoDto getRecruitInfo(Long recruitId){
        //Recruit 가져오기
        Recruit recruit = recruitRepository.findById(recruitId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        //해당 Recruit에 연결된 Team 가져오기
        Team team = teamRepository.findById(recruit.getTeamId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        //fromEntity로 infoDto 만들기
        RecruitInfoDto recruitInfoDto = RecruitInfoDto.fromEntity(team, recruit);
        
        //TechStack 채우기
        List<String> techStackNameList = new ArrayList<>();
        List<TeamTechStack> techStackList = teamTechStackRepository.findAllByTeamId(team.getId());
        for (TeamTechStack teamTechStack : techStackList){
            String techStackName = techStackRepository.findById(teamTechStack.getTechStackId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)).getName();
            techStackNameList.add(techStackName);
        }
        recruitInfoDto.setTechStackName(techStackNameList);

        //teamManagerUsername 채우기
        String teamManagerUsername = userRepository.findById(recruit.getTeamManagerId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)).getUsername();
        recruitInfoDto.setTeamManagerUsername(teamManagerUsername);
        //Recruit 정보를 담은 RecruitInfoDto 가져오기
        return recruitInfoDto;
    }
    
    
    //가장 최근에 만들어진 10개의 모집공고 가져오기
    //Sort버튼
    public List<RecruitDraftDto> getRecruitDraftList(int page){
        PageRequest pageRequest = PageRequest.of(page, 10);
        Page<Recruit> pageOfEntity = recruitRepository.findAllByOrderByIdDesc(pageRequest);

        List<Recruit> recruitList = pageOfEntity.getContent();
        List<RecruitDraftDto> dtoList = new ArrayList<>();
        for (Recruit recruit : recruitList){
            Long teamId = recruit.getTeamId();
            Team team = teamRepository.findById(teamId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

            RecruitDraftDto dto = RecruitDraftDto.fromEntity(team, recruit);

            //TechStack 채우기
            List<String> techStackNameList = new ArrayList<>();
            List<TeamTechStack> techStackList = teamTechStackRepository.findAllByTeamId(team.getId());
            for (TeamTechStack teamTechStack : techStackList){
                String techStackName = techStackRepository.findById(teamTechStack.getTechStackId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)).getName();
                techStackNameList.add(techStackName);
            }
            dto.setTechStackList(techStackNameList);

            //comment 채우기 미구현
            dto.setCommentNum(0);
            dtoList.add(dto);
        }

        return dtoList;
    }
    
    
    //사용자 기술 스택에 포함된 기술 스택을 가지고 있는 프로젝트 가져오기
    //나와 관련된 기술 스택
    public Page<RecruitInfoDto> getRecruitInfoListBaseOnTechStack(int page){
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }

    //신청자수가 많은 순으로 가져오기 혹은...? 좋아요 순으로 가져오기.
    //미구현

}
