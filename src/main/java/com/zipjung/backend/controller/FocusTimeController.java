package com.zipjung.backend.controller;

import com.zipjung.backend.dto.FocusTimeRequestDto;
import com.zipjung.backend.dto.FocusTimeWithEndTimeResponse;
import com.zipjung.backend.dto.FocusedTodayTotalResponse;
import com.zipjung.backend.dto.Result;
import com.zipjung.backend.entity.FocusTime;
import com.zipjung.backend.exception.FocusTimeException;
import com.zipjung.backend.security.CustomUserDetails;
import com.zipjung.backend.service.FocusTimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/focus-time")
@RequiredArgsConstructor
public class FocusTimeController {

    private final FocusTimeService focusTimeService;

    @PostMapping("/save")
    public ResponseEntity<?> saveFocusTime(@RequestBody FocusTimeRequestDto focusTimeRequestDto, @AuthenticationPrincipal CustomUserDetails user) {
        System.out.println("[saveFocusTime] start");
        Long memberId = user.getMemberId();

        try {
            Long savedId = focusTimeService.saveFocusTime(focusTimeRequestDto, memberId);
            return new ResponseEntity<>(savedId, HttpStatus.CREATED);
        } catch (FocusTimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) { // 분류되지 않은 알 수 없는 오류
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 최근 일주일의 집중 시간 리스트 뽑기
    // 사용자 기록 삭제시 해당 집중 시간도 뜨지 않는 문제 해결
    // focus_log_id가 한번 할당되어서 뜨지 않았음
    @GetMapping("/list/fetch")
    public ResponseEntity<Result<List<FocusTime>>> fetchFocusTimes(@AuthenticationPrincipal CustomUserDetails user) {
        System.out.println("[/focus-time/list/fetch] controller는 탐");
        Long memberId = user.getMemberId();

        List<FocusTime> focusTimeList = focusTimeService.fetchRecentFocusTime(memberId);
        if(focusTimeList == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204
        }
        return ResponseEntity.ok().body(new Result<>(focusTimeList, focusTimeList.size()));
    }

    // TODO: /today/fetch -> /today/total 로 수정 flutter에서 매핑 수정해야함!!
    @GetMapping("/today/total")
    public ResponseEntity<FocusedTodayTotalResponse> fetchTodayFocusTimes() {
        FocusedTodayTotalResponse todayFocusTime = focusTimeService.fetchTodayFocusTime();
        return ResponseEntity.ok(todayFocusTime);
    }

    // 00:00 ~ 00:30 (총 30분)
    @GetMapping("/today/list/fetch")
    public ResponseEntity<Result<List<FocusTimeWithEndTimeResponse>>> fetchTodayFocusTimesWithEndTime(@AuthenticationPrincipal CustomUserDetails user) {
        Long memberId = user.getMemberId();

        Result<List<FocusTimeWithEndTimeResponse>> result = focusTimeService.fetchTodayFocusTimesWithEndTime(memberId);
        return ResponseEntity.ok(result);
    }


    @GetMapping("/fetch/{id}")
    public ResponseEntity<FocusTime> fetchFocusTime(@PathVariable Long id) {
        FocusTime savedFocusTime = focusTimeService.fetchFocusTimeById(id);

        if(savedFocusTime == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return ResponseEntity.ok(savedFocusTime);
    }

    @DeleteMapping("/delete/all")
    public ResponseEntity<?> deleteFocusedItemAllByDate(@AuthenticationPrincipal CustomUserDetails user) {
        Long memberId = user.getMemberId();

        try {
            focusTimeService.deleteFocusedItemAll(memberId);

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteFocusItemOneById(@AuthenticationPrincipal CustomUserDetails user, @PathVariable Long id) {
        Long memberId = user.getMemberId();

        try {
            focusTimeService.deleteFocusTimeById(memberId, id);

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
