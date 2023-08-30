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
    //온라인으로 진행하는지 아니면 오프라인으로 진행할지.
    private Boolean isOnline;
    //팀 생성일자
    private LocalDateTime createdAt;
    //팀 설명 글
    private String teamDescribe;
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
    //모집 목표 인원수
    private Integer recruitMemberNum;

    public static RecruitInfoDto fromEntity(Team team, Recruit recruit){
        RecruitInfoDto dto = new RecruitInfoDto();
        dto.setTeamId(team.getId());
        dto.setTeamName(team.getTeamName());
        dto.setTeamType(team.getTeamType().name());
        dto.setIsOnline(team.getIsOnline());
        dto.setCreatedAt(recruit.getCreatedAt());
        dto.setTeamDescribe(team.getTeamDescribe());
        dto.setIsRecruitFinished(recruit.getIsFinished());
        //teamManagerUsername은 알아서 채워라
        //techStackName은 알아서 채워라

        dto.setRecruitId(recruit.getId());
        dto.setRecruitTitle(recruit.getTitle());
        dto.setTeamRecruitDetails(recruit.getTeamRecruitDetails());
        dto.setRecruitMemberNum(recruit.getRecruitMemberNum());
        return dto;
    }
}
