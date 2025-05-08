package com.house.keeping.service.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.house.keeping.service.entity.ServiceEntity;
import com.house.keeping.service.service.ServiceService;
import com.house.keeping.service.util.R;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/services")
@Tag(name = "服务管理", description = "服务管理接口")
public class ServiceController {
    @Autowired
    private ServiceService serviceService;

    @Tag(name = "新增服务")
    @PostMapping("/add")
    public R addService(@RequestBody ServiceEntity service) {
        return new R(serviceService.save(service));
    }

    @Tag(name = "删除服务")
    @DeleteMapping("/delete/{id}")
    public R deleteService(@PathVariable Integer id) {
        return new R(serviceService.removeById(id));
    }

    @Tag(name = "修改服务")
    @PutMapping("/update/{id}")
    public R updateService(@PathVariable Integer id, @RequestBody ServiceEntity service) {
        service.setId(Long.valueOf(id));
        return new R(serviceService.updateById(service));
    }

    @Tag(name = "服务列表")
    @GetMapping("/query")
    public IPage<ServiceEntity> getAllServices(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestBody ServiceEntity service) {
        // 创建分页对象
        Page<ServiceEntity> page = new Page<>(current, size);
        LambdaQueryWrapper<ServiceEntity> wrapper = null;
        if (!service.getName().isEmpty()){
            wrapper = new LambdaQueryWrapper<ServiceEntity>().eq(ServiceEntity::getName,service.getName());
        }
        return serviceService.page(page,wrapper);
    }

    @Tag(name = "热门服务列表")
    @PostMapping("/query/hot")
    public IPage<ServiceEntity> getAllHotServices(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "4") Integer size,
            @RequestBody ServiceEntity service) {
        // 创建分页对象
        Page<ServiceEntity> page = new Page<>(current, size);
        LambdaQueryWrapper<ServiceEntity> wrapper = null;
        if (!service.getIsHot()){
            wrapper = new LambdaQueryWrapper<ServiceEntity>().eq(ServiceEntity::getIsHot,service.getIsHot());
        }
        return serviceService.page(page,wrapper);
    }

    @Tag(name = "服务详情")
    @GetMapping("/info/{id}")
    public R getServiceById(@PathVariable Integer id) {

        return new R(serviceService.getById(id));
    }

}