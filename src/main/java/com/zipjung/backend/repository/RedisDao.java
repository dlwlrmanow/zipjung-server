package com.zipjung.backend.repository;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RedisDao {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ValueOperations<String, Object> values;

    public RedisDao(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.values = redisTemplate.opsForValue();
    }

    public void setValues(String key, String data) {
        values.set(key, data);
    }

    // RefreshToken용
    public void setValues(String key, String data, Duration duration) {
        values.set(key, data, duration);
    }

    // refresh token 검증
    public Object getValues(String key) {
        return values.get(key);
    }

    // 로그아웃
    public void deleteValues(String key) {
        redisTemplate.delete(key);
    }
}
