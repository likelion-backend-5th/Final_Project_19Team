package com.likelion.teammatch.dto.team;

import com.likelion.teammatch.entity.Recruit;
import com.likelion.teammatch.entity.Team;
import com.likelion.teammatch.entity.type.TeamType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
@EqualsAndHashCode
public class TeamCreateDto {
    //팀 이름
    private String teamName;

    private String teamType;

    //온라인으로 진행하는지 아니면 오프라인으로 진행할지.
    private Boolean isOnline;

    //팀 테크스택 리스트
    private String techStackList;

    //팀 소개글
    //(ex: Team-Matching 서비스. 기존 서비스보다 접근성이 좋고~~~ 뭐시기 뭐시기 등등을 계획중입니다.)
    private String teamDescribe;

    //총 인원 수
    private Integer memberNum;

    //팀을 만들면서 모집 공고글을 만들건지 안 만들건지.
    private Boolean recruitCheck;


    //모집 공고 이름
    private String teamRecruitName;

    //모집 공고글
    //팀 소개글과는 다르다! 모집 공고에 적고 싶은 특이사항들을 적게 할 생각이다.
    // (ex: "우리는 XX 공모전에 대비해서 진행할 생각입니다! 백엔드 부분에서 경험 많으신 분을 위주로 모집합니다! 서울에서 진행할 생각입니다!))
    private String teamRecruitDetails;

    private String techStackWanted;




    public static Team getTeamEntity(TeamCreateDto dto){
        Team team = new Team();

        team.setTeamName(dto.getTeamName());
        if (dto.getTeamType().equals("project")) team.setTeamType(TeamType.PROJECT);
        else if (dto.getTeamType().equals("study")) team.setTeamType(TeamType.STUDY);
        else if (dto.getTeamType().equals("employment")) team.setTeamType(TeamType.EMPLOYMENT);
        else if (dto.getTeamType().equals("others")) team.setTeamType(TeamType.OTHERS);
        team.setIsOnline(dto.getIsOnline());
        team.setTeamDescribe(dto.getTeamDescribe());
        team.setDeleted(false);
        team.setMemberNum(1);//본인 포함
        return team;
    }

    public static Recruit getRecruitEntity(TeamCreateDto dto){
        if (!dto.getRecruitCheck()) return null;
        Recruit recruit = new Recruit();
        recruit.setTitle(dto.getTeamRecruitName());
        recruit.setTeamRecruitDetails(dto.getTeamRecruitDetails());
        recruit.setRecruitMemberNum(dto.getMemberNum());
        recruit.setIsFinished(false);
        recruit.setTechStackWanted(dto.getTechStackWanted());
        return recruit;
    }
}
