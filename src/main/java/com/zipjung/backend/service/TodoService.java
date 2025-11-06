package com.zipjung.backend.service;

import com.zipjung.backend.dto.TodoRequest;
import com.zipjung.backend.entity.Post;
import com.zipjung.backend.entity.Todo;
import com.zipjung.backend.repository.PostRepository;
import com.zipjung.backend.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TodoService {
    private final TodoRepository todoRepository;
    private final PostRepository postRepository;
    private final NotificationService notificationService;

    @Transactional
    public void saveTodos(List<TodoRequest> todoRequests, Long memberId) {
        // 1. post 생성
        Post post = Post.builder()
                .title("Todo")
                .content(null)
                .serviceId(2L)
                .memberId(memberId)
                .build();
        postRepository.save(post);

        // 2. post_id 가져오기
        Long postId = post.getId();
        // 3. todos 저장
        List<Todo> todos = todoRequests.stream()
                .map(todoRequest -> {
                    Todo todo = new Todo();
                    todo.setPostId(postId);
                    todo.setTask(todoRequest.getTask());
                    todo.setDone(false);
                    return todo;
                })
                .toList();
        todoRepository.saveAll(todos);

        // 저장 성공 여부를 SSE 전송 + notification 테이블에 알림 저장
        notificationService.saveTodoNotification(memberId, todoRequests);
    }

    public int countNotDone(Long memberId) {
        int count = (int) todoRepository.countByNotDone(memberId);
        return count;
    }
}
