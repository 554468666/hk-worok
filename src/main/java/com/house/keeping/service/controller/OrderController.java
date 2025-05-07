package com.house.keeping.service.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.house.keeping.service.entity.OrderEntity;
import com.house.keeping.service.entity.UserEntity;
import com.house.keeping.service.service.OrderService;
import com.house.keeping.service.util.R;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
@Tag(name = "订单管理", description = "订单管理相关接口")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Tag(name = "获取订单列表")
    @GetMapping("/query")
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

    @Tag(name = "新增订单")
    @PostMapping("/add")
    public R addOrder(@RequestBody OrderEntity order) {
        return new R(orderService.save(order));
    }

    @Tag(name = "删除订单")
    @DeleteMapping("/delete/{id}")
    public R deleteOrder(@PathVariable Long id) {
        return new R(orderService.removeById(id));
    }

    @Tag(name = "修改订单")
    @PutMapping("/update/{id}")
    public R updateOrder(@PathVariable Long id, @RequestBody OrderEntity order) {
        order.setId(id);
        return new R(orderService.updateById(order));
    }
}