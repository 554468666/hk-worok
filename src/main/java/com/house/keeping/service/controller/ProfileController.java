package com.house.keeping.service.controller;

import com.house.keeping.service.common.ErrorCode;
import com.house.keeping.service.common.Result;
import com.house.keeping.service.entity.UserEntity;
import com.house.keeping.service.entity.request.*;
import com.house.keeping.service.service.AuthService;
import com.house.keeping.service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 个人资料管理控制器
 */
@RestController
@RequestMapping("/profile")
@Tag(name = "个人资料管理", description = "个人资料管理接口")
public class ProfileController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private AuthService authService;
    
    /**
     * 更新个人资料
     */
    @PutMapping
    @Operation(summary = "更新个人资料", description = "当前登录用户更新自己的个人资料")
    public Result<Map<String, Object>> updateProfile(@Valid @RequestBody UpdateProfileRequest request,
                                                       @RequestHeader("Authorization") String token) {
        Long userId = getUserIdFromToken(token);
        UserEntity user = userService.getById(userId);
        
        if (user == null) {
            throw new com.house.keeping.service.common.BusinessException(ErrorCode.USER_NOT_EXIST);
        }
        
        if (StringUtils.hasText(request.getNickname())) {
            user.setNickname(request.getNickname());
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
        
        user.setUpdatedAt(new java.util.Date());
        userService.updateById(user);
        
        Map<String, Object> data = new HashMap<>();
        data.put("updateTime", new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
        
        return Result.success(data);
    }
    
    /**
     * 修改密码
     */
    @PutMapping("/password")
    @Operation(summary = "修改密码", description = "当前登录用户修改自己的密码")
    public Result<Map<String, Object>> changePassword(@Valid @RequestBody ChangePasswordRequest request,
                                                        @RequestHeader("Authorization") String token) {
        Long userId = getUserIdFromToken(token);
        UserEntity user = userService.getById(userId);
        
        if (user == null) {
            throw new com.house.keeping.service.common.BusinessException(ErrorCode.USER_NOT_EXIST);
        }
        
        // 验证当前密码
        if (!cn.hutool.crypto.digest.BCrypt.checkpw(request.getCurrentPassword(), user.getPassword())) {
            throw new com.house.keeping.service.common.BusinessException(ErrorCode.USERNAME_OR_PASSWORD_ERROR);
        }
        
        // 检查新旧密码是否相同
        if (request.getCurrentPassword().equals(request.getNewPassword())) {
            throw new com.house.keeping.service.common.BusinessException(ErrorCode.BAD_REQUEST, "新密码不能与当前密码相同");
        }
        
        // 更新密码
        user.setPassword(cn.hutool.crypto.digest.BCrypt.hashpw(request.getNewPassword()));
        user.setUpdatedAt(new java.util.Date());
        userService.updateById(user);
        
        Map<String, Object> data = new HashMap<>();
        data.put("updateTime", new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
        
        return Result.success(data);
    }
    
    /**
     * 实名认证
     */
    @PostMapping("/verify")
    @Operation(summary = "实名认证", description = "当前登录用户进行实名认证")
    public Result<Map<String, Object>> verifyIdentity(@Valid @RequestBody VerifyIdentityRequest request,
                                                      @RequestHeader("Authorization") String token) {
        Long userId = getUserIdFromToken(token);
        UserEntity user = userService.getById(userId);
        
        if (user == null) {
            throw new com.house.keeping.service.common.BusinessException(ErrorCode.USER_NOT_EXIST);
        }
        
        // 验证手机号是否匹配
        if (!request.getPhone().equals(user.getPhone())) {
            throw new com.house.keeping.service.common.BusinessException(ErrorCode.BAD_REQUEST, "手机号与账户绑定的手机号不一致");
        }
        
        // TODO: 调用第三方实名认证接口验证
        
        // 更新实名认证信息
        user.setRealName(request.getRealName());
        user.setIdCard(request.getIdCard());
        user.setIsVerified(true);
        user.setUpdatedAt(new java.util.Date());
        userService.updateById(user);
        
        Map<String, Object> data = new HashMap<>();
        data.put("isVerified", true);
        data.put("verifiedAt", new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
        data.put("verifiedName", request.getRealName().charAt(0) + "*");
        
        return Result.success(data);
    }

    /**
     * 上传头像
     */
    @PostMapping("/avatar")
    @Operation(summary = "上传头像", description = "上传当前登录用户的头像")
    public Result<Map<String, Object>> uploadAvatar(@RequestHeader("Authorization") String token,
                                                    MultipartFile file) {
        Long userId = getUserIdFromToken(token);
        UserEntity user = userService.getById(userId);
        
        if (user == null) {
            throw new com.house.keeping.service.common.BusinessException(ErrorCode.USER_NOT_EXIST);
        }
        
        // 验证文件
        if (file.isEmpty()) {
            throw new com.house.keeping.service.common.BusinessException(ErrorCode.BAD_REQUEST, "文件不能为空");
        }
        
        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || 
            (!contentType.startsWith("image/jpeg") && 
             !contentType.startsWith("image/jpg") && 
             !contentType.startsWith("image/png") && 
             !contentType.startsWith("image/gif"))) {
            throw new com.house.keeping.service.common.BusinessException(ErrorCode.BAD_REQUEST, "只支持图片格式");
        }
        
        // 验证文件大小（5MB）
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new com.house.keeping.service.common.BusinessException(ErrorCode.BAD_REQUEST, "文件大小不能超过5MB");
        }
        
        try {
            // 获取文件名和扩展名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            
            // 生成新文件名
            String newFilename = userId + "_" + System.currentTimeMillis() + extension;
            
            // 保存文件
            java.nio.file.Path uploadDir = java.nio.file.Paths.get("./uploads/avatar");
            if (!java.nio.file.Files.exists(uploadDir)) {
                java.nio.file.Files.createDirectories(uploadDir);
            }
            
            java.nio.file.Path filePath = uploadDir.resolve(newFilename);
            file.transferTo(filePath.toFile());
            
            // 更新用户头像
            String fileUrl = "/uploads/avatar/" + newFilename;
            user.setImageUrl(fileUrl);
            user.setUpdatedAt(new java.util.Date());
            userService.updateById(user);
            
            Map<String, Object> data = new HashMap<>();
            data.put("url", "http://localhost:8080" + fileUrl);
            data.put("filename", newFilename);
            data.put("size", file.getSize());
            data.put("type", contentType);
            data.put("uploadTime", new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
            
            return Result.success(data);
        } catch (Exception e) {
            throw new com.house.keeping.service.common.BusinessException(ErrorCode.BAD_REQUEST, "文件上传失败");
        }
    }
    
    /**
     * 从Token获取用户ID（简化版，实际应该在AuthService中实现）
     */
    private Long getUserIdFromToken(String token) {
        // TODO: 实现Token解析
        // 暂时返回1用于测试
        return 1L;
    }
}
