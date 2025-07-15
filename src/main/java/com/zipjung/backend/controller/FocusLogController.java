package com.zipjung.backend.controller;

import com.zipjung.backend.dto.FocusLogDto;
import com.zipjung.backend.service.FocusLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/save")
@RequiredArgsConstructor
public class FocusLogController {
    final private FocusLogService focusLogService;

    @PostMapping("/focus-log")
    public ResponseEntity<Void> save(@RequestBody FocusLogDto focusLogDto) {
        focusLogService.saveFocusLog(focusLogDto);
        return new ResponseEntity<>(HttpStatus.CREATED); // post요청은 이렇게 보내야함
    }
}
