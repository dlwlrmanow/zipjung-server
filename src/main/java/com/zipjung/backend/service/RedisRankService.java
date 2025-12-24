package com.zipjung.backend.service;

import com.zipjung.backend.dto.RankTop5SpotDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisRankService {
    private final StringRedisTemplate redisTemplate;
    private static final String POPULAR_SPOT_KEY = "POPULAR_SPOT_KEYWORD";

    public void increasePopularSpotScore(String keyword) {
        // TODO: 위치 정보 가져와서 함께 담기(사용자가 클릭시에 장소 상세 페이지로 이동할 수 있도록)
        String today = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String key = POPULAR_SPOT_KEY + today;

        Double rankingScore = redisTemplate.opsForZSet().incrementScore(key, keyword, 1);
        log.info("{} Ranking score is {}", keyword, rankingScore);

        // TTL 한국 시간 아침 9시 초기화
        redisTemplate.expire(key, 3, TimeUnit.DAYS);
    }

    // DTO 반환하기
    public RankTop5SpotDto getRankTop5Spot() {
        String today = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String key = POPULAR_SPOT_KEY + today;

        Set<String> ranking = redisTemplate.opsForZSet().reverseRange(key, 0, 4); // 상위 1 ~ 5위까지

        List<String> rankingList = new ArrayList<>(ranking);

        RankTop5SpotDto rankTop5SpotDto = RankTop5SpotDto.builder()
                .rankSpot1(rankingList.get(0))
                .rankSpot2(rankingList.get(1))
                .rankSpot3(rankingList.get(2))
                .rankSpot4(rankingList.get(3))
                .rankSpot5(rankingList.get(4))
                .build();

        return rankTop5SpotDto;
    }
}
