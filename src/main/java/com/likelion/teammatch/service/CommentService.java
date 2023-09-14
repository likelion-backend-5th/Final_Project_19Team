package com.likelion.teammatch.service;

import com.likelion.teammatch.dto.CommentInfoDto;
import com.likelion.teammatch.entity.Comment;
import com.likelion.teammatch.entity.User;
import com.likelion.teammatch.repository.CommentRepository;
import com.likelion.teammatch.repository.ProjectResultRepository;
import com.likelion.teammatch.repository.RecruitRepository;
import com.likelion.teammatch.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommentService {
    private final ProjectResultRepository projectResultRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final RecruitRepository recruitRepository;

    public CommentService(ProjectResultRepository projectResultRepository, CommentRepository commentRepository, UserRepository userRepository, RecruitRepository recruitRepository) {
        this.projectResultRepository = projectResultRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.recruitRepository = recruitRepository;
    }

    //댓글 생성
    public Long createComment(Long boardId, String commentDetails, Boolean isRecruit) {
        //현재 사용자의 username 가져오기
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        //user Entity 가져오기
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Comment comment = new Comment();
        comment.setIsRecruitComment(isRecruit);
        comment.setContent(commentDetails);
        comment.setUserId(user.getId());
        comment.setBoardId(boardId);
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
    @Transactional
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

    //모집 공고글 댓글 전체 조회
    public List<CommentInfoDto> getCommentsForRecruit(Long recruitId) {

        //entity 가져오기
        List<Comment> entityList = commentRepository.findAllByIsRecruitCommentAndBoardId(true, recruitId);

        List<CommentInfoDto> dtoList = new ArrayList<>();

        for (Comment entity: entityList){
            //User 가져오기
            User writer = userRepository.findById(entity.getUserId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            CommentInfoDto dto = CommentInfoDto.fromEntity(writer, entity);
            dtoList.add(dto);
        }

        return dtoList;
    }
}
