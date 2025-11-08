package com.zipjung.backend.controller;

import com.zipjung.backend.dto.JwtToken;
import com.zipjung.backend.dto.NotificationResponse;
import com.zipjung.backend.security.CustomUserDetails;
import com.zipjung.backend.security.JwtTokenProvider;
import com.zipjung.backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@CookieValue("refreshToken") String refreshToken) {
        // cookie에 담긴 refresh token으로 인증
        boolean validated = jwtTokenProvider.validateRefreshToken(refreshToken);
        // refresh token이 유효한 경우
        if (!validated) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh Token이 유효하지 않습니다. 재로그인하십시오."); // 클라이언트에서 처리
        }

        // access token 재발급
        JwtToken token = jwtTokenProvider.reissueToken(refreshToken);
        // 새로운 access token으로 인증 인가
        Authentication auth = jwtTokenProvider.getAuthentication(token.getAccessToken());


        Long memberId = (Long) auth.getPrincipal();
        System.out.println("[NotificationController] memberId: " + memberId);

        // 연결 성공시 클라이언트에 emitter를 반환
        return notificationService.subscribe(memberId);
    }

    @PostMapping("/send")
    public void sendNotification(@AuthenticationPrincipal CustomUserDetails user, NotificationResponse notificationResponse) {
        Long mememberId = user.getMemberId();

        // notification DB에 저장

    }


}
