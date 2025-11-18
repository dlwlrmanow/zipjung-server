package com.zipjung.backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TodoRequest {
    private String task;
    private final boolean isDone = false;
}
