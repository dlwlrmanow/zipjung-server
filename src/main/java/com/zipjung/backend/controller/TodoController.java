package com.zipjung.backend.controller;

import com.zipjung.backend.dto.TodoRequest;
import com.zipjung.backend.security.CustomUserDetails;
import com.zipjung.backend.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/todo")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @PostMapping("/save")
    public ResponseEntity<?> SaveTodoList(@AuthenticationPrincipal CustomUserDetails user, @RequestBody TodoRequest todoRequest) {
        System.out.println("[TodoController] start");
        Long memberId = user.getMemberId();

        todoService.saveTodos(todoRequest, memberId);
        return ResponseEntity.ok().build();
    }
}
