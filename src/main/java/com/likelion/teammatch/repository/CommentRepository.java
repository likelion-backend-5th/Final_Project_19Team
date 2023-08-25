package com.likelion.teammatch.repository;

import com.likelion.teammatch.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByRecruitId(Long id);
}
