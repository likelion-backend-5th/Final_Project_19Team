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
public class Review extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long projectResultId;

    //동료 평가점수의 평균
    private Long grade;
    //프로젝트에서 담당했던 역할
    private String role;
    //해당 팀원이 적은 본인의 기여도와 소감
    private String describe;
    //포기 여부
    private Boolean giveUp;
}
