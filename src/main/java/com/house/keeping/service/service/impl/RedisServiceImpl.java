package com.house.keeping.service.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisServiceImpl {

    @Autowired
    private StringRedisTemplate redisTemplate;

    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 添加key并设置失效时间
     * @param key 键
     * @param value 值
     * @param timeout 失效时间（秒）
     */
    public void setWithExpire(String key, String value, long timeout) {
        redisTemplate.opsForValue().set(key, value, Duration.ofSeconds(timeout));
    }

    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }
}