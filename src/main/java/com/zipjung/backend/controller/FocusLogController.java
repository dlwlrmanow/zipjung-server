package com.zipjung.backend.controller;

import com.zipjung.backend.dto.FocusLogDto;
import com.zipjung.backend.dto.FocusLogForListDto;
import com.zipjung.backend.dto.Result;
import com.zipjung.backend.service.FocusLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/focus-log")
@RequiredArgsConstructor
public class FocusLogController {
    final private FocusLogService focusLogService;

    @PostMapping("/save")
    public ResponseEntity<Void> saveFocusLog(@RequestBody FocusLogDto focusLogDto) {
        focusLogService.saveFocusLog(focusLogDto);
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
}
