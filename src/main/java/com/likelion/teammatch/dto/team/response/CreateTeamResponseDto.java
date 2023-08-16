package com.likelion.teammatch.dto.team.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateTeamResponseDto {
    private String teamName;
    private Long teamId;
}