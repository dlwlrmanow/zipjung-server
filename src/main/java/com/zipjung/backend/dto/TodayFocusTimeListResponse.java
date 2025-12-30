package com.zipjung.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodayFocusTimeListResponse {
    List<FocusTimeWithLocationDto> focusTimeWithLocationDtoList;
}
