package com.zipjung.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor
public class FocusTimeWithLocationDto {
    private Long id;

    // 원본 데이터 ignore
    @JsonIgnore
    private Long focusedTime;

    @JsonIgnore
    private String startFocusTime;
    @JsonIgnore
    private String endFocusTime;

    // location 데이터가 존재하는 경우 클릭시 이동할 수 있도록 focusLogId 담기
    private Long focusLogId;

    // parsed 데이터
    private String startTime;
    private String endTime;
    private String focusedTimeStr;

    // 날짜 포맷된 형태로
    @QueryProjection
    public FocusTimeWithLocationDto(Long id, Long focusedTime, String startFocusTime, String endFocusTime, Long focusLogId) {
        this.id = id;
        this.focusedTime = focusedTime;
        this.startFocusTime = startFocusTime;
        this.endFocusTime = endFocusTime;
        this.focusLogId = focusLogId;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
        LocalDateTime startTimeFormat = LocalDateTime.parse(startFocusTime, formatter);
        LocalDateTime endTimeFormat = LocalDateTime.parse(endFocusTime, formatter);

        DateTimeFormatter formatterKor = DateTimeFormatter.ofPattern("HH시 mm분 ss초");
        this.startTime = startTimeFormat.format(formatterKor);
        this.endTime = endTimeFormat.format(formatterKor);

        // focused time format
        LocalDateTime focusedTimeFormat = Instant.ofEpochSecond(focusedTime)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        DateTimeFormatter fomatterTime = DateTimeFormatter.ofPattern("HH:mm:ss");
        String focusedTimeStr = fomatterTime.format(focusedTimeFormat);
        this.focusedTimeStr = focusedTimeStr;
    }
}
