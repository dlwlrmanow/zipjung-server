package com.zipjung.backend.controller;

import com.zipjung.backend.dto.FocusLogDto;
import com.zipjung.backend.entity.FocusLog;
import com.zipjung.backend.repository.FocusLogRepository;
import com.zipjung.backend.service.FocusLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/send/focus-log")
@RequiredArgsConstructor
public class FocusLogController {
    final private FocusLogService focusLogService;

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody FocusLogDto focusLogDto) {
        focusLogService.saveFocusLog(focusLogDto);
        return ResponseEntity.ok().build();
    }
}
