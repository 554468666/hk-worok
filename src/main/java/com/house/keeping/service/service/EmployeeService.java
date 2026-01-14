package com.house.keeping.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.house.keeping.service.entity.EmployeeEntity;

public interface EmployeeService extends IService<EmployeeEntity> {
    EmployeeEntity findByUserId(Long userId);
}
