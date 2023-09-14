package com.likelion.teammatch.repository;

import com.likelion.teammatch.entity.TechStack;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TechStackRepository extends JpaRepository<TechStack, Long> {

    //기술 이름으로 TechStack을 찾도록 하는 메소드
    Optional<TechStack> findByName(String name);

    Boolean existsByName(String name);
}
