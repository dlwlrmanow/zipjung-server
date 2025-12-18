package com.zipjung.backend.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
public class TodoResponseDto implements Serializable {
    private Long id;
    private String task;
//    private boolean isDone;
    private LocalDateTime createdAt;

    @QueryProjection
    public TodoResponseDto(Long id, String task, LocalDateTime createdAt) {
        this.id = id;
        this.task = task;
        this.createdAt = createdAt;
    }
}
