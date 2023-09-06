package com.likelion.teammatch.repository;

import com.likelion.teammatch.entity.Apply;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplyRepository extends JpaRepository<Apply, Long> {

    List<Apply> findAllByRecruitId(Long recruitId);

    @Transactional
    void deleteAllByRecruitId(Long recruitId);
}
