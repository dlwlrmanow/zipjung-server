package com.zipjung.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.Date;

@Builder
@AllArgsConstructor
public class RefreshTokenResponseDto {
    private String refreshToken;
    private String refreshTokenExpire;
}
