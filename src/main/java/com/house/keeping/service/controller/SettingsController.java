package com.house.keeping.service.controller;

import com.house.keeping.service.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 系统设置控制器
 */
@RestController
@RequestMapping("/settings")
@Tag(name = "系统设置", description = "系统设置相关接口")
public class SettingsController {
    
    @Value("${hk.site.name:Housekeeping管理系统}")
    private String siteName;
    
    @Value("${hk.version:1.0.0}")
    private String version;
    
    @Value("${hk.maintenance:false}")
    private Boolean maintenance;
    
    @Value("${hk.registration.enabled:true}")
    private Boolean registrationEnabled;
    
    @Value("${spring.servlet.multipart.max-file-size:10MB}")
    private String maxFileSize;
    
    @Value("${hk.features.sms-login:true}")
    private Boolean smsLogin;
    
    @Value("${hk.features.wechat-login:true}")
    private Boolean wechatLogin;
    
    @Value("${hk.features.email-notification:true}")
    private Boolean emailNotification;
    
    /**
     * 获取系统配置
     */
    @GetMapping
    @Operation(summary = "获取系统配置", description = "获取系统运行配置和参数设置")
    public Result<Map<String, Object>> getSettings() {
        Map<String, Object> settings = new HashMap<>();
        
        // 基本信息
        settings.put("siteName", siteName);
        settings.put("version", version);
        settings.put("maintenance", maintenance);
        
        // 用户设置
        Map<String, Object> userSettings = new HashMap<>();
        userSettings.put("registrationEnabled", registrationEnabled);
        settings.put("user", userSettings);
        
        // 文件设置
        Map<String, Object> fileSettings = new HashMap<>();
        fileSettings.put("maxFileSize", parseMaxFileSize(maxFileSize));
        fileSettings.put("allowedFileTypes", Arrays.asList("jpg", "jpeg", "png", "gif", "pdf", "doc", "docx"));
        settings.put("file", fileSettings);
        
        // 功能开关
        Map<String, Object> features = new HashMap<>();
        features.put("smsLogin", smsLogin);
        features.put("wechatLogin", wechatLogin);
        features.put("emailNotification", emailNotification);
        settings.put("features", features);
        
        return Result.success(settings);
    }
    
    /**
     * 更新系统配置
     */
    @PutMapping
    @Operation(summary = "更新系统配置", description = "更新系统配置")
    public Result<Map<String, Object>> updateSettings(@RequestBody Map<String, Object> request) {
        // TODO: 实现配置更新逻辑
        // 实际应用中应该将配置存储到数据库或配置中心
        
        Map<String, Object> data = new HashMap<>();
        data.put("updateTime", new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        
        return Result.success(data);
    }
    
    /**
     * 解析文件大小
     */
    private Long parseMaxFileSize(String size) {
        if (size == null) {
            return 10 * 1024 * 1024L; // 默认10MB
        }
        
        size = size.toUpperCase().trim();
        if (size.endsWith("KB")) {
            return Long.parseLong(size.substring(0, size.length() - 2)) * 1024;
        } else if (size.endsWith("MB")) {
            return Long.parseLong(size.substring(0, size.length() - 2)) * 1024 * 1024;
        } else if (size.endsWith("GB")) {
            return Long.parseLong(size.substring(0, size.length() - 2)) * 1024 * 1024 * 1024;
        }
        
        return Long.parseLong(size);
    }
}
