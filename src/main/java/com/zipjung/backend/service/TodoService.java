package com.zipjung.backend.service;

import com.zipjung.backend.dto.*;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TodoService {
    private final NotificationService notificationService;
    private final TodoRepository todoRepository;
    private final PostRepository postRepository;
    private final NotificationRepository notificationRepository;
    private final ApplicationEventPublisher eventPublisher;


    @Transactional
//    @CacheEvict(value = "getRecentTodoList", key = "#memberId", cacheManager = "ehcacheManager") // 새로운 데이터가 추가되면 기존 캐시 삭제(새로 가져오ㄷ로ㅗㄱ)
    public void saveTodos(TodoRequestDto todoRequestDto, Long memberId) { // todos_id를 return -> void로 수정
        Todo todos; // notification save 할 때 써야함!

        try {
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
            todos = Todo.builder()
                    .task(todoRequestDto.getTask())
                    .postId(postId)
                    .isDone(false)
                    .build();
            todoRepository.save(todos);

            log.info("save todo 방금 저장된 todo_id: {}", todos.getId());
        } catch (Exception e) {
            // @Transactional 덕분에 저장 실패시 완전히 roollback
            log.error("save todo fail: {}", e.getMessage());
            throw new RuntimeException(e); // RuntimeException으로 던져야 rollback이 가능(@Transactional의 기본값)
        }

        log.info("notification 알림 저장 로직 실행 시작");
        notificationService.saveNotification(NotificationType.NEW_TODO,
                "new Todo",
                "새로운 TODO [" + todos.getTask() + "]가 추가되었어요.",
                memberId,
                memberId
        );

        // Redis Pub
        notificationService.publishNotification(memberId,
                NotificationType.NEW_TODO,
                "new Todo",
                "새로운 TODO [" + todos.getTask() + "]가 추가되었어요."
        );
        log.info("todo service 끝");
    }

    // 로그인시에 바로 오늘 할 일 갯수 띄우기
    public void initReminderCount(Long memberId) {
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
        // TODO: reminder의 경우 id를 추가해서 다른 todo_id의 경우에는 클라이언트에서 flag 비교 후 다시 보여주도록
        // TODO: 위의 방식으로 수정
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
        eventPublisher.publishEvent(new NotificationDto(memberId, reminderNotification.getId()));
    }

    @Cacheable(value = "getRecentTodoList", key = "#memberId", cacheManager = "ehcacheManager", condition = "#lastTodoId == null")
    public Result<List<TodoResponseDto>> getTodosAndCount (Long memberId, Long lastTodoId) {
        List<TodoResponseDto> todos = todoRepository.getTodos(memberId, lastTodoId);
        int todosCount = todoRepository.countListTodo(memberId).intValue();

        return new Result<>(todos, todosCount); // 메서드가 뱉어내는 이게 캐싱되는것!! 완제품(더이상 가공이 필요없는 걸)을 캐싱하는 게 성능이 . 좋음
    }

    @Transactional
    @CacheEvict(value = "getRecentTodoList", key = "#memberId", cacheManager = "ehcacheManager") // 새로운 데이터가 추가되면 캐시 삭제
    public void deleteTodo (Long memberId, Long todoId) {
        boolean delete = todoRepository.deleteTodo(memberId, todoId);
        if(!delete) {
            throw new TodoDBException("[deleteTodo] 중 오류 발생");
        }
    }

    @Transactional
    public void updateIsDone(Long memberId, Long todoId) {
        // id로 is done 으로 update
        Todo todo = todoRepository.findById(todoId).orElseThrow(() -> new IllegalArgumentException("해당 id로 todo를 찾을 수 없습니다: " + todoId));
        todo.markAsDone();

        // notification 저장하기
        Notification doneNotification = Notification.builder()
                .notificationType(NotificationType.DONE_TODO)
                .title("완료한 TODO")
                .message("TODO: " + todo.getTask() + " 완료하였어요!")
                .fromId(memberId)
                .toId(memberId)
                .isRead(false)
                .build();
        notificationRepository.save(doneNotification);

        eventPublisher.publishEvent(new NotificationDto(memberId, doneNotification.getId()));
    }

    // TODO: 로그아웃할 때 오늘은 n개의 할일을 마무리 했어요! - sse todo_updatedAt으로
}
