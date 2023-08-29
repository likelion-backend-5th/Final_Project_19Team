package com.likelion.teammatch.dto;

import com.likelion.teammatch.entity.Recruit;
import com.likelion.teammatch.entity.Team;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class RecruitInfoDto {

    //팀 id
    private Long teamId;
    //팀 이름
    private String teamName;
    //팀의 타입
    private String teamType;
    //팀 생성일자
    private LocalDateTime createdAt;
    //팀 설명 글
    private String teamDescribe;
    //공개프로젝트 여부
    private Boolean open;
    //온라인으로 진행하는지 아니면 오프라인으로 진행할지.
    private Boolean isOnline;

    //모집 완료된 글인지 아닌지.
    private Boolean isRecruitFinished;

    //팀매니저 이름
    private String teamManagerUsername;
    //기술 스택 이름 리스트
    private List<String> techStackName = new ArrayList<>();

    


    //모집 공고 아이디 recruitId
    private Long recruitId;
    //모집 공고 이름
    private String recruitTitle;

    //모집 설명글
    private String teamRecruitDetails;



    public static RecruitInfoDto fromEntity(Team team, Recruit recruit){
        RecruitInfoDto dto = new RecruitInfoDto();

        //팀 매니저 이름, 기술 스택 리스트를 제외하고 전부 다 채워줌.
        dto.setTeamId(team.getId());
        dto.setTeamName(team.getTeamName());
        dto.setTeamType(team.getType().name());
        dto.setCreatedAt(team.getCreatedAt());//todo 이거 스트링 포매팅 필요
        dto.setTeamDescribe(team.getTeamDescribe());
        dto.setOpen(team.getOpen());
        dto.setIsOnline(team.getIsOnline());



        dto.setRecruitId(recruit.getId());
        dto.setRecruitTitle(recruit.getTitle());
        dto.setTeamRecruitDetails(recruit.getTeamRecruitDetails());

        return dto;
    }
}
