package com.zipjung.backend.controller;

import com.zipjung.backend.dto.FocusLogDto;
import com.zipjung.backend.dto.FocusLogForListDto;
import com.zipjung.backend.dto.Result;
import com.zipjung.backend.security.CustomUserDetails;
import com.zipjung.backend.service.CustomUserDetailsService;
import com.zipjung.backend.service.FocusLogService;
import com.zipjung.backend.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/focus-log")
@RequiredArgsConstructor
public class FocusLogController {
    private final FocusLogService focusLogService;
    private final PostService postService;

    @PostMapping("/save")
    public ResponseEntity<Integer> saveFocusLog(@RequestBody FocusLogDto focusLogDto, @AuthenticationPrincipal CustomUserDetails user) {
        Long memberId = user.getMemberId();
        System.out.println("[/focus-log/save] memberId: " + memberId);
        int successCount = focusLogService.saveFocusLog(focusLogDto, memberId);
        return new ResponseEntity<>(successCount, HttpStatus.CREATED);
    }

    @GetMapping("/fetc/list")
    // TODO: JUnit으로 test
    public ResponseEntity<Result<List<FocusLogForListDto>>> fetchFocusLogs(@AuthenticationPrincipal CustomUserDetails user) {
        // TODO: authentication 추가
        Long memberId = user.getMemberId();
        List<FocusLogForListDto> lists = focusLogService.getFocusLogList(memberId);
        if (lists.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204
        }
        return ResponseEntity.ok().body(new Result<>(lists, lists.size()));
    }

    @PatchMapping("/delete/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.noContent().build(); // 성공시 204
    }
}
