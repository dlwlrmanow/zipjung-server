package com.zipjung.backend.service;

import com.zipjung.backend.dto.NotificationDto;
import com.zipjung.backend.exception.SseEventException;
import com.zipjung.backend.repository.EmitterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@RequiredArgsConstructor
public class NotificationService {
    // 타임아웃
    private static final Long DEFAULT_TIMEOUT = 60L * 1000L * 60L;
    // 생성자 주입
    private final EmitterRepository notificationRepository;

    public SseEmitter createEmitter(Long id) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        // 생성된 emitter를 저장소에 저장
        notificationRepository.save(id, emitter);

        // emitter 완료 -> emitter 삭제
        emitter.onCompletion(() -> notificationRepository.deleteById(id));
        // 이벤트가 전송되지 않았더라도 타임아웃된 경우 삭제
        emitter.onTimeout(() -> notificationRepository.deleteById(id));

        return emitter;
    }

    public void sendEvent(Long sendId, String eventTitle, Object data) {
        SseEmitter emitter = notificationRepository.getById(sendId);

        if(emitter != null) {
            try {
                emitter.send(SseEmitter.event().id(String.valueOf(sendId)).name(eventTitle).data(data));
            } catch (IOException e) {
                // 전송 중 오류 발생시 emitter 삭제
                notificationRepository.deleteById(sendId);
                emitter.completeWithError(e);
                throw new SseEventException("SSE sendEvent 중 오류 발생");
            }
        }
        throw new SseEventException("emitter가 존재하지 않음");
    }

    // DB에 저장
    public void saveEvent(NotificationDto notificationDto, Long fromId) {

    }
}
