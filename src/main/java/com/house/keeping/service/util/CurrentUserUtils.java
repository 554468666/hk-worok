package com.house.keeping.service.util;

import com.house.keeping.service.entity.UserEntity;
import com.house.keeping.service.service.RedisService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CurrentUserUtils {
    @Autowired
    private RedisService redisService;

    @Autowired
    private HttpSession httpSession;

    /**
     * 从session中获取当前登录的sessionKey
     * 再用sessionKey 获取redis中存入的登录用户信息
     * @return
     */
    public UserEntity getCurrentUser(){
        UserEntity userEntity = new UserEntity();
        String sessionKey = (String) httpSession.getAttribute("sessionKey");
        Map<Object, Object> userMap = redisService.getMap("current_login_user_"+sessionKey);
        userEntity.setRole((String) userMap.get("role"));
        userEntity.setName((String) userMap.get("name"));
        userEntity.setOpenId((String) userMap.get("openId"));
        userEntity.setPhone((String) userMap.get("phone"));
        return userEntity;
    }
}
