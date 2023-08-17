package com.likelion.teammatch.service;

import com.likelion.teammatch.dto.RegisterDto;
import com.likelion.teammatch.dto.UserProfileDto;
import com.likelion.teammatch.entity.User;
import com.likelion.teammatch.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    //회원가입
    public void createUser(RegisterDto dto){
        if (userRegisterConflicts(dto)) throw new ResponseStatusException(HttpStatus.CONFLICT);//Username Conflict!

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());

        userRepository.save(user);
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

    //내 프로필 가져오기
    public UserProfileDto getMyProfile(){
        String myUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        return getProfileOfUser(myUsername);
    }

    //username에 해당하는 유저네임을 가진 유저의 프로필 가져오기
    public UserProfileDto getProfileOfUser(String username){
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return UserProfileDto.fromEntity(user);
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
    public String updateProfile(UserProfileDto dto){
        //내 Username 가져오기
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        //나의 user Entity 가져오기
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));


        //유저네임은 변경 불가! DTO의 username 필드는 비어있어야 한다.
        if (dto.getUsername() != null) throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        //이메일 또는 전화번호가 중복이라면 에러!
        if (emailExists(dto.getEmail())) throw new ResponseStatusException(HttpStatus.CONFLICT);
        if (phoneExists(dto.getPhone())) throw new ResponseStatusException(HttpStatus.CONFLICT);

        //user 업데이트
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setLocation(dto.getLocation());
        user.setIntroduction(dto.getIntroduction());
        user.setPrize(dto.getPrize());
        user.setPast(dto.getPast());
        user.setGithub(dto.getGithub());
        userRepository.save(user);

        return "done";
    }
    
    //유저네임으로 유저 엔티티 가져오기
    public User loadUserByUsername(String username){
        return userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }


}
