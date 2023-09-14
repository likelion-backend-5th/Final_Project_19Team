package com.likelion.teammatch.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterDto {
    private String username;
    private String password;

    private String email;
    private String phone;
    private String techStackList;
}
