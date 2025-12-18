package com.zipjung.backend.repository;

import com.zipjung.backend.dto.TodoResponseDto;

import java.util.List;

public interface TodoCustomRepository {
    Long countByNotDone(Long memberId);

    List<TodoResponseDto> getTodos(Long memberId, Long lastTodoId);

    Long countListTodo(Long memberId);

    boolean deleteTodo(Long memberId, Long postId);
}
