package com.house.keeping.service.controller;

import com.house.keeping.service.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查控制器
 */
@RestController
@RequestMapping("/api")
@Tag(name = "系统健康检查", description = "系统健康检查接口")
public class HealthCheckController {
    
    @GetMapping("/health")
    @Operation(summary = "健康检查", description = "检查系统是否正常运行")
    public Result<Map<String, Object>> health() {
        Map<String, Object> data = new HashMap<>();
        data.put("status", "UP");
        data.put("timestamp", LocalDateTime.now().toString());
        data.put("version", "1.0.0");
        return Result.success(data);
    }
    
    @GetMapping("/version")
    @Operation(summary = "获取版本", description = "获取系统版本信息")
    public Result<Map<String, String>> version() {
        Map<String, String> data = new HashMap<>();
        data.put("version", "1.0.0");
        data.put("name", "Housekeeping Management System");
        data.put("buildDate", "2026-01-05");
        return Result.success(data);
    }
}
