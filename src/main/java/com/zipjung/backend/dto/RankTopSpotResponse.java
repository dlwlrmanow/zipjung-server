package com.zipjung.backend.dto;

import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RankTopSpotResponse {
    private List<RankTopSpotDto> ranks;
}
