//package com.zipjung.backend.dto;
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import com.querydsl.core.annotations.QueryProjection;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//import java.time.Instant;
//import java.time.LocalDateTime;
//import java.time.ZoneId;
//import java.time.format.DateTimeFormatter;
//
//@Getter
//@NoArgsConstructor
//@AllArgsConstructor
//public class FocusTimeNoLocationDto {
//    private Long id;
//
//    // 원본 데이터 ignore
//    @JsonIgnore
//    private Long focusedTime;
//
//    @JsonIgnore
//    private String startFocusTime;
//    @JsonIgnore
//    private String endFocusTime;
//
//    // parsed 데이터
//    private String startTime;
//    private String endTime;
//    private String focusedTimeStr;
//
//    // 날짜 포맷된 형태로
//    @QueryProjection
//    public FocusTimeNoLocationDto(Long id, Long focusedTime, String startFocusTime, String endFocusTime) {
//        this.id = id;
//        this.focusedTime = focusedTime;
//        this.startFocusTime = startFocusTime;
//        this.endFocusTime = endFocusTime;
//
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
//        LocalDateTime startTimeFormat = LocalDateTime.parse(startFocusTime, formatter);
//        LocalDateTime endTimeFormat = LocalDateTime.parse(endFocusTime, formatter);
//
//        DateTimeFormatter formatterKor = DateTimeFormatter.ofPattern("HH시 mm분 ss초");
//        this.startTime = startTimeFormat.format(formatterKor);
//        this.endTime = endTimeFormat.format(formatterKor);
//
//        // focused time format
//        LocalDateTime focusedTimeFormat = Instant.ofEpochSecond(focusedTime)
//                .atZone(ZoneId.systemDefault())
//                .toLocalDateTime();
//        DateTimeFormatter fomatterTime = DateTimeFormatter.ofPattern("HH:mm:ss");
//        String focusedTimeStr = fomatterTime.format(focusedTimeFormat);
//        this.focusedTimeStr = focusedTimeStr;
//    }
//}
