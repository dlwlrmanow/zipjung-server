package com.zipjung.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class RefreshTokenDto {
//    private String grantType; // 인증 방식
    private String username;
//    private Long memberId;
    private String refreshToken;
}
