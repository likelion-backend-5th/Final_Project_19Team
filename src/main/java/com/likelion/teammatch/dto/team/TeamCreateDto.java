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
    private String teamName;

    //총 인원 수
    private Integer memberNum;

    //공개 프로젝트인지 아닌지 여부
    private Boolean open;

    //팀 소개글
    //(ex: Team-Matching 서비스. 기존 서비스보다 접근성이 좋고~~~ 뭐시기 뭐시기 등등을 계획중입니다.)
    private String teamDescribe;

    private String teamType;

    //온라인으로 진행하는지 아니면 오프라인으로 진행할지.
    private Boolean isOnline;

    //모집 공고 이름
    private String teamRecruitName;

    //모집 공고글
    //팀 소개글과는 다르다! 모집 공고에 적고 싶은 특이사항들을 적게 할 생각이다.
    // (ex: "우리는 XX 공모전에 대비해서 진행할 생각입니다! 백엔드 부분에서 경험 많으신 분을 위주로 모집합니다! 서울에서 진행할 생각입니다!))
    private String teamRecruitDetails;

    //팀 테크스택 리스트
    private String teamTechStackList;


    public static Team getTeamEntity(TeamCreateDto dto){
        Team team = new Team();

        team.setTeamName(dto.getTeamName());
        team.setTeamDescribe(dto.getTeamDescribe());
        team.setDeleted(false);
        team.setOpen(dto.getOpen());
        team.setType(TeamType.valueOf(dto.getTeamType()));
        team.setOnRecruit(true);
        team.setIsOnline(dto.getIsOnline());
        team.setMemberNum(1);//본인 포함
        return team;
    }

    public static Recruit getRecruitEntity(TeamCreateDto dto){
        Recruit recruit = new Recruit();
        recruit.setTitle(dto.getTeamRecruitName());
        recruit.setTeamRecruitDetails(dto.getTeamRecruitDetails());
        return recruit;
    }
}
