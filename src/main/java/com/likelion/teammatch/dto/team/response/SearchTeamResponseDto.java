package com.likelion.teammatch.dto.team.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SearchTeamResponseDto {    // 팀 검색 결과로 보내줌
    private String teamName;
    private Long teamId;
}
