package com.zipjung.backend.config;

import com.zipjung.backend.dto.JwtToken;
import com.zipjung.backend.repository.RedisDao;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Duration;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {
    private final Key key;
    private final UserDetailsService userDetailsService;
    private final RedisDao redisDao; // RefreshToken 담기 위해

    private static final String GRANT_TYPE = "Bearer";

    @Value("${jwt.access-token.expire-time}") // 1000 * 60 * 60 * 24 = 1일
    private long ACCESS_TOKEN_EXPIRE_TIME;

    @Value("${jwt.refresh-token.expire-time}") // 1000 * 60 * 60 * 24 * 3 = 3일
    private long REFRESH_TOKEN_EXPIRE_TIME;

    public JwtTokenProvider(@Value("${JASYPT_ENCRYPT_JWT}") String secretKey,
                            UserDetailsService userDetailsService,
                            RedisDao redisDao) {
        byte[] keyBytes = Base64.getEncoder().encode(secretKey.getBytes());
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.userDetailsService = userDetailsService;
        this.redisDao = redisDao;
    }

    public JwtToken GenerateToken(Authentication authentication) {
        // payload (=claims)의 권한을 저장하는 부분
        String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
        long now = (new Date()).getTime();
        String username = authentication.getName();

        // AccessToken
        Date accessTokenExpires = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        String accessToken = generateAccessToken(username, authorities, accessTokenExpires);

        // RefreshToken
        Date refreshTokenExpire = new Date(now + REFRESH_TOKEN_EXPIRE_TIME);
        String refreshToken = generateRefreshToken(username, refreshTokenExpire);

        // 위에서 생성한 refresh token을 redis에 담기
        redisDao.setValues(username, refreshToken, Duration.ofMillis(REFRESH_TOKEN_EXPIRE_TIME));

        return JwtToken.builder()
                .grantType(GRANT_TYPE)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

    }
    // accessToken의 payload + signature
    private String generateAccessToken(String username, String authorities, Date expireTime) {
        return Jwts.builder()
                .setSubject(username) // uasername
                .claim("authorities", authorities) // 권한 정보
                .setExpiration(expireTime) // 만료시간
                .signWith(SignatureAlgorithm.HS512, key) // 키와 알고리즘 서명
                .compact();
    }

    // refreshToken의 payload + signature
    private String generateRefreshToken(String username, Date expireTime) {
        return Jwts.builder()
                .setSubject(username)
                .setExpiration(expireTime)
                .signWith(SignatureAlgorithm.HS512, key)
                .compact();
    }

    // JWT -> spring security가 이해할 수 있는 Authentication객체로 변환
    public Authentication getAuthentication(String accessToken) {
        // claims: JWT token payload -> 자바 객체로 표현한 것
        // 기본적으로 라이브러리 사용시 claims는 Map<String, Object> 형태
        Claims claims = parseClaims(accessToken); // access token에서 사용자 데이터 추출

        if (claims.get("authorities") == null) throw new RuntimeException("권한 정보가 없는 토큰입니다."); // claims에 담긴 정보와 권한이 일치하는 지 확인

        // 권한 문자열 "ROLE_USER" -> [SimpleGrantedAuthority("ROLE_USER")]: GrantedAutority로 변환
        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get("authorities").toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .toList();
        // spring security가 사용하는 유저 객체로 변환해주고
        UserDetails userDetails = new User(claims.getSubject(), "", authorities);
        // 결과적으로 Authentication을 리턴 => security filter chian에서 인증도니 사용자로 처리할 수 있게끔 해줌
        return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty", e);
        }
        return false;
    }


    // refresh Token 검증
    public boolean validateRefreshToken(String token) {
        if (!validateToken(token)) return false;

        try {
            String username = getUserNameFromToken(token);
            String redisToken = (String) redisDao.getValues(username);
            return token.equals(redisToken);
        } catch (Exception e) {
            log.info("RefreshToken Validation Failed", e);
            return false;
        }
    }

    public String getUserNameFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // username
            return claims.getSubject();
        } catch (ExpiredJwtException e) {
            // 토큰이 만료되어도 클레임 내용을 가져올 수 있음
            return e.getClaims().getSubject();
        }
    }

    // 사용자가 로그아웃하는 경우 refresh token 삭제
    public void deleteRefreshToken(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }

        redisDao.deleteValues(username);
    }
}
