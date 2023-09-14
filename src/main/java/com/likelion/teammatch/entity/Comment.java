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
public class Comment extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Recruit에 달린 Comment인지 아닌지
    private Boolean isRecruitComment;

    //Recruit에 달린 것이라면 해당 Recruit의 아이디, 만약 ProjectResult에 달린 것이라면 해당 ProjectResult의 아이디.
    private Long boardId;

    //댓글 작성자
    private Long userId;
    //댓글 내용
    private String content;
}
