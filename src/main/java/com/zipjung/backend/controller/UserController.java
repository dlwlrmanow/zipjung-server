package com.zipjung.backend.controller;

import com.zipjung.backend.dto.RegisterDto;
import com.zipjung.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/sign-up")
    public ResponseEntity<Void> signUp(@RequestBody RegisterDto registerDto) {
        userService.SignUp(registerDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/find-username/{email}")
    public ResponseEntity<String> findUsername(@PathVariable String email) {
        userService.findUsernameByEmail(email);
        return new ResponseEntity<>(email, HttpStatus.OK);
    }
}
