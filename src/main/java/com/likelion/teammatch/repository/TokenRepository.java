package com.likelion.teammatch.repository;

import com.likelion.teammatch.entity.Token;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {

    Boolean existsByAccessTokenAndRefreshToken(String accessToken, String refreshToken);

    @Transactional
    void deleteByAccessTokenAndRefreshToken(String accessToken, String refreshToken);

    Optional<Token> findByAccessTokenAndRefreshToken(String accessToken, String refreshToken);
}
