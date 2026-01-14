package com.house.keeping.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.house.keeping.service.entity.MenuEntity;
import com.house.keeping.service.entity.request.MenuRequest;
import com.house.keeping.service.mapper.MenuMapper;
import com.house.keeping.service.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuServiceImpl implements MenuService {

    @Autowired
    private MenuMapper mapper;

    @Override
    public List<MenuEntity> queryMenuList(MenuRequest menuRequest) {
        LambdaQueryWrapper<MenuEntity> wrapper = new LambdaQueryWrapper<MenuEntity>();
        wrapper.eq(MenuEntity::getMenuType,menuRequest.getMenuType());
        wrapper.eq(MenuEntity::getMenuStatus,true);
        return mapper.selectList(wrapper);
    }
}
