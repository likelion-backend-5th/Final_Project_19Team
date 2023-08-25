package com.likelion.teammatch.dto.team;

import com.likelion.teammatch.entity.Recruit;
import com.likelion.teammatch.entity.Team;
import com.likelion.teammatch.entity.type.TeamType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class TeamCreateDto {
    private String teamName;

    //종료 여부
    private Boolean isFinished;

    //총 인원 수
    private Integer memberNum;

    //공개 프로젝트인지 아닌지 여부
    private Boolean open;

    //팀 소개글
    //(ex: Team-Matching 서비스. 기존 서비스보다 접근성이 좋고~~~ 뭐시기 뭐시기 등등을 계획중입니다.)
    private String teamDescribe;

    private String teamType;

    //정규 모임 시간 정보를 String으로 저장한다. (ex : 월수금 19:00~21:00)
    private String regularMeetingTimeInfo;

    //온라인으로 진행하는지 아니면 오프라인으로 진행할지.
    private Boolean isOnline;

    //만약 "모집 공고를 작성하시겠습니까?" 란에 체크하면 아래 있는 요소들을 작성할 수 있다.
    private Boolean publishRecruit;

    //모집 공고 이름
    private String teamRecruitName;

    //모집 공고글
    //팀 소개글과는 다르다! 모집 공고에 적고 싶은 특이사항들을 적게 할 생각이다.
    // (ex: "우리는 XX 공모전에 대비해서 진행할 생각입니다! 백엔드 부분에서 경험 많으신 분을 위주로 모집합니다! 서울에서 진행할 생각입니다!))
    private String teamRecruitDetails;

    //팀 테크스택 리스트
    private List<String> teamTechStackList = new ArrayList<>();


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
        team.setRegularMeetingTimeInfo(dto.getRegularMeetingTimeInfo());
        return team;
    }

    public static Recruit getRecruitEntity(TeamCreateDto dto){
        if (dto.getPublishRecruit()){
            Recruit recruit = new Recruit();
            recruit.setTitle(dto.getTeamRecruitName());
            recruit.setTeamRecruitDetails(dto.getTeamRecruitDetails());
            return recruit;
        }
        return null;
    }
}
