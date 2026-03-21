package com.zipjung.backend.controller;

import com.zipjung.backend.dto.Result;
import com.zipjung.backend.dto.TodoRequestDto;
import com.zipjung.backend.dto.TodoResponseDto;
import com.zipjung.backend.exception.SseEventException;
import com.zipjung.backend.exception.TodoDBException;
import com.zipjung.backend.security.CustomUserDetails;
import com.zipjung.backend.service.TodoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/todo")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @PostMapping("/save")
    public ResponseEntity<?> saveTodos(@AuthenticationPrincipal CustomUserDetails user, @RequestBody TodoRequestDto todoRequestDto) {
        Long memberId = user.getMemberId();

        try {
            todoService.saveTodos(todoRequestDto, memberId);

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            // DB 쓰기 작업 실패
            log.error("/save에서 DB쓰기 작업 실패: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/fetch/list")
    public ResponseEntity<Result<List<TodoResponseDto>>> getTodosAndCount(@AuthenticationPrincipal CustomUserDetails user, @RequestParam(required = false) Long lastTodoId) {
        Long memberId = user.getMemberId();

        try {
            Result<List<TodoResponseDto>> todosResult = todoService.getTodosAndCount(memberId, lastTodoId);
            return new ResponseEntity<>(todosResult, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/fetch/list/test")
    public ResponseEntity<Result<List<TodoResponseDto>>> getTodosAndCount() {
        Long memberId = 999L;
        Long lastTodoId = null;

        try {
            Result<List<TodoResponseDto>> todosResult = todoService.getTodosAndCount(memberId, lastTodoId);
            return new ResponseEntity<>(todosResult, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteTodo(@AuthenticationPrincipal CustomUserDetails user, @PathVariable(value = "id") Long id) {
        Long memberId = user.getMemberId();

        try {
            todoService.deleteTodo(memberId, id);
        } catch (TodoDBException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        return ResponseEntity.noContent().build(); // 에러에 안잡히면
    }

    @PatchMapping("/update/isdone/{todoId}")
    public ResponseEntity<?> updateIsDone(@AuthenticationPrincipal CustomUserDetails user, @PathVariable(value = "todoId") Long todoId) {
        Long memberId = user.getMemberId();

        try {
            todoService.updateIsDone(memberId, todoId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/reminder")
    public ResponseEntity<?> reminder(@AuthenticationPrincipal CustomUserDetails user) {
        Long memberId = user.getMemberId();

        todoService.initReminderCount(memberId);
        return ResponseEntity.noContent().build();
    }
}
