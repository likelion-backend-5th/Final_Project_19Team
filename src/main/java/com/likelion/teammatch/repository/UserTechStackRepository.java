package com.likelion.teammatch.repository;

import com.likelion.teammatch.entity.UserTechStack;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
public interface UserTechStackRepository extends JpaRepository<UserTechStack, Long> {
    //특정 유저가 가지고 있는 모든 tech stack을 리스트로 반환하는 메소드
    List<UserTechStack> findAllByUserId(Long userId);



    //특정 techStack에 연관이 있는 사람의 수를 리턴하는 메소드
    Long countAllByTechStackId(Long techStackId);

    void deleteAllByUserId(Long id);
}
