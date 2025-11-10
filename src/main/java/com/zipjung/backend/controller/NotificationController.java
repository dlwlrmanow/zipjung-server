package com.zipjung.backend.controller;

import com.zipjung.backend.dto.JwtToken;
import com.zipjung.backend.dto.NotificationResponse;
import com.zipjung.backend.exception.InvaildTokenException;
import com.zipjung.backend.security.CustomUserDetails;
import com.zipjung.backend.security.JwtTokenProvider;
import com.zipjung.backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subscribe(@CookieValue("refreshToken") String refreshToken) {
        try {
            SseEmitter emitter = notificationService.subscribe(refreshToken);
            return new ResponseEntity<>(emitter, HttpStatus.OK);
        } catch (InvaildTokenException e) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
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
