package com.zipjung.backend.controller;

import com.zipjung.backend.dto.Result;
import com.zipjung.backend.entity.FocusTime;
import com.zipjung.backend.service.FocusTimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/focus-time")
@RequiredArgsConstructor
public class FocusTimeController {
    final private FocusTimeService focusTimeService;

    @PostMapping("/save")
    public ResponseEntity<Long> saveFocusTime(@RequestBody FocusTime focusTime) {
        Long savedId = focusTimeService.saveFocusTime(focusTime);
        // TODO: 생성된 focusTime id 클라이언트에 던져줘야함!
        return new ResponseEntity<>(savedId, HttpStatus.CREATED);
    }

    @GetMapping("/list/fetch")
    public ResponseEntity<Result<List<FocusTime>>> fetchFocusTimes() {
        List<FocusTime> focusTimeList = focusTimeService.fetchRecentFocusTime();
        if(focusTimeList == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204
        }
        return ResponseEntity.ok().body(new Result<>(focusTimeList, focusTimeList.size()));
    }

    @GetMapping("/today/fetch")
    public ResponseEntity<Long> fetchTodayFocusTimes() {
        Long todayFocusTime = focusTimeService.fetchTodayFocusTime();
        return ResponseEntity.ok(todayFocusTime);
    }

    @GetMapping("/fetch/{id}")
    public ResponseEntity<FocusTime> fetchFocusTime(@PathVariable Long id) {
        FocusTime savedFocusTime = focusTimeService.fetchFocusTimeById(id);

        if(savedFocusTime == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return ResponseEntity.ok(savedFocusTime);
    }
}
