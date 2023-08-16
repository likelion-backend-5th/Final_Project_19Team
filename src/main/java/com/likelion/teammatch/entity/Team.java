package com.likelion.teammatch.entity;

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
    private Long id;

    private String teamName;

    private boolean isFinished;

    private Integer memberNum;

    private boolean onRecruit;

    private boolean open;

    private LocalDateTime dateTime;

    private boolean deleted;

    public Team(String teamName) {
        this.teamName = teamName;
    }
}
