package com.zipjung.backend.service;

import com.zipjung.backend.dto.NotificationResponse;
import com.zipjung.backend.dto.TodoRequest;
import com.zipjung.backend.entity.Notification;
import com.zipjung.backend.entity.NotificationType;
import com.zipjung.backend.entity.Post;
import com.zipjung.backend.entity.Todo;
import com.zipjung.backend.repository.NotificationRepository;
import com.zipjung.backend.repository.PostRepository;
import com.zipjung.backend.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TodoService {
    private final TodoRepository todoRepository;
    private final PostRepository postRepository;
    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;


    @Transactional
    public void saveTodos(TodoRequest todoRequest, Long memberId) {
        // 1. post 생성
        Post post = Post.builder()
                .title("Todo")
                .serviceId(2L)
                .isDeleted(false)
                .memberId(memberId)
                .build();
        postRepository.save(post);

        // 2. post_id 가져오기
        Long postId = post.getId();

        // 3. todos 저장
        Todo todos = Todo.builder()
                .task(todoRequest.getTask())
                .postId(postId)
                .isDone(false)
                .build();
        todoRepository.save(todos);

        // notification에 저장
        Notification todoNotification = Notification.builder()
                .notificationType(NotificationType.NEW_TODO)
                .title("Todo Saved!")
                .message("새로운 Todo " + post.getTitle() + "가 추가되었어요.")
                .fromId(memberId)
                .toId(memberId)
                .isRead(false)
                .build();
        notificationRepository.save(todoNotification);

        // 저장 성공 여부를 SSE 전송 + notification 테이블에 알림 저장
//        notificationService.sendEvent(memberId, todoNotification);
    }

    @Transactional
    public void initReminderCount(Long memberId, SseEmitter emitter) {
        // 1. 남은 할 일 갯수 count
        // 최근 일주일 동안의 하지 않은 할 일 count
        int todoCount = (int) todoRepository.countByNotDone(memberId);

        // 2. notificaton 객체 생성
        Notification reminderNotification = Notification.builder()
                .notificationType(NotificationType.REMINDER)
                .title("Reminder")
                .message("최근 완료되지 않은 할 일이 " + todoCount + "개 있어요!")
                .fromId(memberId)
                .toId(memberId)
                .isRead(false)
                .build();

        // 3. DB 저장
        notificationRepository.save(reminderNotification);

        // 4. SSE 알림 보내기
        notificationService.sendEvent(memberId, reminderNotification);

    }
}
