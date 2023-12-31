package com.likelion.teammatch.service;

import com.likelion.teammatch.dto.JwtTokenDto;
import com.likelion.teammatch.dto.LoginDto;
import com.likelion.teammatch.dto.RegisterDto;
import com.likelion.teammatch.dto.UserProfileDto;
import com.likelion.teammatch.entity.*;
import com.likelion.teammatch.repository.TechStackRepository;
import com.likelion.teammatch.repository.TokenRepository;
import com.likelion.teammatch.repository.UserRepository;
import com.likelion.teammatch.repository.UserTechStackRepository;
import com.likelion.teammatch.utils.JwtTokenUtils;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtils jwtTokenUtils;
    private final TokenRepository tokenRepository;
    private final UserTechStackRepository userTechStackRepository;
    private final TechStackRepository techStackRepository;


    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenUtils jwtTokenUtils, TokenRepository tokenRepository, UserTechStackRepository userTechStackRepository, TechStackRepository techStackRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtils = jwtTokenUtils;
        this.tokenRepository = tokenRepository;
        this.userTechStackRepository = userTechStackRepository;
        this.techStackRepository = techStackRepository;
    }

    //회원가입
    public void createUser(RegisterDto dto){
        dto.setPassword(passwordEncoder.encode(dto.getPassword()));//encode 해주기!
        if (userRegisterConflicts(dto)) throw new ResponseStatusException(HttpStatus.CONFLICT);//Username Conflict!

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setGrade(2000);//기본 점수 실버 2000점
        user.setGiveUpCount(0);//기본 중도포기 횟수 0건

        user = userRepository.save(user);

        String[] techStackList = dto.getTechStackList().split("/");

        for (String techStackString : techStackList){
            TechStack techStack = techStackRepository.findByName(techStackString).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            UserTechStack userTechStack = new UserTechStack();
            userTechStack.setUserId(user.getId());
            userTechStack.setTechStackId(techStack.getId());
            userTechStackRepository.save(userTechStack);
        }
    }

    //회원가입시 username과 email, phone이 중복인지 아닌지 검사
    public Boolean userRegisterConflicts(RegisterDto dto){
        if (userRepository.existsByUsername(dto.getUsername())) return true;
        if (userRepository.existsByEmail(dto.getEmail())) return true;
        if (userRepository.existsByPhone(dto.getPhone())) return true;
        return false;
    }

    //username 중복인지 아닌지 검사.
    public Boolean usernameExists(String username){
        return userRepository.existsByUsername(username);
    }


    //email 중복인지 검사
    public Boolean emailExists(String email){
        return userRepository.existsByEmail(email);
    }

    //phone 중복인지 검사
    public Boolean phoneExists(String phone) {
        return userRepository.existsByPhone(phone);
    }

    //로그인 시도를 판별하는 메소드
    //만약 유저네임과 비밀번호가 유효하다면 true.
    //잘못된 유저네임 혹은 잘못된 비밀번호는 false.
    public Boolean isLoginAttemptValid(LoginDto loginDto){
        //입력한 유저네임으로 해당 유저 엔티티 찾기
        String username = loginDto.getUsername();
        Optional<User> loginAttemptUser = userRepository.findByUsername(username);

        if (loginAttemptUser.isEmpty()) return false;

        User user = loginAttemptUser.get();

        //입력한 비밀번호 비교하기
        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) return false;

        return true;
    }



    //로그인 정보를 가지고 token(access, refresh)를 발급하는 메소드
    //isLoginAttemptValid를 통과해야지만 호출할 수 있는 getLoginToken 메소드
    public JwtTokenDto getLoginToken(LoginDto loginDto){
        //입력한 유저네임으로 해당 유저 엔티티 찾기
        String username = loginDto.getUsername();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        //입력한 비밀번호 비교하기
        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);


        //토큰 생성하기
        String accessToken = jwtTokenUtils.generateTokenByUsername(username);
        String refreshToken = jwtTokenUtils.generateRefreshTokenByUsername(username);

        Token token = new Token();
        token.setAccessToken(accessToken);
        token.setRefreshToken(refreshToken);
        token.setUsername(username);
        tokenRepository.save(token);

        //dto 리턴하기
        return JwtTokenDto.fromEntity(token);


    }

    //내 프로필 가져오기
    public UserProfileDto getMyProfile(){
        String myUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        return getProfileOfUser(myUsername);
    }

    //username에 해당하는 유저네임을 가진 유저의 프로필 가져오기
    public UserProfileDto getProfileOfUser(String username){
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        UserProfileDto dto = UserProfileDto.fromEntity(user);

        List<UserTechStack> techStackList = userTechStackRepository.findAllByUserId(user.getId());
        List<String> techStackStringList = new ArrayList<>();

        for (UserTechStack userTechStack : techStackList){
            String techStackName = techStackRepository.findById(userTechStack.getTechStackId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)).getName();
            techStackStringList.add(techStackName);
        }
        dto.setTechStackList(techStackStringList);

        return dto;
    }

    //비밀번호 변경
    public String changePassword(String currentPassword, String newPassword){
        //내 Username 가져오기
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        //나의 user Entity 가져오기
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        //passwordEncoder를 통해 사용자가 입력한 현재 password와 엔티티에 저장된 password가 일치하는지 비교.
        if (passwordEncoder.matches(currentPassword, user.getPassword())){
            //일치한다면 사용자가 바꾸고자하는 새로운 password로 변경
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        }
        else {
            //아니라면 Forbidden access 에러
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        return "done";
    }

    //프로필 업데이트하기
    @Transactional
    public String updateProfile(UserProfileDto dto, String techStackString){
        //내 Username 가져오기
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        //나의 user Entity 가져오기
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));



//        //이메일 또는 전화번호가 중복이라면 에러!
//        if (emailExists(dto.getEmail())) throw new ResponseStatusException(HttpStatus.CONFLICT);
//        if (phoneExists(dto.getPhone())) throw new ResponseStatusException(HttpStatus.CONFLICT);

        //user 업데이트
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setLocation(dto.getLocation());
        user.setIntroduction(dto.getIntroduction());
        user.setPrize(dto.getPrize());
        user.setPast(dto.getPast());
        user.setGithub(dto.getGithub());

        //테크 스택 업데이트
        userTechStackRepository.deleteAllByUserId(user.getId());
        String[] techList = techStackString.split("/");
        for (String techStackName : techList){
            TechStack techStack = techStackRepository.findByName(techStackName).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

            UserTechStack userTechStack = new UserTechStack();
            userTechStack.setTechStackId(techStack.getId());
            userTechStack.setUserId(user.getId());
            userTechStackRepository.save(userTechStack);
        }
        userRepository.save(user);

        return "done";
    }
    
    //유저네임으로 유저 엔티티 가져오기
    public User loadUserByUsername(String username){
        return userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }


}
