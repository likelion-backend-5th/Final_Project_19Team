package com.likelion.teammatch.dto.team;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamDraftDto {
    private String teamName;
    private Boolean isFinished;
    private Long teamId;
    private Boolean isManager;
    private Boolean isOnline;
    private Integer memberNum;
}
