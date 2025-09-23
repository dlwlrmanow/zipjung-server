package com.zipjung.backend.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilterInternal (HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        // 로그인, 회원가입 같이 아직 토큰 발급되지 않아 검증 필요없는 경로는 제외시키기
        String path = request.getRequestURI();
        if(path.startsWith("/auth/login") || path.startsWith("/auth/join")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 이미 발급된 토큰을 검증하는 filter
        String accessToken = resolveToken(request);

        // 존재는 하지만 유효하지 않은 토큰
        if(accessToken != null && !jwtTokenProvider.validateToken(accessToken)) {
            throw new BadCredentialsException("Invalid access token");
        }

        // 존재하고 유효한 토큰인 경우만
        if (accessToken != null && jwtTokenProvider.validateToken(accessToken)) {
            Authentication authentication = jwtTokenProvider.getAuthentication(accessToken); // 토큰에 있는 정보 꺼내기
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response); // 인증 정보를 가지고 다음 filter를 타야함
            return;
        }

        filterChain.doFilter(request, response); // 인증이 없는 상태로 진행되다가 AuthenticationEntryPoint로 걸리게 된다.
    }
    public String resolveToken (HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7); // "Bearer "을 제외한 순수한 토큰 값만 리턴
        }

        return null;
    }

}
