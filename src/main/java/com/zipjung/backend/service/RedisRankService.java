package com.zipjung.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisRankService {
    private final StringRedisTemplate redisTemplate;
    private static final String POPULAR_SPOT_KEY = "POPULAR_SPOT_KEYWORD";

    public void increasePopularSpotRank(String keyword) {
        Double rankingScore = redisTemplate.opsForZSet().incrementScore(POPULAR_SPOT_KEY, keyword, 1);
        log.info("{} Ranking score is {}", keyword, rankingScore);
    }
}
