package com.house.keeping.service.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.house.keeping.service.entity.ServiceEntity;
import com.house.keeping.service.entity.UserEntity;
import com.house.keeping.service.service.UserService;
import com.house.keeping.service.util.R;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@Tag(name = "User Management", description = "User related operations")
public class UserController {
    @Autowired
    private UserService userService;

    @Tag(name = "新增用户")
    @PostMapping
    public R addUser(@RequestBody UserEntity user) {
        return new R(userService.save(user));
    }

    @Tag(name = "删除用户")
    @DeleteMapping("/{id}")
    public R deleteUser(@PathVariable Integer id) {
        return  new R(userService.removeById(id));
    }

    @Tag(name = "修改用户")
    @PutMapping("/{id}")
    public R updateUser(@PathVariable Long id, @RequestBody UserEntity user) {
        user.setId(id);
        return new R(userService.updateById(user));
    }

    @Tag(name = "用户列表")
    @GetMapping
    public IPage<UserEntity> getAllUsers(@RequestParam(defaultValue = "1") Integer current,
                                         @RequestParam(defaultValue = "10") Integer size,
                                         @RequestBody UserEntity userEntity) {
        try {
            Page<UserEntity> page = new Page<>(current, size);
            LambdaQueryWrapper<UserEntity> wrapper = null;
            if (!userEntity.getName().isEmpty()){
                wrapper = new LambdaQueryWrapper<UserEntity>().eq(UserEntity::getName,userEntity.getName());
            }
            return userService.page(page,wrapper);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Tag(name = "服务详情")
    @GetMapping("/{id}")
    public R getUserById(@PathVariable Integer id) {
        return new R<>(userService.getById(id));
    }
}