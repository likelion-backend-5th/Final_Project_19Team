package com.likelion.teammatch.utils;

import com.likelion.teammatch.entity.User;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
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
        if (request.getRequestURI().contains("/api")){
            doRestApiFilterInternal(request, response, filterChain);
        }
        else {
            doCookieFilterInternal(request, response, filterChain);
        }
    }

    private void doCookieFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String cookie = request.getHeader("Cookie");
        final String jwt;
        final String username;

        // Token이 없다면 다음 필터로 이동
        if (cookie == null || !cookie.contains("jwtToken=")){
            log.warn("No Token in Cookie!");
            log.warn("cookie : {}", cookie);
            request.setAttribute("Exception", new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED));//AUth header가 없거나 JWT가 존재하지 않음.
        }
        else {
            //토큰 parse하기
            jwt = cookie.substring(cookie.indexOf("jwtToken=") + 9);


            //사용자가 인증이 되었는지만 확인. 해당 요청에 적합한지에 대한 세부 구현적인 내용은 Service에서 검증 과정을 통해 대조하여 exception함.
            try{
                jwtTokenUtils.validate(jwt);
                SecurityContext context = SecurityContextHolder.createEmptyContext();

                username = jwtTokenUtils.parseClaims(jwt).getSubject();
                User user = new User();
                user.setUsername(username);

                AbstractAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        user,
                        jwt,
                        new ArrayList<>()
                );
                context.setAuthentication(authToken);

                SecurityContextHolder.setContext(context);
            }
            catch (ExpiredJwtException ex){
                request.setAttribute("Exception", new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED)); //JWT 만료
            }
            catch (SignatureException | MalformedJwtException ex){
                request.setAttribute("Exception", new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED)); //jwt 변조 혹은 잘못 서명됨.
            }

        }


        filterChain.doFilter(request, response);
    }

    private void doRestApiFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //Header로부터 Auth 정보 가져오기
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        //Header가 비어있거나 Bearer로 시작하지 않는다면 InvalidAuthorizationHeaderException
        if (authHeader == null || !authHeader.startsWith("Bearer ")){
            log.warn("Authorization Header is Not Valid");
            log.warn("auth header : {} " , authHeader);
            request.setAttribute("Exception", new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED));//AUth header가 없거나 JWT가 존재하지 않음.
        }
        else {
            String token = authHeader.split(" ")[1];
            try{
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
            }
            catch (ExpiredJwtException ex){
                request.setAttribute("Exception", new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED)); //JWT 만료
            }
            catch (SignatureException | MalformedJwtException ex){
                request.setAttribute("Exception", new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED)); //jwt 변조 혹은 잘못 서명됨.
            }
        }
        filterChain.doFilter(request, response);
    }
}
