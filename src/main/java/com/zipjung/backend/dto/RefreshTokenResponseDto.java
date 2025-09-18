package com.zipjung.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
public class RefreshTokenResponseDto {
    private String grantType; // 인증 방식
    private String username;
    private Long memberId;
    private String refreshToken;
}
