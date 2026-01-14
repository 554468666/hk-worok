package com.house.keeping.service.controller;

import com.house.keeping.service.entity.*;
import com.house.keeping.service.service.LoginService;
import com.house.keeping.service.service.RedisService;
import com.house.keeping.service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 登录控制器
 */
@RestController
@RequestMapping("/api")
@Tag(name = "登录管理", description = "登录相关接口")
public class LoginController {

    @Autowired
    private UserService userService;
    @Autowired
    private RedisService redisService;

    @Autowired
    private LoginService loginService;

    @Value("${hk.wechat.appid}")
    private String appid;

    @Value("${hk.wechat.secret}")
    private String secret;

    /**
     * Code 换 Session
     */
    @PostMapping("/code2session")
    @Operation(summary = "Code换Session", description = "使用微信code换取session")
    public WxSessionEntity code2Session(@RequestBody Map<String,String> body) {
        return loginService.code2Session(body.get("code"));
    }

    /**
     * 手机号登录/注册
     */
    @PostMapping("/phoneLogin")
    @Operation(summary = "手机号登录", description = "通过手机号进行登录或注册")
    public Map<String,Object> phoneLogin(@RequestBody PhoneLoginDTO dto, HttpSession session) {
        return loginService.phoneLogin(dto,session);
    }

    /**
     * 检查用户手机号
     */
    @PostMapping("/checkUserPhone")
    @Operation(summary = "检查用户手机号", description = "检查手机号是否已注册")
    public Map<String, Object> checkUserPhone(@RequestBody Map<String, String> params) {
        return loginService.checkUserPhone(params);
    }

}