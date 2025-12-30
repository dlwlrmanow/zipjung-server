package com.zipjung.backend.service;

import com.zipjung.backend.dto.RankTopSpotDto;
import com.zipjung.backend.dto.RankTopSpotResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisRankService {
    private final StringRedisTemplate redisTemplate;

    private static final String POPULAR_SPOT_KEY = "POPULAR_SPOT_KEYWORD";
    private static final String POPULAR_SPOT_URL_KEY = "POPULAR_SPOT_URL";

    public void increasePopularSpotScore(String spotName, Long placeId) {
        String today = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String rankKey = POPULAR_SPOT_KEY + today;

        // score
        Double rankingScore = redisTemplate.opsForZSet().incrementScore(rankKey, spotName, 1);
        log.info("{} Ranking score is {}", spotName, rankingScore);

        // url
        String hashKey = POPULAR_SPOT_URL_KEY + today;
        String url = "https://map.kakao.com/link/map/" + placeId;
        redisTemplate.opsForHash().put(hashKey, spotName, url);

        // TTL 한국 시간 아침 9시 초기화
        redisTemplate.expire(rankKey, 3, TimeUnit.DAYS);
        redisTemplate.expire(hashKey, 3, TimeUnit.DAYS);
    }

    // DTO 반환하기
    public RankTopSpotResponse getRankTop5Spot() {
        String today = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String rankkey = POPULAR_SPOT_KEY + today;
        String hashKey = POPULAR_SPOT_URL_KEY + today;

        List<String> rankingList = new ArrayList<>(redisTemplate.opsForZSet().reverseRange(rankkey, 0, 4));

        if(rankingList.isEmpty()) { // 데이터가 존재하지 않는 경우 빈배열 -> 클라이언트가 처리
            return new RankTopSpotResponse(Collections.emptyList());
        }

        List<RankTopSpotDto> dtos = new ArrayList<>(rankingList.size());

        for(int i = 0; i < rankingList.size(); i++) {
            String spotName = rankingList.get(i);
            Object urlObj = redisTemplate.opsForHash().get(hashKey, spotName);

            // null check (null이면 빈배열 리턴)
            String url = (urlObj != null) ? urlObj.toString() : "";

            RankTopSpotDto rankTopSpotDto = RankTopSpotDto.builder()
                    .rank(i + 1)
                    .spot(rankingList.get(i))
                    .url(url)
                    .build();

            dtos.add(rankTopSpotDto);
            log.info("***********{} Ranking score is {} ***********", spotName, rankTopSpotDto.getUrl());
        }

        RankTopSpotResponse rankTopSpotResponse = RankTopSpotResponse.builder()
                .ranks(dtos)
                .build();

        return rankTopSpotResponse;

    }
}
