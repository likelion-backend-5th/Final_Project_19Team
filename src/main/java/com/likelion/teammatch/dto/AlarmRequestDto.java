package com.likelion.teammatch.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlarmRequestDto {
    private String alarmType;
    private String projectName;
    private String memberType;
    private Long userId;
    private String action;
}
