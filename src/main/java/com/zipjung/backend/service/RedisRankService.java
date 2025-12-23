package com.zipjung.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisRankService {
    private final StringRedisTemplate redisTemplate;
    private static final String POPULAR_SPOT_KEY = "POPULAR_SPOT_";

    public void increasePopularSpotRank(String keyword) {
        String today = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String key = POPULAR_SPOT_KEY + today;
        log.info("key: {}", key);

        Double rankingScore = redisTemplate.opsForZSet().incrementScore(key, keyword, 1);
        log.info("{} Ranking score is {}", keyword, rankingScore);

        // 지금 환경설정 시간대가 -Duser.timezone=UTC로 되어있어서
        // 한국시간 아침 9시에 초기화
        redisTemplate.expire(key, 3, TimeUnit.DAYS);
    }
}
