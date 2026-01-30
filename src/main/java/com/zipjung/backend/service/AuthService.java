package com.zipjung.backend.service;

import com.zipjung.backend.dto.JwtToken;
import com.zipjung.backend.exception.InvaildTokenException;
import com.zipjung.backend.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtTokenProvider jwtTokenProvider;

    public String parsingRefreshToken(String refreshTokenHeader) {
        if (refreshTokenHeader != null && refreshTokenHeader.startsWith("Bearer ")) {
            return refreshTokenHeader.substring(7);
        }

        return null;
    }

}
