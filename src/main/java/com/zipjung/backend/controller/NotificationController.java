package com.zipjung.backend.controller;

import com.zipjung.backend.exception.SseEventException;
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

@RestController // 얘 때문에 filter에서 controller로 안넘어감!!
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;
    private final TodoService todoService;

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subscribe(@AuthenticationPrincipal CustomUserDetails user) {
        System.out.println("[NotificationController] subscribe");

        // JwtFilter에서 검증 후 넘어와서 AT 추가로 검증할 필요 없음
        Long memberId = user.getMemberId();

        if(memberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            SseEmitter emitter = notificationService.subscribe(memberId);

            if(emitter == null) {
                System.out.println("[SSE SUBSCRIBED] emitter is null");
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }

            System.out.println("[SSE SUBSCRIBED] success!!]:" + emitter);

            // 구독 성공시 바로 보내기 TODO: 여기서 연결하지 않고 클라이언트가 연결하도록 수정
//            todoService.initReminderCount(memberId, emitter);

            return new ResponseEntity<>(emitter, HttpStatus.OK); // 연결이 됐다는 건 클라이언트에 보내기
        } catch (SseEventException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
