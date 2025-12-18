package com.zipjung.backend.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import javax.cache.Caching;
import java.io.IOException;
import java.time.Duration;

@Configuration
@EnableCaching
public class EhcacheConfiguration {
    // Ehcache (로컬 캐시용)
    @Primary
    @Bean
    public CacheManager ehcacheManager() throws IOException {
        try {
            // XML 파일 경로를 직접 지정해서 캐시 매니저 생성
            Resource resource = new ClassPathResource("ehcache.xml");
            javax.cache.CacheManager cacheManager = Caching.getCachingProvider()
                    .getCacheManager(resource.getURI(), getClass().getClassLoader());
            return new JCacheCacheManager(cacheManager);
        } catch (IOException e) {
            throw new RuntimeException("ehcache.xml 파일을 찾을 수 없어!", e);
        }
    }

    // Redis (JWT용)
    @Bean
    public CacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .entryTtl(Duration.ofMinutes(30)); // 기본 30분 유지

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .build();
    }
}
