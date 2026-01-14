package com.house.keeping.service.controller;

import com.house.keeping.service.entity.request.MenuRequest;
import com.house.keeping.service.service.MenuService;
import com.house.keeping.service.util.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 菜单控制器
 */
@RestController
@RequestMapping("/menu")
@Tag(name = "菜单管理", description = "菜单管理相关接口")
public class MenuController {

    @Autowired
    private MenuService menuService;

    /**
     * 获取菜单数据
     */
    @PostMapping("/queryMenuList")
    @Operation(summary = "获取菜单数据", description = "获取当前用户的菜单列表")
    public R queryMenuList(@RequestBody MenuRequest menuRequest) {
       return R.success(menuService.queryMenuList(menuRequest));
    }
}
