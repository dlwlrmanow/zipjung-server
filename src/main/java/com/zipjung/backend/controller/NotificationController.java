package com.zipjung.backend.controller;

import com.zipjung.backend.security.CustomUserDetails;
import com.zipjung.backend.service.NotificationService;
import com.zipjung.backend.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;
    private final TodoService todoService;

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subscribe(@AuthenticationPrincipal CustomUserDetails user) {
        // filter chain에서 AT 검증하도록 함
        Long memberId = user.getMemberId();

        if(memberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            SseEmitter emitter = notificationService.subscribe(memberId);
            System.out.println("[SSE SUBSCRIBED] success!!]:" + emitter);

            // TODO: 구독이 성공적으로 연결되면, 바로 initReminder send
            todoService.initReminderCount(memberId, emitter);// 얘는 알아서 보내기에 호출만!!

            return new ResponseEntity<>(emitter, HttpStatus.OK); // 연결이 됐다는 건 클라이언트에 보내기
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
