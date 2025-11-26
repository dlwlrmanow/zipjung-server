package com.zipjung.backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FocusedTodayTotalResponse {
    private Long todayFocusTime;

    // formatted 데이터
    private String focusedTimeStr;

    // 파라미터 하나만 받아서 여기서 싹 처리
    public FocusedTodayTotalResponse(Long todayFocusTime) {
        this.todayFocusTime = todayFocusTime;

        // formatted
        // totalSeconde가 null이면 0초로 처리
        long totalSeconds = (todayFocusTime != null) ? todayFocusTime : 0;

        long hour = totalSeconds / 3600;
        long minute = (totalSeconds % 3600) / 60;
        long second = totalSeconds % 60;

        this.focusedTimeStr = String.format("%d:%02d:%02d", hour, minute, second);
    }
}
