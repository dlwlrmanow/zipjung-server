package com.zipjung.backend.repository;

import com.zipjung.backend.dto.TodoResponseDto;

import java.util.List;

public interface TodoCustomRepository {
    long countByNotDone(Long memberId);

    List<TodoResponseDto> getTodos(Long memberId);
}
