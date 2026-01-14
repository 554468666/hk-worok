package com.house.keeping.service.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.house.keeping.service.common.ErrorCode;
import com.house.keeping.service.common.Result;
import com.house.keeping.service.entity.EmployeeEntity;
import com.house.keeping.service.entity.UserEntity;
import com.house.keeping.service.entity.request.CreateEmployeeRequest;
import com.house.keeping.service.entity.request.UpdateEmployeeRequest;
import com.house.keeping.service.entity.response.EmployeeInfoResponse;
import com.house.keeping.service.service.EmployeeService;
import com.house.keeping.service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/employees")
@Tag(name = "员工管理", description = "员工管理接口")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private UserService userService;

    @GetMapping
    @Operation(summary = "获取员工列表", description = "分页获取员工列表，支持多条件搜索和筛选")
    public Result<Map<String, Object>> getEmployees(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "employee") String role,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder) {

        // 首先获取员工列表
        Page<EmployeeEntity> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<EmployeeEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EmployeeEntity::getIsDeleted, false);

        // 排序
        if ("asc".equals(sortOrder)) {
            if ("id".equals(sortBy)) {
                wrapper.orderByAsc(EmployeeEntity::getId);
            } else if ("username".equals(sortBy)) {
                wrapper.orderByAsc(EmployeeEntity::getId);
            } else {
                wrapper.orderByAsc(EmployeeEntity::getCreatedAt);
            }
        } else {
            if ("id".equals(sortBy)) {
                wrapper.orderByDesc(EmployeeEntity::getId);
            } else if ("username".equals(sortBy)) {
                wrapper.orderByDesc(EmployeeEntity::getId);
            } else {
                wrapper.orderByDesc(EmployeeEntity::getCreatedAt);
            }
        }

        IPage<EmployeeEntity> employeePage = employeeService.page(pageParam, wrapper);

        // 构建响应数据
        Map<String, Object> data = new HashMap<>();
        data.put("list", employeePage.getRecords().stream().map(this::buildEmployeeInfoResponse).toList());
        data.put("pagination", Map.of(
            "page", employeePage.getCurrent(),
            "pageSize", employeePage.getSize(),
            "total", employeePage.getTotal(),
            "totalPages", employeePage.getPages(),
            "hasMore", employeePage.getCurrent() < employeePage.getPages()
        ));

        return Result.success(data);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取员工详情", description = "获取指定员工的详细信息")
    public Result<EmployeeInfoResponse> getEmployeeById(@PathVariable Long id) {
        EmployeeEntity employee = employeeService.getById(id);
        if (employee == null || employee.getIsDeleted()) {
            throw new com.house.keeping.service.common.BusinessException(ErrorCode.USER_NOT_EXIST);
        }

        EmployeeInfoResponse response = buildEmployeeInfoResponse(employee);
        return Result.success(response);
    }

    @GetMapping("/by-user/{userId}")
    @Operation(summary = "根据用户ID获取员工信息", description = "根据用户ID获取员工信息")
    public Result<EmployeeInfoResponse> getEmployeeByUserId(@PathVariable Long userId) {
        EmployeeEntity employee = employeeService.findByUserId(userId);
        if (employee == null || employee.getIsDeleted()) {
            throw new com.house.keeping.service.common.BusinessException(ErrorCode.USER_NOT_EXIST);
        }

        EmployeeInfoResponse response = buildEmployeeInfoResponse(employee);
        return Result.success(response);
    }

    @PostMapping
    @Operation(summary = "新增员工", description = "创建新员工，支持选择现有用户或创建新用户")
    public Result<Map<String, Object>> createEmployee(@RequestBody CreateEmployeeRequest request) {
        UserEntity user;
        EmployeeEntity employee = new EmployeeEntity();

        if ("select".equals(request.getMode())) {
            // 选择现有用户
            if (request.getUserId() == null) {
                throw new com.house.keeping.service.common.BusinessException(ErrorCode.BAD_REQUEST, "userId不能为空");
            }
            user = userService.getById(request.getUserId());
            if (user == null) {
                throw new com.house.keeping.service.common.BusinessException(ErrorCode.USER_NOT_EXIST);
            }

            // 更新用户角色为employee
            user.setRole("employee");
            userService.updateById(user);
        } else {
            // 创建新用户
            if (!StringUtils.hasText(request.getUsername()) || !StringUtils.hasText(request.getPassword())) {
                throw new com.house.keeping.service.common.BusinessException(ErrorCode.BAD_REQUEST, "用户名和密码不能为空");
            }

            // 检查用户名是否已存在
            LambdaQueryWrapper<UserEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserEntity::getName, request.getUsername());
            UserEntity existUser = userService.getOne(wrapper);
            if (existUser != null) {
                throw new com.house.keeping.service.common.BusinessException(ErrorCode.USER_ALREADY_EXIST);
            }

            user = new UserEntity();
            user.setName(request.getUsername());
            user.setPassword(cn.hutool.crypto.digest.BCrypt.hashpw(request.getPassword()));
            user.setNickname(request.getNickname());
            user.setRole(request.getRole() != null ? request.getRole() : "employee");
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
        }

        // 创建员工信息
        employee.setUserId(user.getId());
        employee.setWorkYears(request.getWorkYears());
        employee.setIdentity(request.getIdentity());
        employee.setIsTeamLeader(request.getIsTeamLeader() != null ? request.getIsTeamLeader() : false);
        employee.setTeamSize(request.getTeamSize());
        employee.setResume(request.getResume());
        employee.setCreatedAt(new Date());
        employee.setUpdatedAt(new Date());
        employee.setIsDeleted(false);
        employeeService.save(employee);

        Map<String, Object> data = new HashMap<>();
        data.put("id", employee.getId());
        data.put("userId", user.getId());
        data.put("username", user.getName());
        data.put("nickname", user.getNickname());
        data.put("role", user.getRole());
        data.put("joinDate", new java.text.SimpleDateFormat("yyyy-MM-dd").format(user.getCreatedAt()));

        return Result.success(data);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新员工信息", description = "更新员工信息")
    public Result<Map<String, Object>> updateEmployee(@PathVariable Long id,
                                                       @RequestBody UpdateEmployeeRequest request) {
        EmployeeEntity employee = employeeService.getById(id);
        if (employee == null || employee.getIsDeleted()) {
            throw new com.house.keeping.service.common.BusinessException(ErrorCode.USER_NOT_EXIST);
        }

        UserEntity user = userService.getById(employee.getUserId());
        if (user == null) {
            throw new com.house.keeping.service.common.BusinessException(ErrorCode.USER_NOT_EXIST);
        }

        // 更新用户信息
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
        user.setUpdatedAt(new Date());
        userService.updateById(user);

        // 更新员工信息
        if (StringUtils.hasText(request.getWorkYears())) {
            employee.setWorkYears(request.getWorkYears());
        }
        if (StringUtils.hasText(request.getIdentity())) {
            employee.setIdentity(request.getIdentity());
        }
        if (request.getIsTeamLeader() != null) {
            employee.setIsTeamLeader(request.getIsTeamLeader());
        }
        if (StringUtils.hasText(request.getTeamSize())) {
            employee.setTeamSize(request.getTeamSize());
        }
        if (StringUtils.hasText(request.getResume())) {
            employee.setResume(request.getResume());
        }
        employee.setUpdatedAt(new Date());
        employeeService.updateById(employee);

        Map<String, Object> data = new HashMap<>();
        data.put("id", employee.getId());
        data.put("updateTime", new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        return Result.success(data);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除员工", description = "删除指定员工（软删除）")
    public Result<Map<String, Object>> deleteEmployee(@PathVariable Long id) {
        EmployeeEntity employee = employeeService.getById(id);
        if (employee == null || employee.getIsDeleted()) {
            throw new com.house.keeping.service.common.BusinessException(ErrorCode.USER_NOT_EXIST);
        }

        // 软删除员工信息
        employee.setIsDeleted(true);
        employee.setUpdatedAt(new Date());
        employeeService.updateById(employee);

        Map<String, Object> data = new HashMap<>();
        data.put("id", employee.getId());
        data.put("deleteTime", new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        return Result.success(data);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "切换员工状态", description = "启用/禁用员工")
    public Result<Map<String, Object>> updateEmployeeStatus(@PathVariable Long id,
                                                             @RequestBody Map<String, String> request) {
        EmployeeEntity employee = employeeService.getById(id);
        if (employee == null || employee.getIsDeleted()) {
            throw new com.house.keeping.service.common.BusinessException(ErrorCode.USER_NOT_EXIST);
        }

        UserEntity user = userService.getById(employee.getUserId());
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
        data.put("id", employee.getId());
        data.put("status", status);
        data.put("updateTime", new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        return Result.success(data);
    }

    private EmployeeInfoResponse buildEmployeeInfoResponse(EmployeeEntity employee) {
        EmployeeInfoResponse response = new EmployeeInfoResponse();
        response.setId(employee.getId());
        response.setUserId(employee.getUserId());
        response.setWorkYears(employee.getWorkYears());
        response.setIdentity(employee.getIdentity());
        response.setIsTeamLeader(employee.getIsTeamLeader());
        response.setTeamSize(employee.getTeamSize());
        response.setResume(employee.getResume());

        // 获取用户信息
        UserEntity user = userService.getById(employee.getUserId());
        if (user != null) {
            response.setUsername(user.getName());
            response.setNickname(user.getNickname());
            response.setRole(user.getRole());
            response.setEmail(user.getEmail());
            response.setPhone(user.getPhone());
            response.setAvatar(user.getImageUrl());
            response.setStatus(user.getStatus());
            response.setIsVerified(user.getIsVerified());

            // 脱敏身份证号
            if (user.getIdCard() != null && user.getIdCard().length() > 4) {
                String idCard = user.getIdCard();
                response.setIdCard(idCard.substring(0, 3) + "***********" + idCard.substring(idCard.length() - 4));
            }

            response.setAddress(user.getAddress());

            // 设置加入日期
            if (user.getCreatedAt() != null) {
                response.setJoinDate(new java.text.SimpleDateFormat("yyyy-MM-dd").format(user.getCreatedAt()));
            }
        }

        response.setCreatedAt(employee.getCreatedAt());
        response.setUpdatedAt(employee.getUpdatedAt());

        return response;
    }
}
