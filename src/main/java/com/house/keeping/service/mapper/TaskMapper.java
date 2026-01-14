package com.house.keeping.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.house.keeping.service.entity.TaskEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 任务Mapper
 */
@Mapper
public interface TaskMapper extends BaseMapper<TaskEntity> {
}
