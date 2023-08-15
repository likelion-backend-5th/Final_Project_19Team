package com.likelion.teamservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Setter
    private Long id;

    @Setter
    private String adminId;

    @Setter
    private String name;

    @Setter
    private boolean isFinished;

    @Setter
    private Integer memberNum;

    @Setter
    private boolean onRecruit;

    @Setter
    private boolean open;

    @Setter
    private LocalDateTime dateTime;

    @Setter
    private boolean deleted;

    @Builder
    private Team(String adminId, String name, boolean isFinished, Integer memberNum, boolean onRecruit, boolean open, LocalDateTime dateTime, boolean deleted) {
        this.adminId = adminId;
        this.name = name;
        this.isFinished = isFinished;
        this.memberNum = memberNum;
        this.onRecruit = onRecruit;
        this.open = open;
        this.dateTime = dateTime;
        this.deleted = deleted;
    }
}
