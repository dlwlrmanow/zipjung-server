package com.zipjung.backend.service;

import com.zipjung.backend.dto.NotificationDto;
import com.zipjung.backend.dto.TodoRequest;
import com.zipjung.backend.entity.Notification;
import com.zipjung.backend.exception.SseEventException;
import com.zipjung.backend.repository.EmitterRepository;
import com.zipjung.backend.repository.NotificationRepository;
import com.zipjung.backend.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zipjung.backend.entity.QNotification.notification;

@Service
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

    public void sendEvent(SseEmitter emitter, Long sendId, Notification notification) {
        if(emitter != null) {
            try {
                String eventTitle = notification.getTitle();
                String message = notification.getMessage();

                emitter.send(SseEmitter.event().id(String.valueOf(sendId)).name(eventTitle).data(message));
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
            emitter.send("connected!"); // 503 막기 위해서 dummy 보내기
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

//    // 로그인시 reminder를 클라이언트에 send
//    public void sendTodos(Long memberId, SseEmitter emitter) {
//        // TODO: todosService에서 List가져오고 여기에서는 sse로 보내주는 걸로 변경
//        LocalDateTime oneWeek = LocalDateTime.now().minusDays(7);
//        // repository로부터 데이터 가져오고
//        List<Todo> todos = todoRepository.getRecentWeekTodo(oneWeek, memberId);
//        // sendEvent로 todolist 보내기
//        try {
//            sendEvent(emitter, memberId, "reminder", todos);
//        } catch (Exception e) {
//            System.out.println("[[NotificationService] sendTodos: " + e.getMessage());
//        }
//        // TODO: saveEvent -> notification DB에 저장
//    }

    public void saveTodoNotification(SseEmitter emitter, Long memberId, List<TodoRequest> todoRequests) {
        // 저장된 todos count notification에 저장
        Notification todoCountMsg = Notification.builder()
                .title("Todo Saved!")
                .message("새로운 Todo " + todoRequests.size() + "개 저장되었어요!")
                .fromId(memberId)
                .toId(memberId)
                .isRead(false)
                .build();

        notificationRepository.save(todoCountMsg);

        // 실시간 전송
        sendEvent(emitter, memberId, todoCountMsg);
    }

    // 로그인 된 경우 todos sse 알림
}
