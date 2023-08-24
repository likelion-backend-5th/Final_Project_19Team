package com.likelion.teammatch.dto;

import com.likelion.teammatch.entity.ProjectResult;
import com.likelion.teammatch.entity.Team;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ProjectResultInfoDto {
    private Long teamId;
    private String teamManagerUsername;

    private Long projectResultId;
    private String description;
    private String github;

    public static ProjectResultInfoDto fromEntity(Team team, ProjectResult projectResult){
        ProjectResultInfoDto dto = new ProjectResultInfoDto();

        dto.setTeamId(team.getId());

        dto.setProjectResultId(projectResult.getId());
        dto.setDescription(projectResult.getDescription());
        dto.setGithub(projectResult.getGithub());

        return dto;
    }
}
