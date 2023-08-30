package com.likelion.teammatch.dto;

import com.likelion.teammatch.entity.Review;
import com.likelion.teammatch.entity.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReviewInfoDto {
    private Long userId;
    private String username;

    private Long reviewId;

    private Long grade;
    private String role;
    private String describe;
    private Boolean giveUp;

    public static ReviewInfoDto fromEntity(User user, Review review) {
        ReviewInfoDto dto = new ReviewInfoDto();

        dto.setUserId(user.getId());
        dto.setUsername(user.getUsername());

        dto.setReviewId(review.getId());
        dto.setGrade(review.getGrade());
        dto.setRole(review.getRole());
        dto.setDescribe(review.getDescribe());
        dto.setGiveUp(review.getGiveUp());

        return dto;
    }
}
