package com.house.keeping.service.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        // 自动填充插入时间
        this.strictInsertFill(metaObject, "createTime", Date.class, new Date());
        // 自动填充插入人
        this.strictInsertFill(metaObject, "createUser", String.class, getCurrentUserId());

        // 由于updateTime和updateUser的fill属性是FieldFill.INSERT_UPDATE，这里也会填充
        this.strictInsertFill(metaObject, "updateTime", Date.class, new Date());
        this.strictInsertFill(metaObject, "updateUser", String.class, getCurrentUserId());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        // 自动填充更新时间
        this.strictUpdateFill(metaObject, "updateTime", Date.class, new Date());
        // 自动填充更新人
        this.strictUpdateFill(metaObject, "updateUser", String.class, getCurrentUserId());
    }

    /**
     * 获取当前用户ID或用户名
     * 这里只是一个示例，实际项目中需要根据你的用户认证系统来获取当前用户信息
     */
    private String getCurrentUserId() {
        // 假设当前用户ID或用户名存储在某个地方，例如线程局部变量、SecurityContext 等
        // 这里返回一个示例值
        return "current_user_id";
    }
}