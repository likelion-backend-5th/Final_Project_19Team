package com.likelion.teammatch.service.team;


import com.likelion.teammatch.dto.RecruitInfoDto;
import com.likelion.teammatch.dto.team.TeamCreateDto;
import com.likelion.teammatch.dto.team.TeamDraftDto;
import com.likelion.teammatch.dto.team.TeamInfoDto;
import com.likelion.teammatch.entity.*;
import com.likelion.teammatch.repository.*;
import com.likelion.teammatch.repository.team.UserTeamRepository;
import com.likelion.teammatch.repository.team.TeamRepository;
import com.likelion.teammatch.service.S3UploadService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeamService {
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final UserTeamRepository userTeamRepository;
    private final RecruitRepository recruitRepository;
    private final TechStackRepository techStackRepository;
    private final TeamTechStackRepository teamTechStackRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ApplyRepository applyRepository;
    private final S3UploadService s3UploadService;
    //Team 생성
    //생성 후 teamId 리턴함
    public Long createTeam(TeamCreateDto dto, MultipartFile imageFile){
        log.info("팀 생성 : {}", dto.getTeamName());
        //현재 로그인한 유저의 이름 가져오기
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        //유저 엔티티 가져오기
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // 이미지를 S3에 업로드하고 업로드된 이미지 URL을 반환
        String imageUrl;

        if (imageFile.isEmpty()){
            imageUrl = "/assets/index/sample_img.png";
        }
        else {
            try {
                imageUrl = s3UploadService.upload(imageFile);
            } catch (IOException e) {
                e.printStackTrace();
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload image.");
            }
        }

        Team team = TeamCreateDto.getTeamEntity(dto);


        team.setTeamMangerId(user.getId());
        team = teamRepository.save(team);

        String[] techList = dto.getTechStackList().split("/");
        for (String techStackName : techList){
            TechStack techStack = techStackRepository.findByName(techStackName).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

            TeamTechStack teamTechStack = new TeamTechStack();
            teamTechStack.setTechStackId(techStack.getId());
            teamTechStack.setTeamId(team.getId());
            teamTechStackRepository.save(teamTechStack);
        }

        if (dto.getRecruitCheck() != null && dto.getRecruitCheck()){
            log.info("모집글 생성 with Team");
            Recruit recruit = TeamCreateDto.getRecruitEntity(dto);
            recruit.setTeamManagerId(user.getId());
            recruit.setTeamId(team.getId());
            recruit.setImageFileName(imageUrl);

            recruitRepository.save(recruit);
        }

        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(user.getId());
        userTeam.setTeamId(team.getId());
        userTeamRepository.save(userTeam);

        return team.getId();
    }


    //Team 가입
    public void joinTeamByTeamId(Long teamId, Long userId){
        //유저 엔티티 가져오기
        User user = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

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
            if (currentTeam.getDeleted()) continue;
            TeamDraftDto dto = new TeamDraftDto();
            dto.setTeamName(currentTeam.getTeamName());
            dto.setTeamId(currentTeam.getId());
            dto.setIsFinished(currentTeam.getIsFinished());
            dto.setIsManager(user.getId().equals(currentTeam.getTeamMangerId()));
            dto.setMemberNum(currentTeam.getMemberNum());
            dto.setIsOnline(currentTeam.getIsOnline());
            draftList.add(dto);
        }
        return draftList;
    }


    // Team 정보 수정
    @Transactional
    public void updateTeamInfo(Long teamId, TeamCreateDto dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Team team = teamRepository.findById(teamId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!user.getId().equals(team.getTeamMangerId())) throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        team.setTeamName(dto.getTeamName());
        team.setIsOnline(dto.getIsOnline());
        team.setTeamDescribe(dto.getTeamDescribe());

        teamTechStackRepository.deleteByTeamId(team.getId());
        String[] techList = dto.getTechStackList().split("/");
        for (String techStackName : techList){
            TechStack techStack = techStackRepository.findByName(techStackName).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

            TeamTechStack teamTechStack = new TeamTechStack();
            teamTechStack.setTechStackId(techStack.getId());
            teamTechStack.setTeamId(team.getId());
            teamTechStackRepository.save(teamTechStack);
        }

        teamRepository.save(team);
    }

    // Team 삭제
    // team 매니저가 악의적인 의도로 팀 삭제 후 튀어버리는 걸 막기 위한 방법이 현재 존재하지 않음.
    @Transactional
    public void deleteTeam(Long teamId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!user.getId().equals(team.getTeamMangerId())) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        
        //팀 관련 모집 공고 모집 마감
        List<Recruit> teamRecruitList = recruitRepository.findAllByTeamId(teamId);

        for (Recruit recruit : teamRecruitList){
            recruit.setIsFinished(true);
        }
        //모든 apply 삭제
        applyRepository.deleteAllByTeamId(teamId);
        
        team.setDeleted(true);
        teamRepository.save(team);
    }

    // 신규 팀원 모집공고 생성
    public Long createAddRecruit(Long teamId, RecruitInfoDto dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Team team = teamRepository.findById(teamId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!user.getId().equals(team.getTeamMangerId())) throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        Recruit recruit = new Recruit();
        recruit.setTeamManagerId(user.getId());
        recruit.setTeamId(teamId);
        recruit.setTitle(dto.getRecruitTitle());
        recruit.setTeamRecruitDetails(dto.getTeamRecruitDetails());
        recruit.setRecruitMemberNum(dto.getRecruitMemberNum());

        return recruitRepository.save(recruit).getId();
    }

    // 신규 팀원 모집공고 수정
    public void updateAddRecruit(Long recruitId, RecruitInfoDto dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Recruit recruit = recruitRepository.findById(recruitId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!user.getId().equals(recruit.getTeamManagerId())) throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        recruit.setTitle(dto.getRecruitTitle());
        recruit.setTeamRecruitDetails(dto.getTeamRecruitDetails());
        recruit.setRecruitMemberNum(dto.getRecruitMemberNum());

        recruitRepository.save(recruit);
    }

    // 신규 팀원 모집공고 삭제
    public void deleteAddRecruit(Long recruitId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Recruit recruit = recruitRepository.findById(recruitId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!user.getId().equals(recruit.getTeamManagerId())) throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        recruitRepository.deleteById(recruitId);
    }

    // 특정 인원 내보내기
    public void removeMemberFromTeam(Long teamId, Long memberId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Team team = teamRepository.findById(teamId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!user.getId().equals(team.getTeamMangerId())) throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        // 멤버 엔티티 가져오기
        User memberToRemove = userRepository.findById(memberId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // 팀에서 멤버 내보내기
        userTeamRepository.deleteByUserIdAndTeamId(memberToRemove.getId(), teamId);

        // 팀의 현재 멤버 수 감소
        team.setMemberNum(team.getMemberNum() - 1);
        teamRepository.save(team);
    }

    // 팀에서 탈퇴하기
    @Transactional
    public void leaveTeam(Long teamId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Team team = teamRepository.findById(teamId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!userTeamRepository.existsByUserIdAndTeamId(user.getId(), team.getId())) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "팀에 가입된 상태가 아닙니다!");
        if (user.getId().equals(team.getTeamMangerId())) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "팀 매니저는 팀에서 탈퇴할 수 없습니다.");

        //탈퇴한 user 점수 -1000점.
        user.setGiveUpCount(user.getGiveUpCount() + 1);
        user.setGrade(user.getGrade() - 1000);
        userRepository.save(user);
        // 팀에서 유저를 탈퇴시키기
        userTeamRepository.deleteByUserIdAndTeamId(user.getId(), teamId);

        // 팀의 현재 멤버 수 감소
        team.setMemberNum(team.getMemberNum() - 1);
        teamRepository.save(team);
    }

    // 팀 종료하기
    @Transactional
    public void finishTeam(Long teamId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Team team = teamRepository.findById(teamId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!user.getId().equals(team.getTeamMangerId())) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "팀 매니저만 팀을 종료할 수 있습니다.");

        // 팀에 속한 채팅방 삭제
        chatRoomRepository.deleteAllByTeamId(teamId);
        // 팀에 속한 모집 공고 종료
        List<Recruit> teamRecruitList = recruitRepository.findAllByTeamId(teamId);

        for (Recruit recruit : teamRecruitList){
            recruit.setIsFinished(true);
        }
        // 팀에 속한 모집 신청 삭제
        applyRepository.deleteAllByTeamId(teamId);

        //팀에 속한 멤버들 +500점 / 매니저는 +1000점.
        List<UserTeam> memberList = userTeamRepository.findAllByTeamId(teamId);
        for (UserTeam userTeam : memberList){
            User member = userRepository.findById(userTeam.getUserId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            if (member.getId().equals(team.getTeamMangerId())) member.setGrade(member.getGrade() + 1000);
            else member.setGrade(member.getGrade() + 500);
            userRepository.save(member);
        }

        
        // 팀의 상태를 종료로 변경
        team.setIsFinished(true);
        teamRepository.save(team);

        log.info("{} 팀 활동 종료", team.getTeamName());
    }

    public Boolean isMember(Long teamId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Team team = teamRepository.findById(teamId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return userTeamRepository.existsByUserIdAndTeamId(user.getId(), team.getId());
    }
}
