package com.house.keeping.service.listener;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.house.keeping.service.entity.TaskEntity;
import com.house.keeping.service.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.redisson.api.RQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 任务到期处理器
 * 使用 Redisson 延迟队列实现任务到期自动处理
 */
@Slf4j
@Component
public class TaskExpirationListener implements ApplicationRunner {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private TaskService taskService;

    private static final String TASK_EXPIRATION_QUEUE = "task:expiration:queue";

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 启动延迟队列监听线程
        RQueue<Long> queue = redissonClient.getQueue(TASK_EXPIRATION_QUEUE);
        RDelayedQueue<Long> delayedQueue = redissonClient.getDelayedQueue(queue);

        // 创建监听线程
        Thread listenerThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    // 从延迟队列中获取到期的任务ID
                    Long taskId = queue.poll();
                    if (taskId != null) {
                        handleTaskExpiration(taskId);
                    } else {
                        // 没有到期任务，休眠1秒后继续
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException e) {
                    log.info("任务到期监听线程被中断");
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    log.error("处理任务到期时发生错误", e);
                }
            }
        });

        listenerThread.setName("TaskExpirationListener");
        listenerThread.setDaemon(true);
        listenerThread.start();

        log.info("任务到期监听器已启动");
    }

    /**
     * 处理任务到期
     */
    private void handleTaskExpiration(Long taskId) {
        try {
            log.info("开始处理到期任务: taskId={}", taskId);

            // 查询任务
            TaskEntity task = taskService.getById(taskId);
            if (task == null) {
                log.warn("任务不存在: taskId={}", taskId);
                return;
            }

            // 检查任务状态，只有未完成的任务才标记为过期
            if ("pending".equals(task.getStatus()) || "processing".equals(task.getStatus())) {
                // 更新任务状态为过期
                task.setStatus("overdue");
                task.setUpdatedAt(new Date());
                taskService.updateById(task);

                log.info("任务已标记为过期: taskId={}, title={}", taskId, task.getTitle());
            } else {
                log.info("任务已完成或已取消，无需标记过期: taskId={}, status={}", taskId, task.getStatus());
            }
        } catch (Exception e) {
            log.error("处理任务到期失败: taskId={}", taskId, e);
        }
    }
}
