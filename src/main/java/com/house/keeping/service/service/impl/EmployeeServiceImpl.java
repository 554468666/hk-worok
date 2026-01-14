package com.house.keeping.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.house.keeping.service.entity.EmployeeEntity;
import com.house.keeping.service.mapper.EmployeeMapper;
import com.house.keeping.service.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, EmployeeEntity> implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Override
    public EmployeeEntity findByUserId(Long userId) {
        LambdaQueryWrapper<EmployeeEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EmployeeEntity::getUserId, userId);
        wrapper.eq(EmployeeEntity::getIsDeleted, false);
        return employeeMapper.selectOne(wrapper);
    }
}
