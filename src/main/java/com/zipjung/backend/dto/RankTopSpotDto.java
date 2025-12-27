package com.zipjung.backend.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RankTopSpotDto {
    private int rank;
    private String spot;
    private String url;
}
