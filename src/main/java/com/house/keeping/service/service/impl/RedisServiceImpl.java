package com.house.keeping.service.service.impl;

import com.house.keeping.service.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class RedisServiceImpl implements RedisService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 添加key并设置失效时间
     * @param key 键
     * @param value 值
     * @param timeout 失效时间（秒）
     */
    @Override
    public void setWithExpire(String key, String value, long timeout) {
        redisTemplate.opsForValue().set(key, value, Duration.ofSeconds(timeout));
    }

    @Override
    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public Long getTimeOut(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    @Override
    public void saveMap(String key, Map<String, Object> map) {
        // 存储Map到Redis
        redisTemplate.opsForHash().putAll(key, map);
    }

    @Override
    public void saveMapWithExpiration(String key, Map<String, Object> map, long timeout) {
        // 存储Map到Redis
        redisTemplate.opsForHash().putAll(key, map);

        // 设置过期时间
        redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
    }

    @Override
    public Map<Object, Object> getMap(String key) {
        // 从Redis中获取Map
        return redisTemplate.opsForHash().entries(key);
    }
    
    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }
    
    @Override
    public void del(String key) {
        redisTemplate.delete(key);
    }
}