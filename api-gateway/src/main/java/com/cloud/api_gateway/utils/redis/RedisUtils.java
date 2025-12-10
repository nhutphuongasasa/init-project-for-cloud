// package com.cloud.api_gateway.utils.redis;

// import org.springframework.data.redis.core.ReactiveRedisTemplate;
// import org.springframework.stereotype.Component;

// import reactor.core.publisher.Mono;

// @Component
// public class RedisUtils {
//     private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

//     public RedisUtils(ReactiveRedisTemplate<String, String> reactiveRedisTemplate) {
//         this.reactiveRedisTemplate = reactiveRedisTemplate;
//     }

//     public Mono<Boolean> setValue(String key, String value) {
//         return reactiveRedisTemplate.opsForValue().set(key, value);
//     }

//     public Mono<String> getValue(String key) {
//         return reactiveRedisTemplate.opsForValue().get(key);
//     }

//     public Mono<Boolean> deleteValue(String key) {
//         return reactiveRedisTemplate.opsForValue().delete(key);
//     }
// }
