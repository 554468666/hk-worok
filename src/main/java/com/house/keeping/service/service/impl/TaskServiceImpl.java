package com.house.keeping.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.house.keeping.service.entity.TaskEntity;
import com.house.keeping.service.mapper.TaskMapper;
import com.house.keeping.service.service.TaskService;
import org.springframework.stereotype.Service;

/**
 * 任务服务实现
 */
@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, TaskEntity> implements TaskService {
}
