package com.likelion.teammatch.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.PACKAGE)
    private Long id;

    private String teamName;

    private Boolean isFinished;

    private Integer memberNum;

    private Boolean onRecruit;

    private Boolean open;

    @Column(columnDefinition = "TEXT")
    private String teamDescribe;

    private Long teamMangerId;

    private Boolean deleted;

    private LocalDateTime createdAt;

    private LocalDateTime finishedAt;
}