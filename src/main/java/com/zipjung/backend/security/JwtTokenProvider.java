package com.zipjung.backend.security;

import com.zipjung.backend.dto.JwtToken;
import com.zipjung.backend.dto.RefreshTokenDto;
import com.zipjung.backend.repository.RedisDao;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
    private final RedisDao redisDao;

    private static final String GRANT_TYPE = "Bearer";

    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 5; // 30분 -> 5분으로 변경
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 3; // 3일

    public JwtTokenProvider(@Value("${JASYPT_ENCRYPTOR_PASSWORD}") String key,
                            UserDetailsService userDetailsService,
                            RedisDao redisDao) {
        byte[] keyBytes = Base64.getEncoder().encode(key.getBytes());
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.userDetailsService = userDetailsService;
        this.redisDao = redisDao;
    }

    public JwtToken generateToken(Authentication authentication) {
        // payload (=claims)의 권한을 저장하는 부분
        String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
        long now = (new Date()).getTime();
        String username = authentication.getName();

        // memberId 추가
        CustomUserDetails userDetails = (CustomUserDetails)authentication.getPrincipal();
        Long memberId = userDetails.getMemberId();


        // AccessToken
        Date accessTokenExpires = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        String accessToken = generateAccessToken(memberId, username, authorities, accessTokenExpires);

        // RefreshToken
        Date refreshTokenExpire = new Date(now + REFRESH_TOKEN_EXPIRE_TIME);
        String refreshToken = generateRefreshToken(username, refreshTokenExpire);

        // access token을 생성하면서
        // 위에서 생성한 refresh token을 redis에 담기
        redisDao.setValues(username, refreshToken, Duration.ofMillis(REFRESH_TOKEN_EXPIRE_TIME));


        // 클라이언트가 받는 부분
        return JwtToken.builder()
                .grantType(GRANT_TYPE)
                .memberId(memberId)
                .username(username)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

    }
    // accessToken의 payload + signature
    private String generateAccessToken(Long memberId, String username, String authorities, Date expireTime) {
        return Jwts.builder()
                .setSubject(username) // DONE: 토큰 발급시 username이 아니라 member_id 발급하도록 변경
                .claim("memberId", memberId) // authentication에서 사용
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
        Claims claims = parseClaims(accessToken); // access token에서 사용자 데이터 추출, 서명/만료시간도 검증

        if (claims.get("authorities") == null) throw new BadCredentialsException("권한 정보가 없는 토큰입니다."); // claims에 담긴 정보와 권한이 일치하는 지 확인

        // 권한 문자열 "ROLE_USER" -> [SimpleGrantedAuthority("ROLE_USER")]: GrantedAutority로 변환
        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get("authorities").toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .toList();

        Long memberId = claims.get("memberId", Long.class);
        if (memberId == null) throw new InsufficientAuthenticationException("token에 memberId가 존재하지 않습니다");

        // spring security가 사용하는 유저 객체로 변환
        // 결과적으로 Authentication을 리턴 => security filter chian에서 인증도니 사용자로 처리할 수 있게끔 해줌
        CustomUserDetails userDetails = new CustomUserDetails(memberId, claims.getSubject(), "", authorities);
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

    public RefreshTokenDto reissueRefreshToken(String username) {
        long now = (new Date()).getTime();

        Date refreshTokenExpire = new Date(now + REFRESH_TOKEN_EXPIRE_TIME);
        String refreshToken = generateRefreshToken(username, refreshTokenExpire);

        // 기존에 redis에 있던 refresh token 삭제하고 새로운 refresh token 담기
        // TODO: try-catch로 예외던지기
        redisDao.deleteValues(username);
        redisDao.setValues(username, refreshToken, Duration.ofMillis(REFRESH_TOKEN_EXPIRE_TIME));

        // DONE: jwtToken 기존 DTO 사용하기 보다는 새로운 refresh token renew용 DTO 추가하기
        return RefreshTokenDto.builder()
                .username(username)
                .refreshToken(refreshToken)
                .build();
    }


    // refresh Token 검증
    public boolean validateRefreshToken(String token) {
        if (!validateToken(token)) return false; // 유효기간 확인

        try { // redis에 있는 refresh token과 일치한지 확인
            String username = getUserNameFromToken(token);
            String redisToken = (String) redisDao.getValues(username);
            return token.equals(redisToken);
        } catch (Exception e) {
            log.info("RefreshToken Validation Failed", e);
            return false;
        }
    }

    // 사용자가 로그아웃하는 경우 refresh token 삭제
    public void deleteRefreshToken(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }

        redisDao.deleteValues(username);
    }

    public String getUserNameFromToken(String token) {
        try{
            Claims claims = Jwts.parserBuilder() // refresh token을 발급할 때 username 함께 담아줬음
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject(); // = username
        } catch (ExpiredJwtException e) {
            return e.getClaims().getSubject(); // 만료된 토큰 username 반환
        }
    }

    public JwtToken reissueToken(String refreshToken) {
        // UserDeatils로 Authentication 객체 생성
        String username = getUserNameFromToken(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        // 새로 생상하면서 기존 redis에 저장한 refresh token 삭제
        if(redisDao.existsKey(username)) {
            redisDao.deleteValues(username);
        }

        JwtToken jwtToken = generateToken(authentication);
        return jwtToken;
    }

    public Long getRefreshTokenExpireTime() {
        return REFRESH_TOKEN_EXPIRE_TIME;
    }
}
