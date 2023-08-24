package com.likelion.teammatch.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class ProjectResult extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //팀매니저
    private Long teamManagerId;
    private Long teamId;

    //프로젝트에 대한 설명(팀 매니저가)
    private String description;
    //깃허브 주소
    private String github;
    //중도 포기율
    private Long giveUpRate;
    //공개 or 비공개
    private Boolean open;
}
