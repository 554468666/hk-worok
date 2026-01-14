package com.house.keeping.service.service;

/**
 * 任务过期管理服务接口
 */
public interface TaskExpirationService {

    /**
     * 为任务设置过期时间
     * @param taskId 任务ID
     * @param delay 延迟时间（毫秒）
     */
    void setTaskExpiration(Long taskId, long delay);

    /**
     * 为任务设置过期时间
     * @param taskId 任务ID
     * @param delay 延迟时间数值
     * @param timeUnit 时间单位
     */
    void setTaskExpiration(Long taskId, long delay, java.util.concurrent.TimeUnit timeUnit);

    /**
     * 取消任务过期
     * @param taskId 任务ID
     */
    void cancelTaskExpiration(Long taskId);
}
