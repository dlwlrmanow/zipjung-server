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
        try {
            memberService.registerMember(joinRequestDto);
        } catch (DuplicateEmailException e) {
            return new ResponseEntity<>("EXIST_USER", HttpStatus.BAD_REQUEST);
        } catch (DuplicateUsernameException e) {
            return new ResponseEntity<>("DUPLICATE_USERNAME", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/find-username/{email}")
    public ResponseEntity<String> findUsername(@PathVariable String email) {
        memberService.findUsernameByEmail(email);
        return new ResponseEntity<>(email, HttpStatus.OK);
    }

}
