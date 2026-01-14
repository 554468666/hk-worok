package com.house.keeping.service.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.house.keeping.service.entity.OrderEntity;
import com.house.keeping.service.entity.UserEntity;
import com.house.keeping.service.service.OrderService;
import com.house.keeping.service.util.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单管理控制器
 */
@RestController
@RequestMapping("/order")
@Tag(name = "订单管理", description = "订单管理相关接口")
public class OrderController {
    @Autowired
    private OrderService orderService;

    /**
     * 获取订单列表
     */
    @GetMapping("/query")
    @Operation(summary = "获取订单列表", description = "分页获取订单列表")
    public IPage<OrderEntity> getAllOrders(@RequestParam(defaultValue = "1") Integer current,
                                           @RequestParam(defaultValue = "10") Integer size,
                                           @RequestBody OrderEntity orderEntity) {
        IPage<OrderEntity> page = new Page<>(current, size);
        LambdaQueryWrapper<OrderEntity> wrapper = new LambdaQueryWrapper<OrderEntity>();
        if(!ObjectUtils.isEmpty(orderEntity) && null != orderEntity.getUserId()){
            wrapper.eq(OrderEntity::getUserId,orderEntity.getUserId());
        }
        if (!ObjectUtils.isEmpty(orderEntity) && null != orderEntity.getServiceId()){
            wrapper.eq(OrderEntity::getServiceId,orderEntity.getServiceId());
        }
        return orderService.page(page,wrapper);
    }

    /**
     * 新增订单
     */
    @PostMapping("/add")
    @Operation(summary = "新增订单", description = "创建新的订单")
    public R addOrder(@RequestBody OrderEntity order) {
        return new R(orderService.save(order));
    }

    /**
     * 删除订单
     */
    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除订单", description = "根据ID删除订单")
    public R deleteOrder(@PathVariable Long id) {
        return new R(orderService.removeById(id));
    }

    /**
     * 修改订单
     */
    @PutMapping("/update/{id}")
    @Operation(summary = "修改订单", description = "更新订单信息")
    public R updateOrder(@PathVariable Long id, @RequestBody OrderEntity order) {
        order.setId(id);
        return new R(orderService.updateById(order));
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/info/{id}")
    @Operation(summary = "获取订单详情", description = "获取指定订单的详细信息")
    public com.house.keeping.service.common.Result<OrderEntity> getOrderById(@PathVariable Long id) {
        OrderEntity order = orderService.getById(id);
        if (order == null) {
            throw new com.house.keeping.service.common.BusinessException(
                com.house.keeping.service.common.ErrorCode.BAD_REQUEST, "订单不存在");
        }
        return com.house.keeping.service.common.Result.success(order);
    }
}