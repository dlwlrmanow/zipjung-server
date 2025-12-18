// local cache 하나만 사용할거라서 아직 필요 없음

//package com.zipjung.backend.config;
//
//import org.ehcache.config.CacheConfiguration;
//import org.ehcache.config.builders.CacheConfigurationBuilder;
//import org.ehcache.config.builders.ExpiryPolicyBuilder;
//import org.ehcache.config.builders.ResourcePoolsBuilder;
//import org.ehcache.jsr107.Eh107Configuration;
//import org.ehcache.jsr107.EhcacheCachingProvider;
//import org.springframework.cache.CacheManager;
//import org.springframework.cache.jcache.JCacheCacheManager;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import javax.cache.Caching;
//import java.time.Duration;
//
//@Configuration
//public class EhcacheConfiguration {
//    @Bean
//    public JCacheCacheManager ehCacheManager(){
//        EhcacheCachingProvider provider =
//                (EhcacheCachingProvider) Caching.getCachingProvider("org.ehcache.jsr107.EhcacheCachingProvider"); // Provider 를 통해 EHCache 를 JCache 로 연결
//
//        CacheManager cacheManager = provider.getCacheManager(); // CacheManager 생성
//
//        // EHCache 설정 : 힙 메모리 100개 엔트리, TTL 5분
//        CacheConfiguration<String, String> ehcacheConfig = CacheConfigurationBuilder
//                .newCacheConfigurationBuilder(String.class, String.class, ResourcePoolsBuilder.heap(100)) // 캐시 설정 (Key, Value 타입 및 최대 엔트리 수)
//                .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofMinutes(5)))
//                .build();
//
//        // JCache Configuration 으로 변환
//        javax.cache.configuration.Configuration<String, String> cacheConfiguration = Eh107Configuration.fromEhcacheCacheConfiguration(ehcacheConfig);
//
//        cacheManager.createCache("ehcache", cacheConfiguration); // 캐시 생성
//
//        return new JCacheCacheManager(cacheManager); // JCacheCacheManager 로 래핑하여 반환
//    }
//}
