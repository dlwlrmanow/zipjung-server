package com.zipjung.backend.controller;

import com.zipjung.backend.dto.RefreshTokenResponseDto;
import com.zipjung.backend.security.CustomUserDetails;
import com.zipjung.backend.security.JwtTokenProvider;
import com.zipjung.backend.dto.JwtToken;
import com.zipjung.backend.dto.LoginRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword()));
            JwtToken token = jwtTokenProvider.generateToken(authentication);
            return ResponseEntity.ok(token); // 클라이언트에 200
        } catch (BadCredentialsException e) { // 비밀번호 틀림
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED); // 401
        } catch (UsernameNotFoundException e) { // 없는 사용자
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND); // 404
        } catch (Exception e) { // 서버 오류
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR); // 500
        }
    }

    // access token은 유효, refresh token은 연장
    @PostMapping("/validate/refresh")
    public ResponseEntity<?> validateRefreshToken(@AuthenticationPrincipal CustomUserDetails user, @RequestHeader("Authorization") String refreshTokenHeader) {
        String refreshToken = refreshTokenHeader.replace("Bearer ", ""); // token만 파싱

        boolean valid = jwtTokenProvider.validateRefreshToken(refreshToken);

        if (valid) {
            // refresh token 검증 후 로그인 연장을 위한 refresh token 재발급
            RefreshTokenResponseDto refreshTokenDto = jwtTokenProvider.renewRefreshToken(user.getMemberId(), user.getUsername());
            return ResponseEntity.ok(refreshTokenDto);
        }

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
}
