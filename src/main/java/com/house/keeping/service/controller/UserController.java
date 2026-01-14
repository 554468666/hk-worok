package com.house.keeping.service.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.house.keeping.service.common.ErrorCode;
import com.house.keeping.service.common.Result;
import com.house.keeping.service.entity.UserEntity;
import com.house.keeping.service.entity.UserInfoRequestEntity;
import com.house.keeping.service.entity.request.*;
import com.house.keeping.service.entity.response.UserInfoResponse;
import com.house.keeping.service.service.AuthService;
import com.house.keeping.service.service.RedisService;
import com.house.keeping.service.service.UserService;
import com.house.keeping.service.util.CurrentUserUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
@Tag(name = "用户管理", description = "用户管理接口")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private RedisService redisService;
    
    @Autowired
    private AuthService authService;
    
    /**
     * 获取用户列表（分页）
     */
    @GetMapping
    @Operation(summary = "获取用户列表", description = "分页获取系统用户列表，支持多条件搜索和筛选")
    public Result<Map<String, Object>> getUsers(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String role,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder) {
        
        Page<UserEntity> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<UserEntity> wrapper = new LambdaQueryWrapper<>();
        
        // 搜索关键词
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(UserEntity::getName, keyword)
                    .or().like(UserEntity::getNickname, keyword)
                    .or().like(UserEntity::getPhone, keyword)
                    .or().like(UserEntity::getEmail, keyword));
        }
        
        // 状态筛选
        if (StringUtils.hasText(status) && !"all".equals(status)) {
            wrapper.eq(UserEntity::getStatus, status);
        }
        
        // 角色筛选
        if (StringUtils.hasText(role)) {
            wrapper.eq(UserEntity::getRole, role);
        }
        
        // 排序
        if ("asc".equals(sortOrder)) {
            if ("id".equals(sortBy)) {
                wrapper.orderByAsc(UserEntity::getId);
            } else if ("username".equals(sortBy)) {
                wrapper.orderByAsc(UserEntity::getName);
            } else if ("createdAt".equals(sortBy)) {
                wrapper.orderByAsc(UserEntity::getCreatedAt);
            } else if ("lastLogin".equals(sortBy)) {
                wrapper.orderByAsc(UserEntity::getLastLogin);
            } else {
                wrapper.orderByAsc(UserEntity::getCreatedAt);
            }
        } else {
            if ("id".equals(sortBy)) {
                wrapper.orderByDesc(UserEntity::getId);
            } else if ("username".equals(sortBy)) {
                wrapper.orderByDesc(UserEntity::getName);
            } else if ("createdAt".equals(sortBy)) {
                wrapper.orderByDesc(UserEntity::getCreatedAt);
            } else if ("lastLogin".equals(sortBy)) {
                wrapper.orderByDesc(UserEntity::getLastLogin);
            } else {
                wrapper.orderByDesc(UserEntity::getCreatedAt);
            }
        }
        
        // 软删除过滤
        wrapper.eq(UserEntity::getIsDeleted, false);
        
        IPage<UserEntity> userPage = userService.page(pageParam, wrapper);
        
        Map<String, Object> data = new HashMap<>();
        data.put("list", userPage.getRecords());
        data.put("pagination", Map.of(
            "page", userPage.getCurrent(),
            "pageSize", userPage.getSize(),
            "total", userPage.getTotal(),
            "totalPages", userPage.getPages(),
            "hasMore", userPage.getCurrent() < userPage.getPages()
        ));
        
        return Result.success(data);
    }
    
    /**
     * 获取用户详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取用户详情", description = "获取指定用户的详细信息")
    public Result<UserInfoResponse> getUserById(@PathVariable Long id,
                                                 @RequestHeader(value = "Authorization", required = false) String token) {
        // TODO: 验证权限
        UserEntity user = userService.getById(id);
        if (user == null) {
            throw new com.house.keeping.service.common.BusinessException(ErrorCode.USER_NOT_EXIST);
        }
        
        UserInfoResponse userInfo = buildUserInfoResponse(user);
        return Result.success(userInfo);
    }
    
    /**
     * 新增用户
     */
    @PostMapping
    @Operation(summary = "新增用户", description = "创建新用户")
    public Result<Map<String, Object>> addUser(@RequestBody CreateUserRequest request) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<UserEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserEntity::getName, request.getUsername());
        UserEntity existUser = userService.getOne(wrapper);
        if (existUser != null) {
            throw new com.house.keeping.service.common.BusinessException(ErrorCode.USER_ALREADY_EXIST);
        }
        
        UserEntity user = new UserEntity();
        user.setName(request.getUsername());
        // 密码加密
        user.setPassword(cn.hutool.crypto.digest.BCrypt.hashpw(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setRole(request.getRole() != null ? request.getRole() : "member");
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setIdCard(request.getIdCard());
        user.setAddress(request.getAddress());
        user.setStatus("active");
        user.setIsVerified(false);
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        user.setIsDeleted(false);
        
        userService.save(user);
        
        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("username", user.getName());
        data.put("role", user.getRole());
        data.put("status", user.getStatus());
        data.put("joinDate", new java.text.SimpleDateFormat("yyyy-MM-dd").format(user.getCreatedAt()));
        
        return Result.success(data);
    }
    
    /**
     * 更新用户
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新用户", description = "更新用户信息")
    public Result<Map<String, Object>> updateUser(@PathVariable Long id,
                                                    @RequestBody UpdateUserRequest request) {
        UserEntity user = userService.getById(id);
        if (user == null) {
            throw new com.house.keeping.service.common.BusinessException(ErrorCode.USER_NOT_EXIST);
        }
        
        if (StringUtils.hasText(request.getNickname())) {
            user.setNickname(request.getNickname());
        }
        if (StringUtils.hasText(request.getPassword())) {
            user.setPassword(cn.hutool.crypto.digest.BCrypt.hashpw(request.getPassword()));
        }
        if (StringUtils.hasText(request.getRole())) {
            user.setRole(request.getRole());
        }
        if (StringUtils.hasText(request.getEmail())) {
            user.setEmail(request.getEmail());
        }
        if (StringUtils.hasText(request.getPhone())) {
            user.setPhone(request.getPhone());
        }
        if (StringUtils.hasText(request.getIdCard())) {
            user.setIdCard(request.getIdCard());
        }
        if (StringUtils.hasText(request.getAddress())) {
            user.setAddress(request.getAddress());
        }
        
        user.setUpdatedAt(new Date());
        userService.updateById(user);
        
        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("updateTime", new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(user.getUpdatedAt()));
        
        return Result.success(data);
    }
    
    /**
     * 删除用户（软删除）
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户", description = "删除指定用户（软删除）")
    public Result<Map<String, Object>> deleteUser(@PathVariable Long id,
                                                    @RequestHeader("Authorization") String token) {
        UserEntity user = userService.getById(id);
        if (user == null) {
            throw new com.house.keeping.service.common.BusinessException(ErrorCode.USER_NOT_EXIST);
        }
        
        // 软删除
        user.setIsDeleted(true);
        user.setUpdatedAt(new Date());
        userService.updateById(user);
        
        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("deleteTime", new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        
        return Result.success(data);
    }
    
    /**
     * 切换用户状态
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "切换用户状态", description = "启用/禁用用户")
    public Result<Map<String, Object>> updateUserStatus(@PathVariable Long id,
                                                         @RequestBody Map<String, String> request) {
        UserEntity user = userService.getById(id);
        if (user == null) {
            throw new com.house.keeping.service.common.BusinessException(ErrorCode.USER_NOT_EXIST);
        }
        
        String status = request.get("status");
        if (!"active".equals(status) && !"disabled".equals(status)) {
            throw new com.house.keeping.service.common.BusinessException(ErrorCode.BAD_REQUEST, "状态值不正确");
        }
        
        user.setStatus(status);
        user.setUpdatedAt(new Date());
        userService.updateById(user);
        
        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("status", status);
        data.put("updateTime", new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        
        return Result.success(data);
    }
    
    /**
     * 获取微信用户信息（解密）
     */
    @PostMapping("/getUserInfo")
    public Result<String> getUserInfo(@RequestBody UserInfoRequestEntity request) {
        try {
            String sessionKey = redisService.get("current_login_user_" + request.getOpenid());
            
            if (sessionKey == null) {
                return Result.error("获取sessionKey为空");
            }
            
            String decryptedData = decryptUserInfo(
                request.getEncryptedData(),
                sessionKey,
                request.getIv()
            );
            
            return Result.success(decryptedData);
        } catch (Exception e) {
            log.error("获取用户信息失败", e);
            return Result.error("获取用户信息失败");
        }
    }
    
    /**
     * 解密用户信息
     */
    private String decryptUserInfo(String encryptedData, String sessionKey, String iv) throws Exception {
        byte[] sessionKeyBytes = Base64.getDecoder().decode(sessionKey);
        byte[] encryptedDataBytes = Base64.getDecoder().decode(encryptedData);
        byte[] ivBytes = Base64.getDecoder().decode(iv);
        
        SecretKeySpec secretKeySpec = new SecretKeySpec(sessionKeyBytes, "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(ivBytes);
        
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        
        byte[] decryptedBytes = cipher.doFinal(encryptedDataBytes);
        return new String(decryptedBytes, "UTF-8");
    }
    
    /**
     * 构建用户信息响应
     */
    private UserInfoResponse buildUserInfoResponse(UserEntity user) {
        UserInfoResponse response = new UserInfoResponse();
        response.setId(user.getId());
        response.setUsername(user.getName());
        response.setNickname(user.getNickname());
        response.setRole(user.getRole());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setAvatar(user.getImageUrl());
        response.setStatus(user.getStatus());
        response.setIsVerified(user.getIsVerified());
        response.setAddress(user.getAddress());
        response.setWechatOpenId(user.getOpenId());
        response.setWechatUnionId(user.getUnionId());
        
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
        
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        response.setLoginCount(user.getLoginCount());
        
        return response;
    }
}