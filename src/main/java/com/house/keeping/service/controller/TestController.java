package com.house.keeping.service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试控制器
 */
@RestController
@RequestMapping("/test")
@Tag(name = "测试接口", description = "系统测试相关接口")
public class TestController {
    /**
     * Ping测试
     */
    @GetMapping("/ping")
    @Operation(summary = "Ping测试", description = "测试系统是否可访问")
    public String ping() {
        return "pong";
    }
}