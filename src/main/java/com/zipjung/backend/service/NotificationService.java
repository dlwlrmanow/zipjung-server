package com.zipjung.backend.service;

import com.zipjung.backend.dto.NotificationDto;
import com.zipjung.backend.entity.Notification;
import com.zipjung.backend.entity.Todo;
import com.zipjung.backend.exception.SseEventException;
import com.zipjung.backend.repository.EmitterRepository;
import com.zipjung.backend.repository.NotificationRepository;
import com.zipjung.backend.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class NotificationService {
    // 타임아웃
    private static final Long DEFAULT_TIMEOUT = 60L * 1000L * 60L;
    // 생성자 주입
    private final EmitterRepository emitterRepository;
    private final NotificationRepository notificationRepository;
    private final TodoRepository todoRepository;

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

    public void sendEvent(SseEmitter emitter, Long sendId, String eventTitle, Object data) {
        if(emitter != null) {
            try {
                emitter.send(SseEmitter.event().id(String.valueOf(sendId)).name(eventTitle).data(data));
            } catch (IOException e) {
                // 전송 중 오류 발생시 emitter 삭제
                emitterRepository.deleteById(sendId);
                // emitter 종료
                emitter.completeWithError(e);
                throw new SseEventException("SSE sendEvent 중 오류 발생");
            }
        }
        throw new SseEventException("emitter가 존재하지 않음");
    }

    public SseEmitter subscribe(Long memberId) {
        SseEmitter emitter = createEmitter(memberId);
        try {
            sendEvent(emitter, memberId,"subscribed", null);
        } catch (Exception e) {
            System.out.println("[NotificationService] subscribe: " + e.getMessage());
        }
        return emitter;
    }

    // DB에 저장
    @Transactional
    public void saveEvent(NotificationDto notificationDto, Long fromId) {
        // DB 저장
        Notification notification = Notification.builder()
                .title(notificationDto.getTitle())
                .message(notificationDto.getMessage())
                .fromId(fromId)
                .toId(notificationDto.getToId())
                .build();

        notificationRepository.save(notification);
    }

    // 로그인시 reminder를 클라이언트에 send
    public void sendTodos(Long memberId, SseEmitter emitter) {
        LocalDateTime oneWeek = LocalDateTime.now().minusDays(7);
        // repository로부터 데이터 가져오고
        List<Todo> todos = todoRepository.getRecentWeekTodo(oneWeek, memberId);
        // sendEvent로 todolist 보내기
        try {
            sendEvent(emitter, memberId, "reminder", todos);
        } catch (Exception e) {
            System.out.println("[[NotificationService] sendTodos: " + e.getMessage());
        }
        // TODO: saveEvent -> notification DB에 저장
    }
}
