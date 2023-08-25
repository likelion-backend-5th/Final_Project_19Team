package com.likelion.teammatch.service;

import com.likelion.teammatch.dto.RecruitInfoDto;
import com.likelion.teammatch.dto.team.TeamCreateDto;
import com.likelion.teammatch.entity.Recruit;
import com.likelion.teammatch.entity.Team;
import com.likelion.teammatch.entity.User;
import com.likelion.teammatch.repository.RecruitRepository;
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

    public RecruitService(RecruitRepository recruitRepository, UserRepository userRepository, TeamRepository teamRepository) {
        this.recruitRepository = recruitRepository;
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
    }

    //Team을 만들면서 동시에 모집 공고를 만드는 메소드
    public Long createRecruitBoardWithTeam(TeamCreateDto dto){
        Recruit recruit = TeamCreateDto.getRecruitEntity(dto);

        if (recruit != null){
            recruit = recruitRepository.save(recruit);
            return recruit.getId();
        }
        return -1L;
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
        
        //Recruit 정보를 담은 RecruitInfoDto 가져오기
        return RecruitInfoDto.fromEntity(team, recruit);
    }
    
    
    //가장 최근에 만들어진 10개의 모집공고 가져오기
    //Sort버튼
    public List<RecruitInfoDto> getRecruitInfoList(int page){
        PageRequest pageRequest = PageRequest.of(page, 10);
        Page<Recruit> pageOfEntity = recruitRepository.findAllByIdOrderByIdDesc(pageRequest);

        List<Recruit> recruitList = pageOfEntity.getContent();
        List<RecruitInfoDto> dtoList = new ArrayList<>();
        for (Recruit recruit : recruitList){
            Long teamId = recruit.getTeamId();
            Team team = teamRepository.findById(teamId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

            RecruitInfoDto dto = RecruitInfoDto.fromEntity(team, recruit);
            String teamManagerName = userRepository.findById(recruit.getTeamManagerId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)).getUsername();
            dto.setTeamManagerUsername(teamManagerName);
            
            //기술 스택 부분은 미구현. 그냥 빈 기술 스택 올림.
            dto.setTechStackName(new ArrayList<String>());
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
