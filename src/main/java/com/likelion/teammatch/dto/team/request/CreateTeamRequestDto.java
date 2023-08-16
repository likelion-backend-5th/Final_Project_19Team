package com.likelion.teammatch.dto.team.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor // request 에는 @Getter, @NoArgsConstructor 필수
public class CreateTeamRequestDto {
    private String teamName;
}
