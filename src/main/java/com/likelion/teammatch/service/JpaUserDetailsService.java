package com.likelion.teammatch.service;

import com.likelion.teammatch.entity.User;
import com.likelion.teammatch.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

/*
SecurityFilterChain에서 사용하는 UserDetailsService 구현체. Security Config 제외하고 이걸 사용해서는 안된다!!!!!
 */
@Service
public class JpaUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public JpaUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);//유저네임에 해당하는 유저를 찾을 수 없음.
        return optionalUser.get();
    }
}
