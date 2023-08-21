package com.likelion.teammatch.utils;

import com.likelion.teammatch.entity.User;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;

@Slf4j
@Component
public class JwtTokenFilters extends OncePerRequestFilter {

    private final JwtTokenUtils jwtTokenUtils;

    public JwtTokenFilters(JwtTokenUtils jwtTokenUtils) {
        this.jwtTokenUtils = jwtTokenUtils;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();
        Cookie accessTokenCookie = null;
        Cookie refreshTokenCookie = null;

        for (Cookie cookie : cookies){
            if (cookie.getName().equals("accessToken")) accessTokenCookie = cookie;
            if (cookie.getName().equals("refreshToken")) refreshTokenCookie = cookie;
        }

        if (accessTokenCookie == null || refreshTokenCookie == null){
            log.warn("Auth Cookie is null");
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = accessTokenCookie.getValue();
        String refreshToken = refreshTokenCookie.getValue();

        try {
            jwtTokenUtils.validate(accessToken);
            SecurityContext context = SecurityContextHolder.createEmptyContext();

            String username = jwtTokenUtils.parseClaims(accessToken).getSubject();
            User user = new User();
            user.setUsername(username);

            AbstractAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    user,
                    accessToken,
                    new ArrayList<>()
            );
            context.setAuthentication(authToken);

            SecurityContextHolder.setContext(context);

            log.info("no problem");
            filterChain.doFilter(request, response);
        }
        catch (ExpiredJwtException ex){
            log.info("access token expired!");
            //refresh Token 체크
            if (!jwtTokenUtils.checkAccessAndRefreshToken(accessToken, refreshToken)) {
                log.warn("refreshToken is not Valid!");
                filterChain.doFilter(request, response);
                return;
            }

            // refreshToken no problem!
            // 문제가 없다면 accessToken과 refreshToken 업데이트 (Refresh Token Rotation)
            String username = jwtTokenUtils.parseClaims(refreshToken).getSubject();
            String generatedAccessToken = jwtTokenUtils.generateTokenByUsername(username);
            String generatedRefreshToken = jwtTokenUtils.generateRefreshTokenByUsername(username);
            jwtTokenUtils.updateAccessAndRefreshToken(accessToken, refreshToken, generatedAccessToken, generatedRefreshToken);
            accessTokenCookie.setValue(generatedAccessToken);
            refreshTokenCookie.setValue(generatedRefreshToken);
            accessTokenCookie.setMaxAge(3600 * 10);//10시간
            refreshTokenCookie.setMaxAge(3600 * 10);
            accessTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setHttpOnly(true);

            response.addCookie(accessTokenCookie);
            response.addCookie(refreshTokenCookie);

            //인증 객체 생성 후 적용. (즉, 업데이트 이후에도 접속 상태를 유지시킴)
            SecurityContext context = SecurityContextHolder.createEmptyContext();

            User user = new User();
            user.setUsername(username);

            AbstractAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    user,
                    accessToken,
                    new ArrayList<>()
            );
            context.setAuthentication(authToken);

            SecurityContextHolder.setContext(context);
            filterChain.doFilter(request, response);
        }
        catch (Exception ex){
            filterChain.doFilter(request, response);
            return;
        }

    }



    private void doRestApiFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //Header로부터 Auth 정보 가져오기
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        //Header가 비어있거나 Bearer로 시작하지 않는다면 InvalidAuthorizationHeaderException
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Authorization Header is Not Valid");
            log.warn("auth header : {} ", authHeader);
            request.setAttribute("Exception", new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED));//AUth header가 없거나 JWT가 존재하지 않음.
            filterChain.doFilter(request, response);
            return;
        }
        String token = authHeader.split(" ")[1];

        try {
            jwtTokenUtils.validate(token);
            SecurityContext context = SecurityContextHolder.createEmptyContext();

            String username = jwtTokenUtils.parseClaims(token).getSubject();
            User user = new User();
            user.setUsername(username);

            AbstractAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    user,
                    token,
                    new ArrayList<>()
            );
            context.setAuthentication(authToken);

            SecurityContextHolder.setContext(context);
        } catch (ExpiredJwtException ex) {
            String refreshHeader = request.getHeader("RefreshToken");

            if (refreshHeader == null || !refreshHeader.startsWith("Bearer ")){
                log.warn("RefreshToken header is not valid!");
                log.warn("RefreshToken header : {}", refreshHeader);
                request.setAttribute("Exception", new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED));
                filterChain.doFilter(request, response);
                return;
            }

            String refreshToken = refreshHeader.split(" ")[1];
            
        } catch (SignatureException | MalformedJwtException ex) {
            request.setAttribute("Exception", new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED)); //jwt 변조 혹은 잘못 서명됨.
        }
    }



}
