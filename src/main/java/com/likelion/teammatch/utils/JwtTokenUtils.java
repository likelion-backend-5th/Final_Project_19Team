package com.likelion.teammatch.utils;

import com.likelion.teammatch.entity.Token;
import com.likelion.teammatch.repository.TokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Component
public class JwtTokenUtils {
    private final Key signingKey;

    private final JwtParser jwtParser;
    private final TokenRepository tokenRepository;


    public JwtTokenUtils(@Value("${jwt.secret}") String jwtSecret, TokenRepository tokenRepository) {
        this.signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        this.tokenRepository = tokenRepository;

        this.jwtParser = Jwts.parserBuilder()
                .setSigningKey(this.signingKey)
                .build();
    }

    public String generateToken(UserDetails userDetails){
        return generateTokenByUsername(userDetails.getUsername());
    }

    public String generateTokenByUsername(String username){
        Claims jwtClaims = Jwts.claims()
                .setSubject(username)
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusSeconds(3600)));//1시간

        return Jwts.builder()
                .setClaims(jwtClaims)
                .signWith(signingKey)
                .compact();
    }
    
    public String generateRefreshTokenByUsername(String username){
        Claims jwtClaims = Jwts.claims()
                .setSubject(username)
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusSeconds(3600 * 6)));//12시간

        return Jwts.builder()
                .setClaims(jwtClaims)
                .signWith(signingKey)
                .compact();
    }

    public boolean validate(String token){
        try {
            jwtParser.parseClaimsJws(token);
            return true;
        }
        catch (Exception ex){
            log.info("Jwt validate Exception : {}", ex.toString());
            return false;
        }
    }

    public Claims parseClaims(String token){

        return jwtParser.
                parseClaimsJws(token)
                .getBody();
    }

    public String getSubjectFromExpiredAndNoneExpiredJwt(String token){
        try{
            return jwtParser.parseClaimsJws(token).getBody().getSubject();

        }
        catch (ExpiredJwtException ex){
            return ex.getClaims().getId();
        }
    }

    //해당 토큰 정보가 DB에 저장되어 있는지, 발급 대상이 일치하는지, refreshToken이 유효한지를 체크한다.
    public Boolean checkAccessAndRefreshToken(String accessToken, String refreshToken) {
        //refreshToken이 유효하지 않다면 false
        if (!validate(refreshToken)){
            log.info("validation failed!");
            return false;
        }

//        //refreshToken과 accessToken의 username(subject)가 같은지 확인
//        if (!getSubjectFromExpiredAndNoneExpiredJwt(accessToken).equals(getSubjectFromExpiredAndNoneExpiredJwt(refreshToken))){
//            throw new ResponseStatusException(HttpStatus.CONFLICT);
//        }


        //refreshToken, accessToken이 DB에 존재한다면 true. 즉, 문제없이 작동하는 것. 만약 아니라면 쿠키가 탈취당한 상황.
        return tokenRepository.existsByAccessTokenAndRefreshToken(accessToken, refreshToken);
    }

    //해당 토큰 정보를 삭제한다.
    @Transactional
    public void deleteAccessAndRefreshToken(String accessToken, String refreshToken) {
        tokenRepository.deleteByAccessTokenAndRefreshToken(accessToken, refreshToken);
    }


    //기존 토큰을 지우고, 새로운 토큰 정보를 추가한다.
    public void updateAccessAndRefreshToken(String accessToken, String refreshToken, String generatedAccessToken, String generatedRefreshToken) {

        Token token;
        Optional<Token> optionalOldToken = tokenRepository.findByAccessTokenAndRefreshToken(accessToken, refreshToken);

        if (optionalOldToken.isPresent()){
            token = optionalOldToken.get();
        }
        else {
            token = new Token();
        }
        token.setAccessToken(generatedAccessToken);
        token.setRefreshToken(generatedRefreshToken);
        tokenRepository.save(token);
    }

    public Cookie generateAccessCookie(String username) {
        String token = generateTokenByUsername(username);
        Cookie cookie = new Cookie("accessToken", token);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(3600);
        return cookie;
    }

    public Cookie generateRefreshCookie(String username){
        String token = generateTokenByUsername(username);
        Cookie cookie = new Cookie("refreshToken", token);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(3600 * 6);
        return cookie;
    }
}