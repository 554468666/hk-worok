package com.house.keeping.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.house.keeping.service.entity.ServiceEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ServiceMapper extends BaseMapper<ServiceEntity> {
}