package com.zipjung.backend.repository;

import com.zipjung.backend.dto.FocusTimeWithLocationDto;

import java.time.LocalDateTime;
import java.util.List;

public interface FocusTimeCustomRepository {
    boolean isLocationExist(Long focusTimeId);

    List<FocusTimeWithLocationDto> getFocusTimeWithLocationDtoList(LocalDateTime startOfDay, LocalDateTime endOfDay, Long memberId);
}
