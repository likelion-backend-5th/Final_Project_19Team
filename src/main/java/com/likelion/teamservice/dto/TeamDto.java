package com.likelion.teamservice.dto;

import com.likelion.teamservice.entity.Team;
import lombok.Builder;
import org.springframework.security.core.userdetails.User;

import java.time.LocalDateTime;

@Builder
public class TeamDto {
    private Long id;
    private String name;
    private Boolean isFinished;
    private Integer memberNum;
    private Boolean onRecruit;
    private Boolean open;
    private LocalDateTime dateTime;
    private Boolean deleted;

    public static TeamDto fromEntity(Team entity) {
        return TeamDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .isFinished(entity.isFinished())
                .memberNum(entity.getMemberNum())
                .onRecruit(entity.isOnRecruit())
                .open(entity.isOpen())
                .dateTime(entity.getDateTime())
                .deleted(entity.isDeleted())
                .build();
    }

    public Team toEntity(User user) {
        return Team.builder()
                .adminId(user.getUsername())
                .name(name)
                .isFinished(isFinished)
                .memberNum(memberNum)
                .onRecruit(onRecruit)
                .open(open)
                .dateTime(dateTime)
                .deleted(deleted)
                .build();
    }
}
