package com.likelion.teammatch.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
public class Recruit extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.PACKAGE)
    private Long id;

    private Long teamManagerId;
    private Long teamId;
    
    //모집 공고 제목
    private String title;
    //모집 공고 추가 소개글
    private String teamRecruitDetails;
    //모집 목표 인원수
    private Integer recruitMemberNum;

    //모집 종료되었는지 여부
    private Boolean isFinished;


    //모집 공고 관련 techStack.
    private String techStackWanted;
//    private Boolean badge;
}
