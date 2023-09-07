package com.likelion.teammatch.config;


import com.likelion.teammatch.service.JpaUserDetailsService;
import com.likelion.teammatch.utils.JwtTokenFilters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
    private final JwtTokenFilters jwtTokenFilter;
    private final JpaUserDetailsService jpaUserDetailsService;

    public SecurityConfig(JwtTokenFilters jwtTokenFilter, JpaUserDetailsService jpaUserDetailsService) {
        this.jwtTokenFilter = jwtTokenFilter;
        this.jpaUserDetailsService = jpaUserDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authRequest -> authRequest
                        .requestMatchers("/api/issue", "/api/register","/login", "/register")
                        .anonymous()
                        .requestMatchers( "/css/**", "/js/**", "/assets/**","/main","/api/techStackExists","/team/**")
                        .permitAll()
                        .anyRequest()
                        .authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .sessionManagement(manage -> manage
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterAfter(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout -> logout
                                .logoutUrl("/logout")
                                .invalidateHttpSession(true)
                                .deleteCookies("accessToken", "refreshToken")
                                .logoutSuccessUrl("/main")
                );

        return http.build();


    }


    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(jpaUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
}
