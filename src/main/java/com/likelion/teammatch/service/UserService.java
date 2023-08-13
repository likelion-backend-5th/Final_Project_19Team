package com.likelion.teammatch.service;

import com.likelion.teammatch.dto.RegisterDto;
import com.likelion.teammatch.entity.User;
import com.likelion.teammatch.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void createUser(RegisterDto dto){
        if (userRegisterConflicts(dto)) throw new ResponseStatusException(HttpStatus.CONFLICT);//Username Conflict!

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());

        userRepository.save(user);
    }

    public Boolean userRegisterConflicts(RegisterDto dto){
        if (userRepository.existsByUsername(dto.getUsername())) return true;
        if (userRepository.existsByEmail(dto.getEmail())) return true;
        if (userRepository.existsByPhone(dto.getPhone())) return true;
        return false;
    }

    public Boolean usernameExists(String username){
        return userRepository.existsByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);//유저네임에 해당하는 유저를 찾을 수 없음.
        return optionalUser.get();
    }
}
