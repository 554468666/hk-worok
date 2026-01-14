package com.house.keeping.service.service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public interface RedisService {
    void set(String key, String value);

    /**
     * 添加key并设置失效时间
     * @param key 键
     * @param value 值
     * @param timeout 失效时间（秒）
     */
    void setWithExpire(String key, String value, long timeout);

    String get(String key);

    Long getTimeOut(String key);

    void saveMap(String key, Map<String, Object> map);

    Map<Object, Object> getMap(String key);

    void saveMapWithExpiration(String key, Map<String, Object> map, long timeout);
    
    /**
     * 删除key
     */
    void delete(String key);
    
    /**
     * 删除key（别名方法）
     */
    void del(String key);
}
