package com.likelion.teammatch.repository;

import com.likelion.teammatch.entity.Recruit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecruitRepository extends JpaRepository<Recruit, Long> {
    Page<Recruit> findAllByOrderByIdDesc(PageRequest pageReqeust);

    List<Recruit> findAllByTeamId(Long teamId);

    @Query("SELECT e FROM Recruit e WHERE e.techStackWanted LIKE %:searchTerm% OR e.title LIKE %:searchTerm%")
    Page<Recruit> searchByTechStackWantedOrTitleOOrderByIdDesc(PageRequest pageRequest, @Param("searchTerm") String searchTerm);
}
