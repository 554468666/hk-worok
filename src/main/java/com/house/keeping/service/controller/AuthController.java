package com.house.keeping.service.controller;

import com.house.keeping.service.common.ErrorCode;
import com.house.keeping.service.common.Result;
import com.house.keeping.service.entity.request.*;
import com.house.keeping.service.entity.response.LoginResponse;
import com.house.keeping.service.entity.response.UserInfoResponse;
import com.house.keeping.service.service.AuthService;
import com.house.keeping.service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户认证控制器
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "用户认证", description = "用户认证相关接口")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private UserService userService;
    
    /**
     * 用户名密码登录
     */
    @PostMapping("/login")
    @Operation(summary = "用户名密码登录", description = "通过用户名和密码进行身份验证，获取访问令牌")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return Result.success(response);
    }
    
    /**
     * 发送短信验证码
     */
    @PostMapping("/send-sms")
    @Operation(summary = "发送短信验证码", description = "向指定手机号发送短信验证码，用于登录、注册或密码重置")
    public Result<Map<String, Object>> sendSms(@Valid @RequestBody SendSmsRequest request) {
        Map<String, Object> result = authService.sendSms(request);
        return Result.success(result);
    }
    
    /**
     * 手机号验证码登录
     */
    @PostMapping("/login-sms")
    @Operation(summary = "手机号验证码登录", description = "使用手机号和短信验证码进行快速登录")
    public Result<LoginResponse> loginBySms(@Valid @RequestBody PhoneLoginRequest request) {
        LoginResponse response = authService.loginBySms(request);
        return Result.success(response);
    }
    
    /**
     * 微信授权登录
     */
    @PostMapping("/wechat-login")
    @Operation(summary = "微信授权登录", description = "通过微信小程序或公众号进行授权登录")
    public Result<LoginResponse> wechatLogin(@Valid @RequestBody WeChatLoginRequest request) {
        LoginResponse response = authService.wechatLogin(request);
        return Result.success(response);
    }
    
    /**
     * 微信绑定手机号
     */
    @PostMapping("/bind-phone")
    @Operation(summary = "微信绑定手机号", description = "微信登录用户绑定手机号")
    public Result<Map<String, Object>> bindPhone(@Valid @RequestBody BindPhoneRequest request,
                                                 @RequestHeader("Authorization") String token) {
        Map<String, Object> result = authService.bindPhone(request, token);
        return Result.success(result);
    }
    
    /**
     * 用户登出
     */
    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "用户退出登录，使当前Token失效")
    public Result<Map<String, Object>> logout(@RequestHeader("Authorization") String token) {
        authService.logout(token);
        return Result.success();
    }
    
    /**
     * 刷新Token
     */
    @PostMapping("/refresh")
    @Operation(summary = "刷新Token", description = "使用当前Token获取新的访问令牌")
    public Result<LoginResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        LoginResponse response = authService.refreshToken(request);
        return Result.success(response);
    }
    
    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息")
    public Result<UserInfoResponse> getCurrentUser(@RequestHeader("Authorization") String token) {
        UserInfoResponse userInfo = authService.getCurrentUser(token);
        return Result.success(userInfo);
    }
}
