package com.likelion.teammatch.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Alarm extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // 알람 유형
    private String alarmType;
    // 프로젝트 명
    private String projectName;
    // 알림 조회 여부
    private Boolean status;
    // 팀장 or 팀원
    private String memberType;
    private Long userId;
    // 수락, 거절, 탈퇴하기 등
    private String action;

    // 생성자
    protected Alarm() {
        // JPA 사용을 위해 기본 생성자 필요
}
    private Alarm(String alarmType, String projectName, String memberType, Long userId) {
        this.alarmType = alarmType;
        this.projectName = projectName;
        this.memberType = memberType;
        this.userId = userId;
        this.status = false; // 초기값 설정
        this.action = ""; // 초기값 설정
    }

    // 알람 상태 업데이트 메소드
    public void markAsRead() {
        this.status = true;
    }

    // 알람 액션 처리 메소드
    public void performAction(String action) {
        // 액션 처리 로직 분기
        if ("accept".equalsIgnoreCase(action)) {
            // 수락 처리 로직
        } else if ("reject".equalsIgnoreCase(action)) {
            // 거절 처리 로직
        } else if ("leave".equalsIgnoreCase(action)) {
            // 탈퇴 처리 로직
        } else {
            throw new IllegalArgumentException("Invalid action");
        }
        this.action = action;
    }

    // 정적 팩토리 메소드
    public static Alarm create(String alarmType, String projectName, String memberType, Long userId) {
        return new Alarm(alarmType, projectName, memberType, userId);
    }
}