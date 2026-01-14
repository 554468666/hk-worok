package com.house.keeping.service.service.impl;

import com.house.keeping.service.service.TaskExpirationService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RQueue;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 任务过期管理服务实现
 */
@Slf4j
@Service
public class TaskExpirationServiceImpl implements TaskExpirationService {

    @Autowired
    private RedissonClient redissonClient;

    private static final String TASK_EXPIRATION_QUEUE = "task:expiration:queue";

    @Override
    public void setTaskExpiration(Long taskId, long delay) {
        setTaskExpiration(taskId, delay, TimeUnit.MILLISECONDS);
    }

    @Override
    public void setTaskExpiration(Long taskId, long delay, TimeUnit timeUnit) {
        try {
            RQueue<Long> queue = redissonClient.getQueue(TASK_EXPIRATION_QUEUE);
            RDelayedQueue<Long> delayedQueue = redissonClient.getDelayedQueue(queue);
            delayedQueue.offer(taskId, delay, timeUnit);
            log.info("任务已设置过期: taskId={}, delay={} {}", taskId, delay, timeUnit);
        } catch (Exception e) {
            log.error("设置任务过期失败: taskId={}", taskId, e);
        }
    }

    @Override
    public void cancelTaskExpiration(Long taskId) {
        try {
            RQueue<Long> queue = redissonClient.getQueue(TASK_EXPIRATION_QUEUE);
            RDelayedQueue<Long> delayedQueue = redissonClient.getDelayedQueue(queue);
            boolean removed = delayedQueue.remove(taskId);
            if (removed) {
                log.info("任务过期已取消: taskId={}", taskId);
            } else {
                log.warn("任务不在过期队列中: taskId={}", taskId);
            }
        } catch (Exception e) {
            log.error("取消任务过期失败: taskId={}", taskId, e);
        }
    }
}
