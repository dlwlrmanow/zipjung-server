package com.zipjung.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocationRequest {
    // 인기 검색어
    private String spot;

    private Long focusTimeId;
    // 위도
    private Double latitude;
    // 경도
    private Double longitude;
}
