package com.house.keeping.service.service;

import java.time.Duration;

public interface RedisService {
    public void set(String key, String value);

    /**
     * 添加key并设置失效时间
     * @param key 键
     * @param value 值
     * @param timeout 失效时间（秒）
     */
    public void setWithExpire(String key, String value, long timeout);

    public String get(String key);
}
