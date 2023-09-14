package com.likelion.teammatch.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class UserTechStack {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.PACKAGE)
    private Long id;

    private Long userId;
    private Long techStackId;

    private String skilled;

    public static UserTechStack getInstance(Long userId, Long techStackId, String skilled){
        UserTechStack userTechStack = new UserTechStack();
        userTechStack.setUserId(userId);
        userTechStack.setTechStackId(techStackId);
        userTechStack.setSkilled(skilled);

        return userTechStack;
    }
}
