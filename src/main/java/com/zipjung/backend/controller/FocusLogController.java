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
    public ResponseEntity<Void> saveFocusLog(@RequestBody FocusLogDto focusLogDto, @AuthenticationPrincipal CustomUserDetails user) {
        // DOWE: memberId를 저장하도록 변경 -> @AuthenticationPrincipal CustomUserDetails user로 memberId 찾아오기
        Long memberId = user.getMemberId();
        focusLogService.saveFocusLog(focusLogDto, memberId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/fetch")
    public ResponseEntity<Result<List<FocusLogForListDto>>> fetchFocusLogs() {
        List<FocusLogForListDto> lists = focusLogService.getFocusLogs();
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
