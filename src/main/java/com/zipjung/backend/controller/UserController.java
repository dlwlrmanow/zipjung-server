package com.zipjung.backend.controller;

import com.zipjung.backend.dto.JoinRequestDto;
import com.zipjung.backend.exception.DuplicateEmailException;
import com.zipjung.backend.exception.DuplicateUsernameException;
import com.zipjung.backend.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final MemberService memberService;

    @PostMapping("/join")
    public ResponseEntity<String> join(@RequestBody JoinRequestDto joinRequestDto) {
        memberService.registerMember(joinRequestDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<String> handleDuplicateEmailException(DuplicateEmailException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT); // IM_USED는 사용할 수 없음 = 508 : 서버 웹 확장을 위한 코드
    }

    @ExceptionHandler(DuplicateUsernameException.class)
    public ResponseEntity<String> handleDuplicateUsernameException(DuplicateUsernameException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }

    @GetMapping("/find-username/{email}")
    public ResponseEntity<String> findUsername(@PathVariable String email) {
        memberService.findUsernameByEmail(email);
        return new ResponseEntity<>(email, HttpStatus.OK);
    }

}
