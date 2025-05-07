package com.house.keeping.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.house.keeping.service.entity.OrderEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<OrderEntity> {
}