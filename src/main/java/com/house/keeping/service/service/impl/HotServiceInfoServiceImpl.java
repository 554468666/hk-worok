package com.house.keeping.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.house.keeping.service.entity.HotServiceInfoEntity;
import com.house.keeping.service.mapper.HotServiceInfoMapper;
import com.house.keeping.service.service.HotServiceInfoService;
import org.springframework.stereotype.Service;

@Service
public class HotServiceInfoServiceImpl extends ServiceImpl<HotServiceInfoMapper, HotServiceInfoEntity> implements HotServiceInfoService {
}
