package com.likelion.teammatch.repository;


import com.likelion.teammatch.entity.TeamTechStack;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamTechStackRepository extends JpaRepository<TeamTechStack, Long> {
    List<TeamTechStack> findAllByTeamId(Long teamId);

    List<TeamTechStack> findAllByTechStackId(Long teamId);
}
