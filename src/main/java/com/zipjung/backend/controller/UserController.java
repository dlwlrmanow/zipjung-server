package com.zipjung.backend.controller;

import com.zipjung.backend.dto.RegisterDto;
import com.zipjung.backend.dto.UserDto;
import com.zipjung.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/sign-up")
    public ResponseEntity<Void> signUp(@RequestBody RegisterDto registerDto) {
        userService.SignUp(registerDto);
        // TODO:
        return ResponseEntity.ok().build(); // TODO: 에러 메세지 body에 담아서 프론트가 처리할 수 이ㅆ도록
    }
}
