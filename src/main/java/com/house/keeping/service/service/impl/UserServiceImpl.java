package com.house.keeping.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.house.keeping.service.entity.UserEntity;
import com.house.keeping.service.mapper.UserMapper;
import com.house.keeping.service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {
    @Autowired
    private UserMapper userMapper;

    public UserEntity findByOpenId(String openId) {
        return userMapper.findByOpenId(openId);
    }
}