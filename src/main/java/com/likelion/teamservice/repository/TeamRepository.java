package com.likelion.teamservice.repository;

import com.likelion.teamservice.entity.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
    void deleteByIdAndAdminId(Long teamId, String adminId);
}
