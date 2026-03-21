package com.zipjung.backend.service;

import com.zipjung.backend.entity.Notification;
import com.zipjung.backend.entity.NotificationType;
import com.zipjung.backend.exception.SseEventException;
import com.zipjung.backend.repository.EmitterRepository;
import com.zipjung.backend.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    // 타임아웃
    private static final Long DEFAULT_TIMEOUT = 60L * 1000L * 60L;
    // 생성자 주입
//    private final EmitterRepository emitterRepository;
    private final NotificationRepository notificationRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Transactional // 실패시 롤백을 위해
    public void saveNotification(NotificationType notificationType,
                                 String title, String message,
                                 Long sender, Long receiver) {
        Notification notification;
        try {
            notification = Notification.builder()
                    .notificationType(notificationType)
                    .title(title)
                    .message(message)
                    .fromId(sender)
                    .toId(receiver)
                    .isRead(false) // 아직 기능 구현 X
                    .build();

            notificationRepository.save(notification);
        } catch (Exception e) {
            log.error("notification save 중 문제 발생: {}", e.getMessage());
            throw new RuntimeException(e);
        }

        log.info("save notification success: from {} ~ to {} / notificationId: {}", sender, receiver, notification.getId());
    }

    public void publishNotification(Long memberId, NotificationType type, String title, String message) {
        Map<String, Object> notification = new HashMap<>();

        notification.put("memberId", memberId);
        notification.put("type", type);
        notification.put("title", title);
        notification.put("message", message);
        notification.put("createdAt", LocalDateTime.now().toString());

        // notifications 채널에 메세지 publish
        redisTemplate.convertAndSend("notification", notification);
        log.info("Redis Pub: memberId {}", memberId);
    }

//    public SseEmitter createEmitter(Long memberId) {
//        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
//        // 생성된 emitter를 저장소에 저장
//        emitterRepository.save(memberId, emitter);
//
//        // emitter 완료 -> emitter 삭제
//        emitter.onCompletion(() -> emitterRepository.deleteById(memberId));
//        // 이벤트가 전송되지 않았더라도 타임아웃된 경우 삭제
//        emitter.onTimeout(() -> emitterRepository.deleteById(memberId));
//
//        return emitter;
//    }
//
//    @Transactional (propagation = Propagation.REQUIRES_NEW) // 읽음 처리를 위해서 DB connection 열어야
//    public void sendEvent(Long memberId, Long notificationId){
//        // notificationDto
//        Notification notificationData = notificationRepository.findById(notificationId)
//                .orElseThrow(() -> new SseEventException("Notification with id " + notificationId + " not found"));
//
//        SseEmitter emitter = emitterRepository.getById(memberId); // 구독이 완료되었으면 이미 emitter가 이미 존재
//
//        if(emitter != null) {
//            try {
//                emitter.send(SseEmitter.event()
//                        .name("notification")
//                        .id(String.valueOf(memberId))
//                        .data(notificationData)
//                );
//
//                // sse 알림 성공시 is_read = true
//                notificationData.markAsRead();
//            } catch (IOException e) {
//                // 실패시 삭제
//                emitterRepository.deleteById(memberId);
//                emitter.completeWithError(e);
//
//                log.error("SSE 알림 전송 실패 : memberId={}, error={}, notificationId={}"
//                        , memberId, e.getMessage(), notificationId);
//            }
//        }
//    }
//
//    public SseEmitter subscribe(Long memberId) {
//        // 이전에 만든 emitter 존재하는 지 확인
//        SseEmitter existEmitter = emitterRepository.getById(memberId);
//
//        if(existEmitter != null) {
//            log.info("[NotificationService] subscribe: emitter exist");
//            return existEmitter;
//        }
//
//        // 이제 emitter 생성 및 구독
//        SseEmitter emitter = createEmitter(memberId);
//        try {
//            emitter.send(SseEmitter.event()
//                    .name("dummy")
//                    .data("connected!")
//                    .id("")
//            ); // 503 막기 위해서 dummy 보내기
//        } catch (Exception e) {
//            log.error(e.getMessage());
//        }
//        log.info("[NotificationService] subscribe: {}", memberId);
//        return emitter;
//    }
}
