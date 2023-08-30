package com.likelion.teammatch.service;

import com.likelion.teammatch.dto.ReviewInfoDto;
import com.likelion.teammatch.entity.Review;
import com.likelion.teammatch.entity.Team;
import com.likelion.teammatch.entity.User;
import com.likelion.teammatch.repository.ReviewRepository;
import com.likelion.teammatch.repository.UserRepository;
import com.likelion.teammatch.repository.team.TeamRepository;
import com.likelion.teammatch.repository.team.UserTeamRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final UserTeamRepository userTeamRepository;

    public ReviewService(ReviewRepository reviewRepository, TeamRepository teamRepository, UserRepository userRepository, UserTeamRepository userTeamRepository) {
        this.reviewRepository = reviewRepository;
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.userTeamRepository = userTeamRepository;
    }

    //리뷰를 따로 추가하는 메소드
    private Long createReview(Long teamId, String reviewDetails, String role) {
        //현재 사용자의 username 가져오기
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        //user Entity 가져오기
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        //Team entity 가져오기
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        //리뷰 수정 권한 확인하기
        if (!userTeamRepository.existsByUserIdAndTeamId(user.getId(), team.getId())) throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        Review review = new Review();
        review.setDescribe(reviewDetails);
        review.setRole(role);
        review.setUserId(user.getId());
        review = reviewRepository.save(review);

        return review.getId();
    }

    //리뷰 수정
    public Long updateReview(Long reviewId, String reviewDetails, String role) {
        //현재 사용자의 username 가져오기
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        //user Entity 가져오기
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        //ReviewEntity 가져오기
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        //리뷰 수정 권한 확인하기
        if (!user.getId().equals(review.getUserId())) throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        review.setDescribe(reviewDetails);
        review.setRole(role);
        review = reviewRepository.save(review);

        return review.getId();
    }

    //리뷰 삭제
    @Transactional
    public void deleteReview(Long reviewId) {
        //현재 사용자의 username 가져오기
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        //user Entity 가져오기
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        //ReviewEntity 가져오기
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        //리뷰 삭제 권한 확인하기
        if (!user.getId().equals(review.getUserId())) throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        reviewRepository.deleteById(reviewId);
    }

    //리뷰 가져오기
    public ReviewInfoDto getReviewInfo(Long reviewId) {
        //Review 가져오기
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        //해당 Review 에 연결된 User 가져오기
        User user = userRepository.findById(review.getUserId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return ReviewInfoDto.fromEntity(user, review);
    }
}
