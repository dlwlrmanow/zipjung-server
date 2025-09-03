package com.zipjung.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class JwtToken { // 클라이언트와 주고받기 때문에 DTO
    private String grantType; // 인증 방식
    private String username;
    private Long memberId;
    private String accessToken;
    private String refreshToken;
}
