package com.likelion.teammatch.dto;

import com.likelion.teammatch.entity.Token;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtTokenDto {
    private String accessToken;
    private String refreshToken;

    public static JwtTokenDto fromEntity(Token token){
        JwtTokenDto dto = new JwtTokenDto();
        dto.setAccessToken(token.getAccessToken());
        dto.setRefreshToken(token.getRefreshToken());
        return dto;
    }
}
