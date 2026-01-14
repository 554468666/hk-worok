package com.house.keeping.service.service;

import com.house.keeping.service.entity.request.*;
import com.house.keeping.service.entity.response.LoginResponse;
import com.house.keeping.service.entity.response.UserInfoResponse;

import java.util.Map;

/**
 * 认证服务接口
 */
public interface AuthService {
    
    /**
     * 用户名密码登录
     */
    LoginResponse login(LoginRequest request);
    
    /**
     * 发送短信验证码
     */
    Map<String, Object> sendSms(SendSmsRequest request);
    
    /**
     * 手机号验证码登录
     */
    LoginResponse loginBySms(PhoneLoginRequest request);
    
    /**
     * 微信授权登录
     */
    LoginResponse wechatLogin(WeChatLoginRequest request);
    
    /**
     * 微信绑定手机号
     */
    Map<String, Object> bindPhone(BindPhoneRequest request, String token);
    
    /**
     * 用户登出
     */
    void logout(String token);
    
    /**
     * 刷新Token
     */
    LoginResponse refreshToken(RefreshTokenRequest request);
    
    /**
     * 获取当前用户信息
     */
    UserInfoResponse getCurrentUser(String token);
}
