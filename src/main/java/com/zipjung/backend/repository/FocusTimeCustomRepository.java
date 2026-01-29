package com.zipjung.backend.repository;

import com.zipjung.backend.dto.FocusTimeWithLocationDto;

import java.time.LocalDateTime;
import java.util.List;

public interface FocusTimeCustomRepository {
    boolean isLocationExist(Long focusTimeId);

    List<FocusTimeWithLocationDto> getFocusTimeWithLocationDtoList(LocalDateTime startOfDay, LocalDateTime endOfDay, Long memberId);

    // 누적 총 오늘의 집중시간 마지막 하나만 가져오기
    Long getLastTotalFocusedTimeToday(Long memberId);
}
