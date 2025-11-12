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
    public ResponseEntity<?> SaveTodoList(@AuthenticationPrincipal CustomUserDetails user, @RequestBody TodoRequest todos) {
        Long memberId = user.getMemberId();

        todoService.saveTodos(todos, memberId);
        return ResponseEntity.ok().build();
    }

    // TODO: delete 추가

}
