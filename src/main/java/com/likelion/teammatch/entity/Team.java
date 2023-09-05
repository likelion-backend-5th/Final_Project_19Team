package com.likelion.teammatch.entity;

import com.likelion.teammatch.entity.type.TeamType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
public class Team extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.PACKAGE)
    private Long id;

    private String teamName;

    @Enumerated(EnumType.STRING)
    private TeamType teamType;

    //온라인으로 진행하는지 아니면 오프라인으로 진행할지.
    private Boolean isOnline;

    //팀 소개글
    //(ex: Team-Matching 서비스. 기존 서비스보다 접근성이 좋고~~~ 뭐시기 뭐시기 등등을 계획중입니다.)
    @Column(columnDefinition = "TEXT")
    private String teamDescribe;

    //현재 인원수
    //Recruit의 memberRecruitNum 과는 다르다! memberRecruitNum은 목표로 하는 팀 인원수이다.
    private Integer memberNum;




    //팀 매니저의 유저 id
    private Long teamMangerId;

    //삭제 여부 (soft delete)
    private Boolean deleted;


    //종료 여부
    private Boolean isFinished;
    //프로젝트 종료 시간.
    private LocalDateTime finishedAt;
}