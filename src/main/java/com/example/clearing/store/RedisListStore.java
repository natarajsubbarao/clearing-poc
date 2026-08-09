package com.example.clearing.store;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisListStore implements ListStore {

    private final RedisTemplate<String, String> redisTemplate;

    public RedisListStore(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String leftPop(String key) {
        return redisTemplate.opsForList().leftPop(key);
    }

    @Override
    public void rightPush(String key, String value) {
        redisTemplate.opsForList().rightPush(key, value);
    }

    @Override
    public void leftPush(String key, String value) {
        redisTemplate.opsForList().leftPush(key, value);
    }

    @Override
    public long size(String key) {
        Long l = redisTemplate.opsForList().size(key);
        return l == null ? 0L : l;
    }
}