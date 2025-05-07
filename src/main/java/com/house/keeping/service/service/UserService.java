package com.house.keeping.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.house.keeping.service.entity.UserEntity;

public interface UserService extends IService<UserEntity> {
    UserEntity findByOpenId(String openId);
}