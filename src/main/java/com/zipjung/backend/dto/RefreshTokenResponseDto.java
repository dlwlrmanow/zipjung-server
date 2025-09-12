package com.zipjung.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
public class RefreshTokenResponseDto {
    private String refreshToken;
    private String refreshTokenExpire;
}
