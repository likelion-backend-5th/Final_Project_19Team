package com.likelion.teammatch.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class CreateRecruitDto {
    private String teamRecruitName;
    private String teamRecruitDetails;
    private Integer memberNum;
}
