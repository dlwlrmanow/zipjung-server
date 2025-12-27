package com.zipjung.backend.controller;

import com.zipjung.backend.dto.FocusLogDto;
import com.zipjung.backend.dto.FocusLogForListDto;
import com.zipjung.backend.dto.LocationRequest;
import com.zipjung.backend.dto.Result;
import com.zipjung.backend.security.CustomUserDetails;
import com.zipjung.backend.service.CustomUserDetailsService;
import com.zipjung.backend.service.FocusLogService;
import com.zipjung.backend.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/focus-log")
@RequiredArgsConstructor
public class FocusLogController {
    private final FocusLogService focusLogService;
    private final PostService postService;

    @PostMapping("/save")
    public ResponseEntity<Integer> saveFocusLog(@RequestBody FocusLogDto focusLogDto, @AuthenticationPrincipal CustomUserDetails user) {
        Long memberId = user.getMemberId();

        // focusLog 저장 실패
        if (!focusLogService.saveFocusLog(focusLogDto, memberId)){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/add/location")
    public ResponseEntity<?> addLocationFocusLog(@RequestBody LocationRequest locationRequest, @AuthenticationPrincipal CustomUserDetails user) {
        System.out.println("/add/location///////////////////////");

        Long memberId = user.getMemberId();

        try {
            focusLogService.addLocation(memberId, locationRequest);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            log.warn(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/list/fetch")
    public ResponseEntity<Result<List<FocusLogForListDto>>> fetchFocusLogs(@AuthenticationPrincipal CustomUserDetails user) {
        Long memberId = user.getMemberId();

        System.out.println("[/focus-log/fetch] memberId: " + memberId);
        List<FocusLogForListDto> lists = focusLogService.getFocusLogList(memberId);
        if (lists.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204
        }
        int successCount = lists.size();
        return ResponseEntity.ok().body(new Result<>(lists, successCount));
    }

    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<Void> deletePost(@AuthenticationPrincipal CustomUserDetails user, @PathVariable Long postId) {
        Long memberId = user.getMemberId();
        postService.deletePost(memberId, postId);
        return ResponseEntity.noContent().build(); // 성공시 204
    }
}
