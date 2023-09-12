package com.likelion.teammatch.dto;

import com.likelion.teammatch.entity.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

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
    private List<String> techStackList;
    private Integer grade;
    private Integer tier;
    private Integer giveUpCount;

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

        Integer grade = user.getGrade();
        dto.setGrade(grade);
        if (grade <= 1000) dto.setTier(1);
        else if (grade <= 2000) dto.setTier(2);
        else if (grade <= 3000) dto.setTier(3);
        else if (grade <= 4000) dto.setTier(4);
        else if (grade <= 5000) dto.setTier(5);
        dto.setGiveUpCount(user.getGiveUpCount());
        //techStackList는 알아서 채워라.
        return dto;
    }
}
