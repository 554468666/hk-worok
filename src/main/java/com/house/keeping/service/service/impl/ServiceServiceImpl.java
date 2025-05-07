package com.house.keeping.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.house.keeping.service.entity.ServiceEntity;
import com.house.keeping.service.mapper.ServiceMapper;
import com.house.keeping.service.service.ServiceService;
import org.springframework.stereotype.Service;

@Service
public class ServiceServiceImpl extends ServiceImpl<ServiceMapper, ServiceEntity> implements ServiceService {

}