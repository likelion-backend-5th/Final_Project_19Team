package com.likelion.teammatch.dto.team;

import com.likelion.teammatch.entity.Team;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class TeamInfoDto {
    private Long id;

    private String teamName;

    private Boolean isFinished;

    private Integer memberNum;

    private Boolean onRecruit;

    private Boolean open;

    private String teamDescribe;

    private String teamManagerUsername;

    private List<String> teamMemberUsername = new ArrayList<>();

    private String createdAt;
    private String finishedAt;

    public static TeamInfoDto fromEntity(Team team) {
        TeamInfoDto dto = new TeamInfoDto();
        dto.setId(team.getId());
        dto.setTeamName(team.getTeamName());
        dto.setIsFinished(team.getIsFinished());
        dto.setMemberNum(team.getMemberNum());
        dto.setOnRecruit(team.getOnRecruit());
        dto.setOpen(team.getOpen());
        dto.setTeamDescribe(team.getTeamDescribe());
        //teamManagerUsername은 나중에 채울 것
        //team member username은 나중에 채울 것
        dto.setCreatedAt(team.getCreatedAt().toString());
        dto.setFinishedAt(team.getFinishedAt().toString());
        return dto;

    }
}
