package com.zipjung.backend.controller;

import com.zipjung.backend.entity.FocusTime;
import com.zipjung.backend.service.FocusTimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/save")
@RequiredArgsConstructor
public class FocusTimeController {
    final private FocusTimeService focusTimeService;

    @PostMapping("/focus-time")
    public ResponseEntity<Void> saveFocusTime(@RequestBody FocusTime focusTime) {
        focusTimeService.saveFocusTime(focusTime);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}
