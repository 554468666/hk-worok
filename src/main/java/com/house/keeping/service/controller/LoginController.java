package com.house.keeping.service.controller;

import com.house.keeping.service.entity.UserEntity;
import com.house.keeping.service.service.RedisService;
import com.house.keeping.service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class LoginController {

    @Autowired
    private UserService userService;
    @Autowired
    private RedisService redisService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginData) {
        String wxOpenId = loginData.get("wxOpenId"); // 获取微信小程序发送的openId
        String wxSessionKey = loginData.get("wxSessionKey"); // 获取微信小程序发送的sessionKey
        String wxUnionId = loginData.get("wxUnionId"); // 获取微信小程序发送的unionId
        String shortId = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(UUID.randomUUID().toString().getBytes());
        // 获取当前时间的毫秒数
        long currentTimeMillis = System.currentTimeMillis();

        // 将毫秒数转换为字符串
        String timeString = String.valueOf(currentTimeMillis);
        // 验证用户是否存在
        if(wxOpenId == null){
            wxOpenId = "admin";
        }
        UserEntity user = userService.findByOpenId(wxOpenId);
        if (user == null) {
            // 如果用户不存在，创建新用户
            user = new UserEntity();
            user.setOpenId(wxOpenId);
            user.setSessionKey(wxSessionKey);
            user.setUnionId(wxUnionId);
            user.setName("用户"+timeString); // 默认用户名
            user.setPhone(""); // 默认电话
            user.setIsMember(false); // 默认非会员
            userService.save(user);
        }

        // 创建登录凭证（token），这里简单返回一个随机字符串
        String token = java.util.UUID.randomUUID().toString();

        // 返回用户信息和token
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("userId", user.getId());
        response.put("name", user.getName());
        response.put("phone", user.getPhone());
        response.put("isMember", user.getIsMember());


        redisService.setWithExpire("current_login_user",user.getName(),1800);
        return ResponseEntity.ok(response);
    }
}