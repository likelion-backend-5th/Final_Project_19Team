package com.likelion.teammatch.service;

import com.likelion.teammatch.entity.TechStack;
import com.likelion.teammatch.entity.User;
import com.likelion.teammatch.entity.UserTechStack;
import com.likelion.teammatch.repository.TechStackRepository;
import com.likelion.teammatch.repository.UserRepository;
import com.likelion.teammatch.repository.UserTechStackRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TechStackService {
    private final UserTechStackRepository userTechStackRepository;
    private final TechStackRepository techStackRepository;
    private final UserRepository userRepository;


    public TechStackService(UserTechStackRepository userTechStackRepository, TechStackRepository techStackRepository, UserRepository userRepository) {
        this.userTechStackRepository = userTechStackRepository;
        this.techStackRepository = techStackRepository;
        this.userRepository = userRepository;
    }

    //특정 테크 스택을 User(본인)에게 연결하기
    public String addTechStackToUser(String techStackName, String skilled){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        TechStack techStack = techStackRepository.findByName(techStackName).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        UserTechStack userTechStack = UserTechStack.getInstance(user.getId(), techStack.getId(), skilled);
        userTechStackRepository.save(userTechStack);
        return "done";
    }

    //특정 테크 스택을 User(본인)에게서 지우기
    public void deleteTechStackOfUser(Long userTechStackId){
        userTechStackRepository.deleteById(userTechStackId);
    }

    //본인의 테크 스택 리스트 가져오기
    public List<UserTechStack> getTechStacksOfUser(){
        return getTechStacksOfUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    //특정 유저의 테크 스택 리스트 가져오기
    public List<UserTechStack> getTechStacksOfUserByUsername(String username){
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return userTechStackRepository.findAllByUserId(user.getId());
    }
}
