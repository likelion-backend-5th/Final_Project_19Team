package com.likelion.teammatch.repository;

import com.likelion.teammatch.entity.Recruit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecruitRepository extends JpaRepository<Recruit, Long> {
    Page<Recruit> findAllByOrderByIdDesc(PageRequest pageReqeust);

    List<Recruit> findAllByTeamId(Long teamId);
}
