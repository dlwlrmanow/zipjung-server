package com.zipjung.backend.controller;

import com.zipjung.backend.dto.RankTop5SpotDto;
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
    public ResponseEntity<RankTop5SpotDto> getRankTop5Spot() {
        RankTop5SpotDto rankTop5SpotDto = redisRankService.getRankTop5Spot();
        log.info("[/rank/spot] rankTop5SpotDto: {}", rankTop5SpotDto);

        return ResponseEntity.ok(rankTop5SpotDto);
    }
}
