package com.zipjung.backend.controller;

import com.zipjung.backend.dto.NotificationResponse;
import com.zipjung.backend.security.CustomUserDetails;
import com.zipjung.backend.service.NotificationService;
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

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subscribe(@RequestParam("token") String accessToken, @AuthenticationPrincipal CustomUserDetails user) {
        Long memberId = user.getMemberId();

        if(memberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            SseEmitter emitter = notificationService.subscribe(memberId);
            return new ResponseEntity<>(emitter, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/send")
    public void sendNotification(@AuthenticationPrincipal CustomUserDetails user, NotificationResponse notificationResponse) {
        Long mememberId = user.getMemberId();

        // notification DB에 저장

    }


}
