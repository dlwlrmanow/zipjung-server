package com.zipjung.backend.controller;

import com.fasterxml.jackson.core.TreeCodec;
import com.zipjung.backend.dto.RefreshTokenDto;
import com.zipjung.backend.security.JwtTokenProvider;
import com.zipjung.backend.dto.JwtToken;
import com.zipjung.backend.dto.LoginRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
//@CrossOrigin(origins = "http://localhost:63342")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final TreeCodec treeCodec;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDto)  {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword()));
            JwtToken token = jwtTokenProvider.generateToken(authentication);
            return ResponseEntity.ok(token); // 클라이언트에 200
        } catch (BadCredentialsException e) { // 비밀번호 오류 혹은 권한 오류
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED); // 401
        } catch (UsernameNotFoundException e) { // 없는 사용자
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND); // 404
        } catch (Exception e) { // 서버 오류
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR); // 500
        }
    }

    @PostMapping("/login/web")
    public ResponseEntity<?> loginForWeb(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response)  {
        // authentication 인증
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword()));
        // jwt 토큰 생성
        JwtToken token = jwtTokenProvider.generateToken(authentication);

        // refresh toekn은 httpOnly 설정
        String refreshToken = token.getRefreshToken();

        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false) // TODO: 배포시에는 true로 변경!! https관련
                .path("/") //
                .maxAge(jwtTokenProvider.getRefreshTokenExpireTime() / 1000)
                .sameSite("Lax") // CSRF 방어
                .build();

        response.setHeader("Set-Cookie", cookie.toString()); // 응답 헤더에 쿠키 추가

        token.setRefreshToken(null); // 클라이언트에 다시 응답할 때는 refresh token을 제외
        return ResponseEntity.ok(token);
    }

    // access token은 유효, refresh token은 연장
    @PostMapping("/validate/refresh")
    public ResponseEntity<?> validateRefreshToken(@RequestBody RefreshTokenDto refreshTokenDto) {
        String refreshToken = refreshTokenDto.getRefreshToken();

        boolean isValid = jwtTokenProvider.validateRefreshToken(refreshToken);

        if (isValid) {
            // refresh token 재발급
            RefreshTokenDto newRefreshToken = jwtTokenProvider.reissueRefreshToken(refreshTokenDto.getUsername());
            return ResponseEntity.ok(newRefreshToken);
        }
        // 검증 실패시
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("/validate/token/web")
    public ResponseEntity<?> validateAccessTokenForWeb(@CookieValue("refreshToken") String refreshTokenValue, HttpServletResponse response) {
        boolean isValid = jwtTokenProvider.validateRefreshToken(refreshTokenValue);

        if (isValid) {
            System.out.println("[/validate/token/web] valid refreshToken");

            // 전체 Token reissue
            JwtToken newJwtToken = jwtTokenProvider.reissueToken(refreshTokenValue);
            // TODO: 메서드 분리하기
            ResponseCookie cookie = ResponseCookie.from("refreshToken", newJwtToken.getRefreshToken())
                    .httpOnly(true)
                    .secure(false) // TODO: 배포시에는 true로 변경!! https관련
                    .path("/") //
                    .maxAge(jwtTokenProvider.getRefreshTokenExpireTime() / 1000)
                    .sameSite("Lax") // CSRF 방어
                    .build();

            response.setHeader("Set-Cookie", cookie.toString()); // 응답 헤더에 쿠키 추가

            newJwtToken.setRefreshToken(null);
            return ResponseEntity.ok(newJwtToken);
        }
        // refresh token 만료
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("/reissue/access")
    public ResponseEntity<?> reissueAccess(@RequestBody RefreshTokenDto refreshTokenDto) {
        // refresh token 검증
        String refreshToken = refreshTokenDto.getRefreshToken();

        boolean isValid = jwtTokenProvider.validateRefreshToken(refreshToken);

        if (isValid) {
            // refresh token이 유효하다면
            // JWT token 전체 재발급
            System.out.println("isValid refreshToken");
            JwtToken newJwtToken = jwtTokenProvider.reissueToken(refreshToken);
            return ResponseEntity.ok(newJwtToken);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody RefreshTokenDto refreshTokenDto) {
        // redis에서 삭제하기 위해서 검증 먼저
        String refreshToken = refreshTokenDto.getRefreshToken();
        boolean isValid = jwtTokenProvider.validateRefreshToken(refreshToken);

        if(!isValid) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        String username = jwtTokenProvider.getUserNameFromToken(refreshToken);
        jwtTokenProvider.deleteRefreshToken(username);

        return ResponseEntity.ok().build();
    }
}
