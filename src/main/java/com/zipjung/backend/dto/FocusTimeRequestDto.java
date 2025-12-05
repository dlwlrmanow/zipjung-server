package com.zipjung.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FocusTimeRequestDto {
    private Long focusedTime;
    private String startFocusTime;
}
