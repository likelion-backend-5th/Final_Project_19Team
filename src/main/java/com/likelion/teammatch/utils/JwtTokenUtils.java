package com.likelion.teammatch.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenUtils {
    private final Key signingKey;

    private final JwtParser jwtParser;


    public JwtTokenUtils(@Value("${jwt.secret}") String jwtSecret) {
        this.signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());

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
                .setExpiration(Date.from(Instant.now().plusSeconds(3600)));

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
}