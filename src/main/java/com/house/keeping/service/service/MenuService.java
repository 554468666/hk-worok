package com.house.keeping.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.house.keeping.service.entity.MenuEntity;
import com.house.keeping.service.entity.UserEntity;
import com.house.keeping.service.entity.request.MenuRequest;

import java.util.List;

public interface MenuService {

    /**
     * 根据条件获取菜单列表
     * @param menuRequest
     * @return
     */
    List<MenuEntity> queryMenuList(MenuRequest menuRequest);
}
