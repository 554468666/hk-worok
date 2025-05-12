package com.house.keeping.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.house.keeping.service.entity.SysConfigEntity;
import com.house.keeping.service.mapper.ConfigMapper;
import com.house.keeping.service.service.ConfigService;
import org.springframework.stereotype.Service;

@Service
public class ConfigServiceImpl  extends ServiceImpl<ConfigMapper, SysConfigEntity> implements ConfigService {
}
