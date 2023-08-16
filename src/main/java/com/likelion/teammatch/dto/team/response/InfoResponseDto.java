package com.likelion.teammatch.dto.team.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class InfoResponseDto {
    private String teamName;
    private List<String> members;
}
