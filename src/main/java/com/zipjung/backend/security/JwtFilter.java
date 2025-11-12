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
        // preflight 요청시 거절당하는 거 방지
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        // 이미 발급된 토큰을 검증하는 filter
        String accessToken = resolveToken(request);

        // 존재는 하지만 유효하지 않은 토큰
        if(accessToken != null && !jwtTokenProvider.validateToken(accessToken)) {
            throw new BadCredentialsException("[JWT Filter] Invalid access token");
        }

        // 존재하고 유효한 토큰인 경우만
        if (accessToken != null && jwtTokenProvider.validateToken(accessToken)) {
            Authentication authentication = jwtTokenProvider.getAuthentication(accessToken); // 토큰에 있는 정보 꺼내기
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response); // 인증 정보를 가지고 다음 filter를 타야함
            System.out.println("[JWT Filter] Access token validated]");
            return;
        }
        filterChain.doFilter(request, response); // 인증이 없는 상태로 진행되다가 AuthenticationEntryPoint로 걸리게 된다.
    }
    public String resolveToken (HttpServletRequest request) {
        // 1. header에 담긴 token을 resolve
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7); // "Bearer "을 제외한 순수한 토큰 값만 리턴
        }

        // 2. 쿼리 파라미터로 담긴 token을 resolve
        String queryToken = request.getParameter("token");
        if(StringUtils.hasText(queryToken)) {
            return queryToken;
        }
        return null;
    }

}
