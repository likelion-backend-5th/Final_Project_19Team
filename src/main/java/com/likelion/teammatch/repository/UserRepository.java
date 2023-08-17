package com.likelion.teammatch.repository;

import com.likelion.teammatch.entity.Team;
import com.likelion.teammatch.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    Boolean existsByPhone(String phone);

    List<String> findUserNamesByTeam(@Param("team") Team team);
}
