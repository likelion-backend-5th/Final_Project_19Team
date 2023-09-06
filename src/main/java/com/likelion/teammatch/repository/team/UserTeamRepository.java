package com.likelion.teammatch.repository.team;

import com.likelion.teammatch.entity.UserTeam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserTeamRepository extends JpaRepository<UserTeam, Long> {
    //해당 userId를 가진 user가 해당 teamId를 가진 team에 참가해있는지 확인.
    Boolean existsByUserIdAndTeamId(Long userId, Long teamId);

    //해당 team에 가입한 모든 팀가입정보 가져오기
    List<UserTeam> findAllByTeamId(Long teamId);

    //해당 userId를 가진 유저가 가입한 모든 팀가입정보 가져오기
    List<UserTeam> findAllByUserId(Long id);

    // userId와 teamId를 기반으로 특정 유저를 해당 팀에서 삭제
    void deleteByUserIdAndTeamId(Long userId, Long teamId);

    // teamId를 기반으로 해당 팀의 모든 멤버 정보 삭제
    void deleteAllByTeamId(Long teamId);
}
