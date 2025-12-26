package com.zipjung.backend.controller;

import com.zipjung.backend.dto.RankTopSpotResponse;
import com.zipjung.backend.service.RedisRankService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/rank")
@RequiredArgsConstructor
public class RankingController {
    private final RedisRankService redisRankService;

    @GetMapping("/spot")
    public ResponseEntity<RankTopSpotResponse> getRankTop5Spot() {
        RankTopSpotResponse rankTopSpotResponse = redisRankService.getRankTop5Spot();
        log.info("[/rank/spot] rankTop5SpotDto: {}", rankTopSpotResponse);

        return ResponseEntity.ok(rankTopSpotResponse);
    }
}
