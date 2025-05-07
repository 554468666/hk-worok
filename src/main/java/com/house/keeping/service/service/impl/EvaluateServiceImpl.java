package com.house.keeping.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.house.keeping.service.entity.EvaluateEntity;
import com.house.keeping.service.mapper.EvaluateMapper;
import com.house.keeping.service.service.EvaluateService;
import org.springframework.stereotype.Service;

@Service
public class EvaluateServiceImpl extends ServiceImpl<EvaluateMapper, EvaluateEntity> implements EvaluateService {
}