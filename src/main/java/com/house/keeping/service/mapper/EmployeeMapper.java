package com.house.keeping.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.house.keeping.service.entity.EmployeeEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<EmployeeEntity> {
}
