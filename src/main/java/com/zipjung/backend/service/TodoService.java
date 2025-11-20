package com.zipjung.backend.service;

import com.zipjung.backend.dto.Result;
import com.zipjung.backend.dto.TodoRequestDto;
import com.zipjung.backend.dto.TodoResponseDto;
import com.zipjung.backend.entity.Notification;
import com.zipjung.backend.entity.NotificationType;
import com.zipjung.backend.entity.Post;
import com.zipjung.backend.entity.Todo;
import com.zipjung.backend.exception.SseEventException;
import com.zipjung.backend.exception.TodoDBException;
import com.zipjung.backend.repository.EmitterRepository;
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
    private final EmitterRepository emitterRepository;


    @Transactional
    public void saveTodos(TodoRequestDto todoRequestDto, Long memberId) {
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
                .task(todoRequestDto.getTask())
                .postId(postId)
                .isDone(false)
                .build();
        todoRepository.save(todos);

        // notification에 저장
        Notification todoNotification = Notification.builder()
                .notificationType(NotificationType.NEW_TODO)
                .title("new TODO ")
                .message("새로운 Todo [" + todos.getTask() + "] 추가되었어요.")
                .fromId(memberId)
                .toId(memberId)
                .isRead(false)
                .build();
        notificationRepository.save(todoNotification);

        // notification 저장 성공 후 바로 알림
        SseEmitter emitter = emitterRepository.getById(memberId);

        notificationService.sendEvent(memberId, todoNotification, emitter);
    }

    // 로그인시에 바로 오늘 할 일 갯수 띄우기
    public void initReminderCount(Long memberId, SseEmitter emitter) {
        if(emitter == null) {
            throw new SseEventException("emitter is null");
        }
        // 1. 남은 할 일 갯수 count
        // 최근 일주일 동안의 하지 않은 할 일 count
        // int로 변환 21억개 이상의 데이터면 데이터가 손실 될 수도
        int todoCount = todoRepository.countByNotDone(memberId).intValue();

        // todos 0인경우 알림 적재 X
        if(todoCount == 0L) {
            System.out.println("[TodoService initReminderCount] todos 0개");
            return;
        }

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
        notificationService.sendEvent(memberId, reminderNotification, emitter);
    }

    public Result<List<TodoResponseDto>> getTodosAndCount (Long memberId) {
        List<TodoResponseDto> todos = todoRepository.getTodos(memberId);
        int todosCount = todoRepository.countListTodo(memberId).intValue();

        return new Result<>(todos, todosCount);
    }

    public void deleteTodo (Long memberId, Long todoId) {
        if(!todoRepository.deleteTodo(memberId, todoId)) {
            throw new TodoDBException("[deleteTodo] 중 오류 발생");
        }
    }

    // TODO: 로그아웃할 때 오늘은 n개의 할일을 마무리 했어요! - sse
}
