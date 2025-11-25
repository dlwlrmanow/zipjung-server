package com.zipjung.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
public class FocusTimeWithEndTimeResponse {
    private Long focusedTime;

    // 원본 데이터 ignore
    @JsonIgnore
    private String startFocusTime;
    @JsonIgnore
    private String endFocusTime;

    // parsed 데이터
    private String startTime;
    private String endTime;

    public FocusTimeWithEndTimeResponse(String startFocusTime, String endFocusTime) {
        this.startFocusTime = startFocusTime;
        this.endFocusTime = endFocusTime;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
        LocalDateTime startTimeFormat = LocalDateTime.parse(startFocusTime, formatter);
        LocalDateTime endTimeFormat = LocalDateTime.parse(endFocusTime, formatter);

        DateTimeFormatter formatterKor = DateTimeFormatter.ofPattern("HH시 mm분 ss초");
        this.startTime = startTimeFormat.format(formatterKor);
        this.endTime = endTimeFormat.format(formatterKor);
    }
}
