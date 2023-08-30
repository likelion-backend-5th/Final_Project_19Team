package com.likelion.teammatch.service;

import com.likelion.teammatch.dto.CommentInfoDto;
import com.likelion.teammatch.entity.Comment;
import com.likelion.teammatch.entity.User;
import com.likelion.teammatch.repository.CommentRepository;
import com.likelion.teammatch.repository.ProjectResultRepository;
import com.likelion.teammatch.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CommentService {
    private final ProjectResultRepository projectResultRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public CommentService(ProjectResultRepository projectResultRepository, CommentRepository commentRepository, UserRepository userRepository) {
        this.projectResultRepository = projectResultRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }

    //댓글 생성
    public Long createComment(Long recruitId, String commentDetails) {
        //현재 사용자의 username 가져오기
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        //user Entity 가져오기
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Comment comment = new Comment();
        comment.setContent(comment.getContent());
        comment.setUserId(user.getId());
        comment = commentRepository.save(comment);

        return comment.getId();
    }

    //댓글 수정
    public Long updateComment(Long commentId, String commentDetails) {
        //현재 사용자의 username 가져오기
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        //user Entity 가져오기
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        //commentEntity 가져오기
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        //댓글 수정 권한 확인하기
        if (!user.getId().equals(comment.getUserId())) throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        comment.setContent(commentDetails);
        comment = commentRepository.save(comment);

        return comment.getId();
    }

    //댓글 삭제
    public void deleteComment(Long commentId) {
        //현재 사용자의 username 가져오기
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        //user Entity 가져오기
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        //commentEntity 가져오기
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        //댓글 삭제 권한 확인하기
        if (!user.getId().equals(comment.getUserId())) throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        commentRepository.deleteById(commentId);
    }

    //댓글 전체 조회
    public CommentInfoDto getCommentInfo(Long commentId) {
        //ProjectResult 가져오기
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        //해당 Comment 에 연결된 User 가져오기
        User user = userRepository.findById(comment.getUserId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return CommentInfoDto.fromEntity(user, comment);
    }
}
