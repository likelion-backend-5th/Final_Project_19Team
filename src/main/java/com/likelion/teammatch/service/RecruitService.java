package com.likelion.teammatch.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.likelion.teammatch.dto.ApplyDto;
import com.likelion.teammatch.dto.RecruitDraftDto;
import com.likelion.teammatch.dto.RecruitInfoDto;
import com.likelion.teammatch.entity.*;
import com.likelion.teammatch.repository.*;
import com.likelion.teammatch.repository.team.TeamRepository;
import com.likelion.teammatch.repository.team.UserTeamRepository;
import com.likelion.teammatch.service.team.TeamService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class RecruitService {
    private final RecruitRepository recruitRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final TeamTechStackRepository teamTechStackRepository;
    private final UserTeamRepository userTeamRepository;
    private final S3UploadService s3UploadService;

    private final TechStackRepository techStackRepository;
    private final TeamService teamService;
    private final ApplyRepository applyRepository;
    private final AlarmService alarmService;
    public RecruitService(RecruitRepository recruitRepository, UserRepository userRepository, TeamRepository teamRepository, TeamTechStackRepository teamTechStackRepository, UserTeamRepository userTeamRepository, S3UploadService s3UploadService, TechStackRepository techStackRepository, TeamService teamService, ApplyRepository applyRepository, AlarmService alarmService) {
        this.recruitRepository = recruitRepository;
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.teamTechStackRepository = teamTechStackRepository;
        this.userTeamRepository = userTeamRepository;
        this.s3UploadService = s3UploadService;
        this.techStackRepository = techStackRepository;
        this.teamService = teamService;
        this.applyRepository = applyRepository;
        this.alarmService = alarmService;
    }
    
    //모집 공고를 따로 추가하는 메소드
    public Long createRecruit(Long teamId, String title,Integer recruitMemberNum, String recruitDetails, String techStackWanted, MultipartFile imageFile){
        //현재 사용자의 username 가져오기
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        //user Entity 가져오기
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        //Team entity 가져오기
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

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

        //모집 공고 권한 확인하기
        if (!team.getTeamMangerId().equals(user.getId())) throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        Recruit recruit = new Recruit();
        recruit.setTitle(title);
        recruit.setRecruitMemberNum(recruitMemberNum);
        recruit.setTeamRecruitDetails(recruitDetails);
        recruit.setTeamManagerId(user.getId());
        recruit.setTeamId(team.getId());
        recruit.setIsFinished(false);
        recruit.setTechStackWanted(techStackWanted);
        recruit.setImageFileName(imageUrl);
        recruit = recruitRepository.save(recruit);

        return recruit.getId();//이 모집 공고 상세보기를 하고 싶다.
    }

    //모집 공고 수정
    public Long updateRecruit(Long recruitId, String title, Integer recruitMemberNum, String recruitDetails, String techStackWanted, MultipartFile imageFile){
        //현재 사용자의 username 가져오기
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        //user Entity 가져오기
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        //recruitEntity 가져오기
        Recruit recruit = recruitRepository.findById(recruitId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // 이미지를 S3에 업로드하고 업로드된 이미지 URL을 반환
        String imageUrl;

        if (imageFile.isEmpty()){
            // 이미지를 업로드하지 않은 경우, 이전 이미지 URL을 가져옴
            imageUrl = recruit.getImageFileName();
        }
        else {
            try {
                imageUrl = s3UploadService.upload(imageFile);
            } catch (IOException e) {
                e.printStackTrace();
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload image.");
            }
        }

        //모집 공고 수정 권한 확인하기
        if (!recruit.getTeamManagerId().equals(user.getId())) throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        recruit.setTitle(title);
        recruit.setRecruitMemberNum(recruitMemberNum);
        recruit.setTeamRecruitDetails(recruitDetails);
        recruit.setTechStackWanted(techStackWanted);
        recruit.setImageFileName(imageUrl);
        recruit = recruitRepository.save(recruit);

        return recruit.getId();
    }

    //모집 공고 해제
    @Transactional
    public void recruitFinish(Long recruitId){
        //현재 사용자의 username 가져오기
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        //user Entity 가져오기
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        //recruitEntity 가져오기
        Recruit recruit = recruitRepository.findById(recruitId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        //모집 공고 수정 권한 확인하기
        if (!recruit.getTeamManagerId().equals(user.getId())) throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        for (Apply applyEntity : applyRepository.findAllByRecruitId(recruitId)){
            alarmService.createAlarm("\"" + recruit.getTitle() + "\" 모집 종료", applyEntity.getUserId());
        }

        applyRepository.deleteAllByRecruitId(recruitId);//모집 마감시 해당 모집 공고에 연결된 모든 모집 신청 삭제
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
        List<String> techStackNameList = Arrays.stream(recruit.getTechStackWanted().split("/")).toList();

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

            if (recruit.getTechStackWanted() != null) techStackNameList = Arrays.stream(recruit.getTechStackWanted().split("/")).toList();
            dto.setTechStackList(techStackNameList);

            // 이미지 URL 채우기
            if (dto.getImageUrl().equals("/assets/index/sample_img.png")) dto.setImageUrl(recruit.getImageFileName());
            else dto.setImageUrl(getImageUrlForRecruit(recruit));

            //comment 채우기 미구현
            dto.setCommentNum(0);
            dtoList.add(dto);
        }

        return dtoList;
    }

    // Recruit 객체로부터 이미지 URL을 얻는 메서드
    private String getImageUrlForRecruit(Recruit recruit) {
        // S3 버킷 정보
        String bucketName = "19team-s3-bucket"; // S3 버킷 이름
        String region = "ap-northeast-2"; // AWS 리전 (예: "us-east-1")

        // 이미지 파일 이름
        String imageFileName = recruit.getImageFileName(); // 이미지 파일 이름을 가져오는 로직을 적용

        // S3 클라이언트 생성
        AmazonS3 amazonS3 = AmazonS3Client.builder()
                .withRegion(region)
                .build();

        // 이미지 URL 생성
        String imageUrl = amazonS3.getUrl(bucketName, imageFileName).toString();

        return imageUrl;
    }

    public String uploadAndProcessImage(MultipartFile file, Long recruitId) throws IOException {
        Recruit recruit = recruitRepository.findById(recruitId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recruit not found"));

        // 이미지 업로드 및 URL 얻기
        String imageUrl = s3UploadService.upload(file);

        // recruit 엔티티에 이미지 URL 설정
        recruit.setImageFileName(imageUrl);

        // recruit 엔티티 저장
        recruitRepository.save(recruit);

        String imageUrlForRecruit = getImageUrlForRecruit(recruit);

        return imageUrlForRecruit;
    }
    
    //사용자 기술 스택에 포함된 기술 스택을 가지고 있는 프로젝트 가져오기
    //나와 관련된 기술 스택
    public Page<RecruitInfoDto> getRecruitInfoListBaseOnTechStack(int page){
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }

    public void applyToRecruit(Long recruitId, String introduction) {
        //현재 사용자의 username 가져오기
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        //user Entity 가져오기
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        //recruitEntity 가져오기
        Recruit recruit = recruitRepository.findById(recruitId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        //이미 팀에 참가한 유저인지 확인하기
        if (userTeamRepository.existsByUserIdAndTeamId(user.getId(), recruit.getTeamId())) throw new ResponseStatusException(HttpStatus.CONFLICT);

        //다른 포지션으로 여러번 지원할 수 있기 때문에 apply를 중복해서 넣은 건 아닌지 검사하지 않는다.
        Apply apply = new Apply();
        apply.setUserId(user.getId());
        apply.setTeamId(recruit.getTeamId());
        apply.setRecruitId(recruit.getId());
        apply.setIntroduction(introduction);

        applyRepository.save(apply);

        alarmService.createAlarm(username + " 유저 \n" + recruit.getTitle() + "\n모집 공고 신청", recruit.getTeamManagerId());
    }

    @Transactional
    public void acceptOrDenyApply(Long recruitId, Long applyId, String status) {
        //현재 사용자의 username 가져오기
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        //user Entity 가져오기
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        //recruitEntity 가져오기
        Recruit recruit = recruitRepository.findById(recruitId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        //Apply Entity 가져오기
        Apply apply = applyRepository.findById(applyId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        //현재 user가 teamManager가 맞는지 검증
        if (!user.getId().equals(recruit.getTeamManagerId())) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        //recruit의 id와 apply가 작성된 recruit이 일치하는지 검증
        if (!recruit.getTeamId().equals(apply.getTeamId())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        //이미 종료된 recruit인지 검증
        if (recruit.getIsFinished()) throw new ResponseStatusException(HttpStatus.GONE);

        //분기
        if (status.equals("accept")){
            //teamService의 메소드 활용
            teamService.joinTeamByTeamId(recruit.getTeamId(), apply.getUserId());
            alarmService.createAlarm( recruit.getTitle() + "\n 모집 공고 합격!", apply.getUserId());
        }
        else {
            alarmService.createAlarm(recruit.getTitle() + "\n 모집 공고 탈락", apply.getUserId());
        }
        applyRepository.deleteById(apply.getId());
    }

    public List<ApplyDto> getApplyListForRecruit(Long recruitId) {

        List<Apply> applyList = applyRepository.findAllByRecruitId(recruitId);

        List<ApplyDto> dtoList = new ArrayList<>();
        for (Apply entity: applyList){
            ApplyDto dto = new ApplyDto();

            String username = userRepository.findById(entity.getUserId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)).getUsername();

            dto.setId(entity.getId());
            dto.setUsername(username);
            dto.setIntroduction(entity.getIntroduction());
            dtoList.add(dto);

        }
        return dtoList;
    }

    public List<RecruitDraftDto> getRecruitDraftListBySearch(Integer page, String searchTerm) {
        PageRequest pageRequest = PageRequest.of(page, 10);
        Page<Recruit> pageOfEntity = recruitRepository.searchByTechStackWantedOrTitleOrderByIdDesc(pageRequest, searchTerm);

        List<Recruit> recruitList = pageOfEntity.getContent();
        List<RecruitDraftDto> dtoList = new ArrayList<>();
        for (Recruit recruit : recruitList){
            Long teamId = recruit.getTeamId();
            Team team = teamRepository.findById(teamId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

            RecruitDraftDto dto = RecruitDraftDto.fromEntity(team, recruit);

            //TechStack 채우기
            List<String> techStackNameList = new ArrayList<>();

            if (recruit.getTechStackWanted() != null) techStackNameList = Arrays.stream(recruit.getTechStackWanted().split("/")).toList();
            dto.setTechStackList(techStackNameList);

            // 이미지 URL 채우기
            if (dto.getImageUrl().equals("/assets/index/sample_img.png")) dto.setImageUrl(recruit.getImageFileName());
            else dto.setImageUrl(getImageUrlForRecruit(recruit));

            //comment 채우기 미구현
            dto.setCommentNum(0);
            dtoList.add(dto);
        }

        return dtoList;
    }


    //신청자수가 많은 순으로 가져오기 혹은...? 좋아요 순으로 가져오기.
    //미구현

}
