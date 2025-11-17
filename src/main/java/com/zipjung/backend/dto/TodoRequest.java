package com.zipjung.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TodoRequest {
//    private Long postId;
    private String task;
    private final boolean isDone = false;
}
