package com.zipjung.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodayFocusTimeListResponse {
    List<FocusTimeWithLocationDto> focusTimeWithLocationDtoList;
    List<FocusTimeNoLocationDto> focusTimeNoLocationDtoList;
}
