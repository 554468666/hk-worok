package com.house.keeping.service.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.house.keeping.service.entity.UserEntity;
import com.house.keeping.service.service.RedisService;
import com.house.keeping.service.util.CurrentUserUtils;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Autowired
    private RedisService redisService;

    @Autowired
    private CurrentUserUtils currentUserUtils;

    /**
     * 插入时的填充策略
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("start insert fill.....");
        UserEntity userEntity = currentUserUtils.getCurrentUser();
        log.info("获取登录用户名:"+userEntity.getName());
        this.setFieldValByName("createTime", new Date(), metaObject);
        this.setFieldValByName("updateTime", new Date(), metaObject);

        this.setFieldValByName("createUser", userEntity.getName(),metaObject);
        this.setFieldValByName("updateUser", userEntity.getName(),metaObject);
    }

    /**
     * 更新时的填充策略
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("start update fill.....");
        UserEntity userEntity = currentUserUtils.getCurrentUser();
        log.info("获取登录用户名:"+userEntity.getName());
        this.setFieldValByName("updateTime", new Date(), metaObject);
        this.setFieldValByName("updateUser", userEntity.getName(),metaObject);
    }
}

