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
    public ResponseEntity<Void> saveFocusTime(@RequestBody FocusTime focusTime) {
        System.out.println(focusTime.getStartFocusTime());
        System.out.println(focusTime.getFocusedTime());
        focusTimeService.saveFocusTime(focusTime);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/fetch")
    public ResponseEntity<Result<List<FocusTime>>> fetchFocusTimes() {
        List<FocusTime> focusTimeList = focusTimeService.fetchRecentFocusTime();
        if(focusTimeList == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204
        }
        return ResponseEntity.ok().body(new Result<>(focusTimeList, focusTimeList.size()));
    }

    @GetMapping("/today/fetch")
    public ResponseEntity<Result<List<FocusTime>>> fetchTodayFocusTimes() {
        List<FocusTime> focusTimeTodayList = focusTimeService.fetchTodayFocusTime();
        if(focusTimeTodayList == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok().body(new Result<>(focusTimeTodayList, focusTimeTodayList.size()));
    }

}
