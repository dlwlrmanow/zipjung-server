package com.zipjung.backend.controller;

import com.zipjung.backend.dto.NotificationResponse;
import com.zipjung.backend.security.CustomUserDetails;
import com.zipjung.backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@AuthenticationPrincipal CustomUserDetails user) {
        Long memberId = user.getMemberId();

        // 연결 성공시 클라이언트에 emitter를 반환
        return notificationService.subscribe(memberId);
    }

    @PostMapping("/send")
    public void sendNotification(@AuthenticationPrincipal CustomUserDetails user, NotificationResponse notificationResponse) {
        Long mememberId = user.getMemberId();

        // notification DB에 저장

    }


}
