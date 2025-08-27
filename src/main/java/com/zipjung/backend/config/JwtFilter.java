package com.zipjung.backend.config;

import com.zipjung.backend.exception.InvaildTokenException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilterInternal (HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 이미 발금된 토큰을 검증하는 filter
        String accessToken = resolveToken(request);
//        String accessToken = resolveToken((HttpServletRequest) request);

        if (accessToken != null) {
            if (!jwtTokenProvider.validateToken(accessToken)) {
                // 유효하지 않은 토큰
                throw new InvaildTokenException("유효하지 않은 accessToken");
                // DONE: 유효하지 않은 토큰에 대한 handler
            }
            Authentication authentication = jwtTokenProvider.getAuthentication(accessToken); // 토큰에 있는 정보 꺼내기
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        chain.doFilter(request, response);
    }

    // request header에서 token 가져오기
    public String resolveToken (HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7); // "Bearer "을 제외한 순수한 토큰 값만 리턴
        }

        return null;
    }
}
