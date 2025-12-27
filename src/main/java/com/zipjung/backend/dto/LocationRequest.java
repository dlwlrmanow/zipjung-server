package com.zipjung.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
public class LocationRequest {
    // 인기 장소 검색어
    private String spotName;

    private Long focusTimeId;
    // 위도
    private Double latitude;
    // 경도
    private Double longitude;
    // 장소ID
    private Long placeId;
    // url
    private String placeUrl;
}
