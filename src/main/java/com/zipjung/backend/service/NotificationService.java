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
    // 생성자 주입
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

        String notificationType = type.toString();
        log.info("notification type : {}", notificationType);

        String senderMemberId = memberId.toString();
        log.info("sender member id : {}", senderMemberId);

        notification.put("memberId", senderMemberId);
        notification.put("type", notificationType);
        notification.put("title", title);
        notification.put("message", message);
        notification.put("createdAt", LocalDateTime.now().toString());

        // notifications 채널에 메세지 publish
        redisTemplate.convertAndSend("notification", notification);
        log.info("Redis Pub: memberId {}", memberId);
    }
}
