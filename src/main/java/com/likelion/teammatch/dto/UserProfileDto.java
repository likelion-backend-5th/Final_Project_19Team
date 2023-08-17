package com.likelion.teammatch.dto;

import com.likelion.teammatch.entity.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class UserProfileDto {
    private String username;
    private String email;
    private String phone;
    private String introduction;
    private String location;
    private String prize;
    private String past;
    private String github;
    private Long id;

    public static UserProfileDto fromEntity(User user){
        UserProfileDto dto = new UserProfileDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setIntroduction(user.getIntroduction());
        dto.setLocation(user.getLocation());
        dto.setPrize(user.getPrize());
        dto.setPast(user.getPast());
        dto.setGithub(user.getGithub());

        return dto;
    }
}
