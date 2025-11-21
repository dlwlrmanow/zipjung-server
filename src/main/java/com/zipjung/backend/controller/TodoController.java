package com.zipjung.backend.controller;

import com.zipjung.backend.dto.Result;
import com.zipjung.backend.dto.TodoRequestDto;
import com.zipjung.backend.dto.TodoResponseDto;
import com.zipjung.backend.exception.SseEventException;
import com.zipjung.backend.exception.TodoDBException;
import com.zipjung.backend.security.CustomUserDetails;
import com.zipjung.backend.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/todo")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @PostMapping("/save")
    public ResponseEntity<?> saveTodos(@AuthenticationPrincipal CustomUserDetails user, @RequestBody TodoRequestDto todoRequestDto) {
        Long memberId = user.getMemberId();

        try {
            Long todoId = todoService.saveTodos(todoRequestDto, memberId);

            // 성공적으로 저장된 경우 todo_id 반환
            return new ResponseEntity<>(Map.of("id", todoId), HttpStatus.OK);
        } catch (SseEventException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/fetch/list")
    public ResponseEntity<Result<List<TodoResponseDto>>> getTodosAndCount(@AuthenticationPrincipal CustomUserDetails user) {
        Long memberId = user.getMemberId();

        try {
            Result<List<TodoResponseDto>> todosResult = todoService.getTodosAndCount(memberId);
            return new ResponseEntity<>(todosResult, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteTodo(@AuthenticationPrincipal CustomUserDetails user, @PathVariable(value = "id") Long id) {
        System.out.println("[/delete/{id}] start]");
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
}
