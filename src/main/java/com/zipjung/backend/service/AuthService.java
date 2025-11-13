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

    public JwtToken getNewAccessToken(String refreshToken) {
        if(jwtTokenProvider.validateToken(refreshToken)) {
            throw new InvaildTokenException("[AuthService] 유효하지 않은 Token");
        }

        // access token만 재발급
        JwtToken token = jwtTokenProvider.reissueAccessToken(refreshToken);
        return token;
    }

}
