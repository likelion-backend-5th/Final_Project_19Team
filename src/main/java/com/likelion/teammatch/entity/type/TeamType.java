package com.likelion.teammatch.entity.type;

public enum TeamType {
    PROJECT("프로젝트"),
    STUDY("스터디"),
    EMPLOYMENT("취준"),
    OTHERS("기타");

    private String type;

    TeamType(String type) {
        this.type = type;
    }
}