package com.zipjung.backend.controller;

import com.zipjung.backend.security.CustomUserDetails;
import com.zipjung.backend.service.NotificationService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping(value = "/subscribe/{id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subscribe(@AuthenticationPrincipal CustomUserDetails user, HttpServletResponse response) {
        Long memberId = user.getMemberId();

        notificationService.createEmitter(memberId);
        // 응답 헤더 설정
        response.setContentType("text/event-stream");
        response.setCharacterEncoding("UTF-8");

        // dummy 보내기
        try {
            notificationService.sendEvent(memberId, "connected", null);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.out.println("[NotificationController]: " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

//    @PostMapping("/save")
//    public void sendNotification(@AuthenticationPrincipal CustomUserDetails user, ) {
//        Long mememberId = user.getMemberId();
//        // notification DB에 저장
//
//    }


}
