package com.likelion.teammatch.repository.team;

import com.likelion.teammatch.dto.team.response.SearchTeamResponseDto;
import com.likelion.teammatch.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {
    List<SearchTeamResponseDto> findAllTeamInfo();
    List<SearchTeamResponseDto> findByKeyword(@Param("keyword") String keyword);
}
