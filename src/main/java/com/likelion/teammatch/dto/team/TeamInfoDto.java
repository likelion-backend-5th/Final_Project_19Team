package com.likelion.teammatch.dto.team;

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
public class TeamInfoDto {
    private Long id;

    private String teamName;

    private Boolean isFinished;

    private Integer memberNum;

    private Boolean isOnline;

    private String teamDescribe;

    private String teamManagerUsername;

    private String teamType;

    private List<String> teamMemberUsername = new ArrayList<>();
    private List<String> teamTechStackName = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime finishedAt;

    public static TeamInfoDto fromEntity(Team team) {
        TeamInfoDto dto = new TeamInfoDto();
        dto.setId(team.getId());
        dto.setTeamName(team.getTeamName());
        dto.setIsFinished(team.getIsFinished());
        dto.setMemberNum(team.getMemberNum());
        dto.setIsOnline(team.getIsOnline());
        dto.setTeamDescribe(team.getTeamDescribe());
        dto.setTeamType(team.getTeamType().name());
        //teamManagerUsername은 나중에 채울 것
        //team member username은 나중에 채울 것
        //team techStackName은 나중에 채울 것
        dto.setCreatedAt(team.getCreatedAt());
        if(team.getFinishedAt() != null) dto.setFinishedAt(team.getFinishedAt());
        return dto;

    }
}
