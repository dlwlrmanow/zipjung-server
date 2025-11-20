package com.zipjung.backend.controller;

import com.zipjung.backend.dto.Result;
import com.zipjung.backend.dto.TodoRequestDto;
import com.zipjung.backend.dto.TodoResponseDto;
import com.zipjung.backend.exception.SseEventException;
import com.zipjung.backend.security.CustomUserDetails;
import com.zipjung.backend.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<?> SaveTodoList(@AuthenticationPrincipal CustomUserDetails user, @RequestBody TodoRequestDto todoRequestDto) {
        System.out.println("[TodoController] start");
        Long memberId = user.getMemberId();

        try {
            todoService.saveTodos(todoRequestDto, memberId);
        } catch (SseEventException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/fetch/list")
    public ResponseEntity<Result<List<TodoResponseDto>>> GetTodos(@AuthenticationPrincipal CustomUserDetails user) {
        System.out.println("[/fetch/list] start");
        Long memnberId = user.getMemberId();

        Result<List<TodoResponseDto>> todosResult = todoService.getTodosAndCount(memnberId);
        return new ResponseEntity<>(todosResult, HttpStatus.OK);
    }
}
