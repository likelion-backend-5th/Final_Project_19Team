package com.likelion.teammatch.repository.team;

import com.likelion.teammatch.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {
    Optional<Team> findByIdAndDeletedFalse(Long id);
}
