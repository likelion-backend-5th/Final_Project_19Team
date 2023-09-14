package com.likelion.teammatch.dto;

import com.likelion.teammatch.entity.Comment;
import com.likelion.teammatch.entity.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CommentInfoDto {
    private Long userId;
    private String username;

    private Long commentId;
    private String content;

    public static CommentInfoDto fromEntity(User user, Comment comment) {
        CommentInfoDto dto = new CommentInfoDto();

        dto.setUserId(user.getId());
        dto.setUsername(user.getUsername());

        dto.setCommentId(comment.getId());
        dto.setContent(comment.getContent());
        return dto;
    }
}
