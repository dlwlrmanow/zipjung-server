package com.zipjung.backend.service;

import com.zipjung.backend.dto.NotificationResponse;
import com.zipjung.backend.entity.Notification;
import com.zipjung.backend.exception.SseEventException;
import com.zipjung.backend.repository.EmitterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class NotificationService {
    // 타임아웃
    private static final Long DEFAULT_TIMEOUT = 60L * 1000L * 60L;
    // 생성자 주입
    private final EmitterRepository emitterRepository;

    public SseEmitter createEmitter(Long memberId) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        // 생성된 emitter를 저장소에 저장
        emitterRepository.save(memberId, emitter);

        // emitter 완료 -> emitter 삭제
        emitter.onCompletion(() -> emitterRepository.deleteById(memberId));
        // 이벤트가 전송되지 않았더라도 타임아웃된 경우 삭제
        emitter.onTimeout(() -> emitterRepository.deleteById(memberId));

        return emitter;
    }

    @Transactional
    public void sendEvent(Long memberId, Notification notification, SseEmitter emitter) {
        if(emitter != null) {
            try {
                // notificationDto
                NotificationResponse notificationResponse = NotificationResponse.builder()
                        .notificationType(notification.getNotificationType()) // 그냥 enum으로 내려줘서 클라이언트에서 상수로 처리
                        .title(notification.getTitle())
                        .message(notification.getMessage())
                        .toId(notification.getToId())
                        .memberId(notification.getFromId())
                        .build();

                emitter.send(SseEmitter.event()
                        .name("notification")
                        .id(String.valueOf(memberId))
                        .data(notificationResponse)
                );

                // 전송 성공 시 isRead = true 변경
                notification.markAsRead();
            } catch (IOException e) {
                // 전송 중 오류 발생시 emitter 삭제
                emitterRepository.deleteById(memberId);
                // emitter 종료
                emitter.completeWithError(e);
                throw new SseEventException("SSE sendEvent 중 오류 발생");
            }
        }
    }

    public SseEmitter subscribe(Long memberId) {
        // 이전에 만든 emitter 존재하는 지 확인
        SseEmitter existEmitter = emitterRepository.getById(memberId);

        if(existEmitter != null) {
            System.out.println("[NotificationService] subscribe: emitter exist");
            return existEmitter;
        }

        // 이제 emitter 생성 및 구독
        SseEmitter emitter = createEmitter(memberId);
        try {
            emitter.send(SseEmitter.event()
                    .name("dummy")
                    .data("connected!")
                    .id("")
            ); // 503 막기 위해서 dummy 보내기
        } catch (Exception e) {
            System.out.println("[NotificationService] subscribe: " + e.getMessage());
        }

        System.out.println("[NotificationService] subscribe: " + memberId);
        return emitter;
    }
}
