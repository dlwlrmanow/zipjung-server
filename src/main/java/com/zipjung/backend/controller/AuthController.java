package com.zipjung.backend.controller;

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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword()));
            JwtToken token = jwtTokenProvider.generateToken(authentication);
            return ResponseEntity.ok(token);
        } catch (BadCredentialsException e) { // 비밀번호 틀림
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED); // 401
        } catch (UsernameNotFoundException e) { // 없는 사용자
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND); // 404
        } catch (Exception e) { // 서버 오류
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR); // 500
        }
    }
}
