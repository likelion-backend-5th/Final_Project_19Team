package com.likelion.teammatch.dto;

import com.likelion.teammatch.entity.Recruit;
import com.likelion.teammatch.entity.Team;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

//아직까지 필요가 없을 것 같음.
@Getter
@Setter
@ToString
public class RecruitDraftDto {
    private Long recruitId;
    private String recruitTitle;
    private String teamType;
    private List<String> techStackList = new ArrayList<>();
    private Boolean isFinished;

    private String recruitDetails;

    private Integer commentNum;
    private String imageUrl;

    public static RecruitDraftDto fromEntity(Team team, Recruit recruit){
        RecruitDraftDto dto = new RecruitDraftDto();
        dto.setRecruitId(recruit.getId());
        dto.setRecruitTitle(recruit.getTitle());
        dto.setTeamType(team.getTeamType().name());
        //techstackList는 알아서 채워라
        dto.setIsFinished(recruit.getIsFinished());
        dto.setRecruitDetails(recruit.getTeamRecruitDetails().substring(0, Math.min(recruit.getTeamRecruitDetails().length(), 30)) + "...");
        //commnetNum은 알아서 채워라
        dto.setImageUrl(recruit.getImageFileName());
        return dto;
    }
}
