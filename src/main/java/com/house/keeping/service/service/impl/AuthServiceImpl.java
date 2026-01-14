package com.house.keeping.service.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.house.keeping.service.common.BusinessException;
import com.house.keeping.service.common.ErrorCode;
import com.house.keeping.service.entity.*;
import com.house.keeping.service.entity.request.*;
import com.house.keeping.service.entity.response.LoginResponse;
import com.house.keeping.service.entity.response.UserInfoResponse;
import com.house.keeping.service.mapper.UserMapper;
import com.house.keeping.service.service.AuthService;
import com.house.keeping.service.service.RedisService;
import com.house.keeping.service.util.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 认证服务实现
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private RedisService redisService;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    @Value("${hk.wechat.appid}")
    private String wechatAppId;
    
    @Value("${hk.wechat.secret}")
    private String wechatSecret;
    
    private static final Long TOKEN_EXPIRE_TIME = 86400L; // 24小时
    private static final Long REFRESH_TOKEN_EXPIRE_TIME = 604800L; // 7天
    private static final Integer SMS_CODE_EXPIRE_TIME = 300; // 5分钟
    private static final Integer LOGIN_MAX_ATTEMPTS = 5;
    private static final Integer LOGIN_LOCK_TIME = 1800; // 30分钟
    
    @Override
    public LoginResponse login(LoginRequest request) {
        // 检查登录失败次数
        String failKey = "login_fail_" + request.getUsername();
        String failCountStr = redisService.get(failKey);
        if (failCountStr != null && Integer.parseInt(failCountStr) >= LOGIN_MAX_ATTEMPTS) {
            throw new BusinessException(ErrorCode.PASSWORD_ERROR_TOO_MANY);
        }
        
        // 查询用户
        LambdaQueryWrapper<UserEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserEntity::getName, request.getUsername());
        UserEntity user = userMapper.selectOne(wrapper);
        
        // 验证用户名和密码
        if (user == null) {
            increaseFailCount(failKey);
            throw new BusinessException(ErrorCode.USERNAME_OR_PASSWORD_ERROR);
        }
        
        // 验证密码
        if (!BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            increaseFailCount(failKey);
            throw new BusinessException(ErrorCode.USERNAME_OR_PASSWORD_ERROR);
        }
        
        // 检查用户状态
        if ("disabled".equals(user.getStatus())) {
            throw new BusinessException(ErrorCode.FORBIDDEN.getCode(), "账户已被禁用");
        }
        
        // 清除登录失败次数
        redisService.del(failKey);
        
        // 生成Token
        String token = generateToken(user.getId());
        String refreshToken = UUID.randomUUID().toString();
        
        // 存储刷新令牌
        redisService.setWithExpire("refresh_token_" + user.getId(), refreshToken, REFRESH_TOKEN_EXPIRE_TIME);
        
        // 更新最后登录时间
        updateLastLogin(user.getId());
        
        // 返回登录响应
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setTokenType("Bearer");
        response.setExpiresIn(TOKEN_EXPIRE_TIME);
        response.setRefreshToken(refreshToken);
        response.setUserInfo(buildUserInfoResponse(user));
        response.setIsNewUser(false);
        
        return response;
    }
    
    @Override
    public Map<String, Object> sendSms(SendSmsRequest request) {
        // 检查发送频率
        String rateKey = "sms_rate_" + request.getPhone();
        String rateCountStr = redisService.get(rateKey);
        if (rateCountStr != null && Integer.parseInt(rateCountStr) > 0) {
            throw new BusinessException(ErrorCode.SMS_SEND_TOO_FREQUENT);
        }
        
        // 生成验证码
        String code = RandomUtil.randomNumbers(6);
        String sessionId = "sms_session_" + System.currentTimeMillis() + "_" + RandomUtil.randomString(6);
        
        // 存储验证码
        String codeKey = "sms_code_" + request.getPhone() + "_" + request.getType();
        redisService.setWithExpire(codeKey, code, SMS_CODE_EXPIRE_TIME);
        redisService.setWithExpire("sms_session_" + sessionId, codeKey, SMS_CODE_EXPIRE_TIME);

        // 设置发送频率限制（60秒）
        redisService.setWithExpire(rateKey, "1", 60);
        
        // TODO: 调用短信服务商发送验证码
        log.info("发送短信验证码 - 手机号: {}, 验证码: {}, 类型: {}", request.getPhone(), code, request.getType());
        
        Map<String, Object> result = new HashMap<>();
        result.put("sessionId", sessionId);
        result.put("expireTime", SMS_CODE_EXPIRE_TIME);
        result.put("nextSendTime", 60);
        
        return result;
    }
    
    @Override
    public LoginResponse loginBySms(PhoneLoginRequest request) {
        // 验证验证码
        String sessionKey = "sms_session_" + request.getSessionId();
        String codeKey = (String) redisService.get(sessionKey);
        if (codeKey == null) {
            throw new BusinessException(ErrorCode.VERIFICATION_CODE_EXPIRED);
        }
        
        String storedCode = (String) redisService.get(codeKey);
        if (storedCode == null || !storedCode.equals(request.getCode())) {
            throw new BusinessException(ErrorCode.VERIFICATION_CODE_ERROR);
        }
        
        // 查询用户
        LambdaQueryWrapper<UserEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserEntity::getPhone, request.getPhone());
        UserEntity user = userMapper.selectOne(wrapper);
        
        boolean isNewUser = false;
        
        if (user == null) {
            // 新用户，创建账户
            user = new UserEntity();
            user.setName("user_" + request.getPhone());
            user.setPhone(request.getPhone());
            user.setRole("member");
            user.setStatus("active");
            user.setCreatedAt(new Date());
            userMapper.insert(user);
            isNewUser = true;
        }
        
        // 清除验证码
        redisService.del(codeKey);
        redisService.del(sessionKey);
        
        // 检查用户状态
        if ("disabled".equals(user.getStatus())) {
            throw new BusinessException(ErrorCode.FORBIDDEN.getCode(), "账户已被禁用");
        }
        
        // 生成Token
        String token = generateToken(user.getId());
        String refreshToken = UUID.randomUUID().toString();
        
        // 存储刷新令牌
        redisService.setWithExpire("refresh_token_" + user.getId(), refreshToken, REFRESH_TOKEN_EXPIRE_TIME);
        
        // 更新最后登录时间
        updateLastLogin(user.getId());
        
        // 返回登录响应
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setTokenType("Bearer");
        response.setExpiresIn(TOKEN_EXPIRE_TIME);
        response.setRefreshToken(refreshToken);
        response.setUserInfo(buildUserInfoResponse(user));
        response.setIsNewUser(isNewUser);
        
        return response;
    }
    
    @Override
    public LoginResponse wechatLogin(WeChatLoginRequest request) {
        // TODO: 调用微信接口获取openid和session_key
        // WxSessionEntity wxSession = getWxSession(request.getCode());
        String openId = "mock_openid_" + System.currentTimeMillis();
        String sessionKey = "mock_session_key";
        
        // 查询用户
        LambdaQueryWrapper<UserEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserEntity::getOpenId, openId);
        UserEntity user = userMapper.selectOne(wrapper);
        
        boolean isNewUser = false;
        
        if (user == null) {
            // 新用户，创建账户
            user = new UserEntity();
            user.setName("wx_user_" + RandomUtil.randomString(6));
            user.setOpenId(openId);
            user.setSessionKey(sessionKey);
            user.setRole("member");
            user.setStatus("active");
            user.setCreatedAt(new Date());
            
            // 设置用户信息
            if (request.getUserInfo() != null) {
                WeChatLoginRequest.WeChatUserInfo userInfo = request.getUserInfo();
                user.setNickname(userInfo.getNickname());
                user.setImageUrl(userInfo.getAvatar());
            }
            
            userMapper.insert(user);
            isNewUser = true;
        } else {
            // 更新session_key
            user.setSessionKey(sessionKey);
            userMapper.updateById(user);
        }
        
        // 检查用户状态
        if ("disabled".equals(user.getStatus())) {
            throw new BusinessException(ErrorCode.FORBIDDEN.getCode(), "账户已被禁用");
        }
        
        // 生成Token
        String token = generateToken(user.getId());
        String refreshToken = UUID.randomUUID().toString();
        
        // 存储刷新令牌
        redisService.setWithExpire("refresh_token_" + user.getId(), refreshToken, REFRESH_TOKEN_EXPIRE_TIME);
        
        // 更新最后登录时间
        updateLastLogin(user.getId());
        
        // 返回登录响应
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setTokenType("Bearer");
        response.setExpiresIn(TOKEN_EXPIRE_TIME);
        response.setRefreshToken(refreshToken);
        response.setUserInfo(buildUserInfoResponse(user));
        response.setIsNewUser(isNewUser);
        
        return response;
    }
    
    @Override
    public Map<String, Object> bindPhone(BindPhoneRequest request, String token) {
        // 验证Token并获取用户ID
        Long userId = getUserIdFromToken(token);
        
        // 验证验证码
        String sessionKey = "sms_session_" + request.getSessionId();
        String codeKey = (String) redisService.get(sessionKey);
        if (codeKey == null) {
            throw new BusinessException(ErrorCode.VERIFICATION_CODE_EXPIRED);
        }
        
        String storedCode = (String) redisService.get(codeKey);
        if (storedCode == null || !storedCode.equals(request.getCode())) {
            throw new BusinessException(ErrorCode.VERIFICATION_CODE_ERROR);
        }
        
        // 检查手机号是否已被绑定
        LambdaQueryWrapper<UserEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserEntity::getPhone, request.getPhone());
        wrapper.ne(UserEntity::getId, userId);
        UserEntity existUser = userMapper.selectOne(wrapper);
        if (existUser != null) {
            throw new BusinessException(ErrorCode.USER_ALREADY_EXIST);
        }
        
        // 更新用户手机号
        UserEntity user = userMapper.selectById(userId);
        user.setPhone(request.getPhone());
        userMapper.updateById(user);
        
        // 清除验证码
        redisService.del(codeKey);
        redisService.del(sessionKey);
        
        Map<String, Object> result = new HashMap<>();
        result.put("phone", request.getPhone());
        result.put("bindTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        return result;
    }
    
    @Override
    public void logout(String token) {
        // 验证Token并获取用户ID
        Long userId = getUserIdFromToken(token);
        
        // 将Token加入黑名单
        redisService.setWithExpire("token_blacklist_" + token, "1", TOKEN_EXPIRE_TIME);
        
        // 清除刷新令牌
        redisService.del("refresh_token_" + userId);
        
        log.info("用户登出: userId={}", userId);
    }
    
    @Override
    public LoginResponse refreshToken(RefreshTokenRequest request) {
        // 查找对应的用户ID
        // 这里简化处理，实际应该从refreshToken中解析用户信息
        // 可以将refreshToken设计为包含userId的JWT
        
        // TODO: 从refreshToken解析userId
        Long userId = 1L;
        
        // 验证refreshToken
        String storedRefreshToken = (String) redisService.get("refresh_token_" + userId);
        if (storedRefreshToken == null || !storedRefreshToken.equals(request.getRefreshToken())) {
            throw new BusinessException(ErrorCode.TOKEN_INVALID);
        }
        
        // 获取用户信息
        UserEntity user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }
        
        // 生成新Token
        String newToken = generateToken(user.getId());
        String newRefreshToken = UUID.randomUUID().toString();
        
        // 更新刷新令牌
        redisService.setWithExpire("refresh_token_" + user.getId(), newRefreshToken, REFRESH_TOKEN_EXPIRE_TIME);
        
        // 返回新Token
        LoginResponse response = new LoginResponse();
        response.setToken(newToken);
        response.setTokenType("Bearer");
        response.setExpiresIn(TOKEN_EXPIRE_TIME);
        response.setRefreshToken(newRefreshToken);
        response.setUserInfo(buildUserInfoResponse(user));
        
        return response;
    }
    
    @Override
    public UserInfoResponse getCurrentUser(String token) {
        Long userId = getUserIdFromToken(token);
        UserEntity user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }
        return buildUserInfoResponse(user);
    }
    
    /**
     * 生成Token
     */
    private String generateToken(Long userId) {
        return jwtUtils.generate(String.valueOf(userId));
    }
    
    /**
     * 从Token获取用户ID
     */
    private Long getUserIdFromToken(String token) {
        // 移除 "Bearer " 前缀
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        String userIdStr = jwtUtils.getOpenid(token);
        if (userIdStr == null) {
            throw new BusinessException(ErrorCode.TOKEN_INVALID);
        }

        // 检查Token是否在黑名单中
        if (redisService.get("token_blacklist_" + token) != null) {
            throw new BusinessException(ErrorCode.TOKEN_EXPIRED);
        }

        return Long.parseLong(userIdStr);
    }
    
    /**
     * 增加登录失败次数
     */
    private void increaseFailCount(String key) {
        String countStr = redisService.get(key);
        Integer count = 0;
        if (countStr != null) {
            count = Integer.parseInt(countStr);
        }
        count++;
        redisService.setWithExpire(key, String.valueOf(count), LOGIN_LOCK_TIME);
    }
    
    /**
     * 更新最后登录时间
     */
    private void updateLastLogin(Long userId) {
        UserEntity user = userMapper.selectById(userId);
        user.setLastLogin(new Date());
        userMapper.updateById(user);
    }
    
    /**
     * 构建用户信息响应
     */
    private UserInfoResponse buildUserInfoResponse(UserEntity user) {
        UserInfoResponse response = new UserInfoResponse();
        BeanUtils.copyProperties(user, response);
        response.setUsername(user.getName());
        
        // 脱敏身份证号
        if (user.getIdCard() != null && user.getIdCard().length() > 4) {
            String idCard = user.getIdCard();
            response.setIdCard(idCard.substring(0, 3) + "***********" + idCard.substring(idCard.length() - 4));
        }
        
        // 设置加入日期
        if (user.getCreatedAt() != null) {
            response.setJoinDate(new java.text.SimpleDateFormat("yyyy-MM-dd").format(user.getCreatedAt()));
        }
        
        // 设置最后登录时间
        if (user.getLastLogin() != null) {
            response.setLastLogin(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(user.getLastLogin()));
        }
        
        // 设置权限列表（根据角色）
        response.setPermissions(getPermissionsByRole(user.getRole()));
        
        return response;
    }
    
    /**
     * 根据角色获取权限列表
     */
    private List<String> getPermissionsByRole(String role) {
        List<String> permissions = new ArrayList<>();
        
        // 基础权限
        permissions.add("user:view");
        permissions.add("task:view");
        permissions.add("task:create");
        permissions.add("file:upload");
        
        if ("admin".equals(role)) {
            // 管理员权限
            permissions.add("user:create");
            permissions.add("user:update");
            permissions.add("user:delete");
            permissions.add("task:update");
            permissions.add("task:delete");
            permissions.add("system:manage");
        } else if ("manager".equals(role)) {
            // 管理者权限
            permissions.add("user:create");
            permissions.add("user:update");
            permissions.add("task:update");
            permissions.add("task:delete");
        }
        
        return permissions;
    }
}
