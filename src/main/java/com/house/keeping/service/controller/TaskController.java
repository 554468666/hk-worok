package com.house.keeping.service.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.house.keeping.service.common.ErrorCode;
import com.house.keeping.service.common.Result;
import com.house.keeping.service.entity.TaskEntity;
import com.house.keeping.service.entity.UserEntity;
import com.house.keeping.service.entity.request.*;
import com.house.keeping.service.entity.response.TaskResponse;
import com.house.keeping.service.entity.response.UserInfoResponse;
import com.house.keeping.service.service.TaskExpirationService;
import com.house.keeping.service.service.TaskService;
import com.house.keeping.service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 任务管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/tasks")
@Tag(name = "任务管理", description = "任务管理相关接口")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    @Autowired
    private TaskExpirationService taskExpirationService;
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    
    /**
     * 获取任务列表
     */
    @GetMapping
    @Operation(summary = "获取任务列表", description = "分页获取任务列表，支持多种筛选和搜索条件")
    public Result<Map<String, Object>> getTasks(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) Long assigneeId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder) {
        
        Page<TaskEntity> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<TaskEntity> wrapper = new LambdaQueryWrapper<>();
        
        // 搜索关键词
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(TaskEntity::getTitle, keyword)
                    .or().like(TaskEntity::getDescription, keyword));
        }
        
        // 类型筛选
        if (StringUtils.hasText(type)) {
            wrapper.eq(TaskEntity::getType, type);
        }
        
        // 状态筛选
        if (StringUtils.hasText(status)) {
            wrapper.eq(TaskEntity::getStatus, status);
        }
        
        // 优先级筛选
        if (StringUtils.hasText(priority)) {
            wrapper.eq(TaskEntity::getPriority, priority);
        }
        
        // 指派人筛选
        if (assigneeId != null) {
            wrapper.eq(TaskEntity::getAssigneeId, assigneeId);
        }
        
        // 日期范围筛选
        if (StringUtils.hasText(startDate)) {
            try {
                wrapper.ge(TaskEntity::getDueDate, DATE_FORMAT.parse(startDate));
            } catch (Exception e) {
                // 忽略解析错误
            }
        }
        if (StringUtils.hasText(endDate)) {
            try {
                wrapper.le(TaskEntity::getDueDate, DATE_FORMAT.parse(endDate));
            } catch (Exception e) {
                // 忽略解析错误
            }
        }
        
        // 排序
        if ("asc".equals(sortOrder)) {
            if ("id".equals(sortBy)) {
                wrapper.orderByAsc(TaskEntity::getId);
            } else if ("title".equals(sortBy)) {
                wrapper.orderByAsc(TaskEntity::getTitle);
            } else if ("priority".equals(sortBy)) {
                wrapper.orderByAsc(TaskEntity::getPriority);
            } else if ("status".equals(sortBy)) {
                wrapper.orderByAsc(TaskEntity::getStatus);
            } else if ("dueDate".equals(sortBy)) {
                wrapper.orderByAsc(TaskEntity::getDueDate);
            } else {
                wrapper.orderByAsc(TaskEntity::getCreatedAt);
            }
        } else {
            if ("id".equals(sortBy)) {
                wrapper.orderByDesc(TaskEntity::getId);
            } else if ("title".equals(sortBy)) {
                wrapper.orderByDesc(TaskEntity::getTitle);
            } else if ("priority".equals(sortBy)) {
                wrapper.orderByDesc(TaskEntity::getPriority);
            } else if ("status".equals(sortBy)) {
                wrapper.orderByDesc(TaskEntity::getStatus);
            } else if ("dueDate".equals(sortBy)) {
                wrapper.orderByDesc(TaskEntity::getDueDate);
            } else {
                wrapper.orderByDesc(TaskEntity::getCreatedAt);
            }
        }
        
        // 软删除过滤
        wrapper.eq(TaskEntity::getIsDeleted, false);
        
        IPage<TaskEntity> taskPage = taskService.page(pageParam, wrapper);

        System.out.println("=== Task Query Debug ===");
        System.out.println("Total records: " + taskPage.getTotal());
        System.out.println("Records size: " + taskPage.getRecords().size());
        System.out.println("Records: " + taskPage.getRecords());

        List<TaskResponse> taskList = new ArrayList<>();
        for (TaskEntity task : taskPage.getRecords()) {
            System.out.println("Processing task: " + task.getId() + " - " + task.getTitle());
            taskList.add(buildTaskResponse(task));
        }
        
        Map<String, Object> data = new HashMap<>();

        data.put("list", taskList);
        data.put("pagination", Map.of(
            "page", taskPage.getCurrent(),
            "pageSize", taskPage.getSize(),
            "total", taskPage.getTotal(),
            "totalPages", taskPage.getPages(),
            "hasMore", taskPage.getCurrent() < taskPage.getPages()
        ));
        
        return Result.success(data);
    }
    
    /**
     * 获取任务详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取任务详情", description = "获取指定任务的详细信息")
    public Result<TaskResponse> getTaskById(@PathVariable Long id) {
        TaskEntity task = taskService.getById(id);
        if (task == null || task.getIsDeleted()) {
            throw new com.house.keeping.service.common.BusinessException(ErrorCode.TASK_NOT_EXIST);
        }
        
        return Result.success(buildTaskResponse(task));
    }
    
    /**
     * 创建任务
     */
    @PostMapping
    @Operation(summary = "创建任务", description = "创建新的任务，支持设置多种属性和指派")
    public Result<Map<String, Object>> createTask(@Valid @RequestBody CreateTaskRequest request,
                                                   @RequestHeader("Authorization") String token) {
        Long userId = getUserIdFromToken(token);
        
        // 验证指派人是否存在
        if (request.getAssigneeId() != null) {
            UserEntity assignee = userService.getById(request.getAssigneeId());
            if (assignee == null) {
                throw new com.house.keeping.service.common.BusinessException(ErrorCode.USER_NOT_EXIST);
            }
        }
        
        TaskEntity task = new TaskEntity();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setType(request.getType() != null ? request.getType() : "daily_task");
        task.setPriority(request.getPriority() != null ? request.getPriority() : "medium");
        task.setAssigneeId(request.getAssigneeId());
        task.setCreatorId(userId);
        task.setCategory(request.getCategory());
        task.setStatus("pending");
        
        // 处理截止日期
        if (StringUtils.hasText(request.getDueDate())) {
            try {
                Date dueDate = DATE_FORMAT.parse(request.getDueDate());
                task.setDueDate(dueDate);

                // 计算从现在到截止时间的延迟（毫秒）
                long delay = dueDate.getTime() - System.currentTimeMillis();
                if (delay > 0) {
                    // 设置任务到期自动处理
                    taskExpirationService.setTaskExpiration(task.getId(), delay, TimeUnit.MILLISECONDS);
                } else {
                    log.warn("任务截止时间已过期，直接标记为过期: taskId={}, dueDate={}", task.getId(), request.getDueDate());
                    task.setStatus("overdue");
                }
            } catch (Exception e) {
                // 忽略解析错误
            }
        }
        
        // 处理标签
        if (request.getTags() != null && !request.getTags().isEmpty()) {
            try {
                task.setTags(OBJECT_MAPPER.writeValueAsString(request.getTags()));
            } catch (Exception e) {
                // 忽略序列化错误
            }
        }
        
        Date now = new Date();
        task.setCreatedAt(now);
        task.setUpdatedAt(now);
        task.setIsDeleted(false);
        
        taskService.save(task);
        
        Map<String, Object> data = new HashMap<>();
        data.put("id", task.getId());
        data.put("title", task.getTitle());
        data.put("status", task.getStatus());
        data.put("createdAt", DATETIME_FORMAT.format(task.getCreatedAt()));
        
        // TODO: 发送通知给指派人
        
        return Result.success(data);
    }
    
    /**
     * 更新任务
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新任务", description = "更新任务信息")
    public Result<Map<String, Object>> updateTask(@PathVariable Long id,
                                                    @Valid @RequestBody UpdateTaskRequest request) {
        TaskEntity task = taskService.getById(id);
        if (task == null || task.getIsDeleted()) {
            throw new com.house.keeping.service.common.BusinessException(ErrorCode.TASK_NOT_EXIST);
        }
        
        if (StringUtils.hasText(request.getTitle())) {
            task.setTitle(request.getTitle());
        }
        if (StringUtils.hasText(request.getDescription())) {
            task.setDescription(request.getDescription());
        }
        if (StringUtils.hasText(request.getType())) {
            task.setType(request.getType());
        }
        if (StringUtils.hasText(request.getPriority())) {
            task.setPriority(request.getPriority());
        }
        if (request.getAssigneeId() != null) {
            UserEntity assignee = userService.getById(request.getAssigneeId());
            if (assignee == null) {
                throw new com.house.keeping.service.common.BusinessException(ErrorCode.USER_NOT_EXIST);
            }
            task.setAssigneeId(request.getAssigneeId());
        }
        if (StringUtils.hasText(request.getStatus())) {
            task.setStatus(request.getStatus());
        }
        if (StringUtils.hasText(request.getDueDate())) {
            try {
                Date dueDate = DATE_FORMAT.parse(request.getDueDate());
                task.setDueDate(dueDate);

                // 取消旧的过期设置，重新设置
                taskExpirationService.cancelTaskExpiration(id);

                // 重新计算延迟时间
                long delay = dueDate.getTime() - System.currentTimeMillis();
                if (delay > 0) {
                    taskExpirationService.setTaskExpiration(id, delay, TimeUnit.MILLISECONDS);
                } else if (!"overdue".equals(task.getStatus())) {
                    log.warn("任务截止时间已过期，标记为过期: taskId={}, dueDate={}", id, request.getDueDate());
                    task.setStatus("overdue");
                }
            } catch (Exception e) {
                // 忽略解析错误
            }
        }
        if (StringUtils.hasText(request.getCategory())) {
            task.setCategory(request.getCategory());
        }
        if (request.getTags() != null) {
            try {
                task.setTags(OBJECT_MAPPER.writeValueAsString(request.getTags()));
            } catch (Exception e) {
                // 忽略序列化错误
            }
        }
        
        task.setUpdatedAt(new Date());
        taskService.updateById(task);
        
        Map<String, Object> data = new HashMap<>();
        data.put("id", task.getId());
        data.put("updateTime", DATETIME_FORMAT.format(task.getUpdatedAt()));
        
        return Result.success(data);
    }
    
    /**
     * 删除任务（软删除）
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除任务", description = "删除指定任务（软删除）")
    public Result<Map<String, Object>> deleteTask(@PathVariable Long id) {
        TaskEntity task = taskService.getById(id);
        if (task == null || task.getIsDeleted()) {
            throw new com.house.keeping.service.common.BusinessException(ErrorCode.TASK_NOT_EXIST);
        }

        task.setIsDeleted(true);
        task.setUpdatedAt(new Date());
        taskService.updateById(task);

        // 取消任务过期处理
        taskExpirationService.cancelTaskExpiration(id);

        Map<String, Object> data = new HashMap<>();
        data.put("id", task.getId());
        data.put("deleteTime", DATETIME_FORMAT.format(new Date()));

        return Result.success(data);
    }
    
    /**
     * 更新任务状态
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "更新任务状态", description = "更新任务状态")
    public Result<Map<String, Object>> updateTaskStatus(@PathVariable Long id,
                                                         @RequestBody Map<String, String> request) {
        TaskEntity task = taskService.getById(id);
        if (task == null || task.getIsDeleted()) {
            throw new com.house.keeping.service.common.BusinessException(ErrorCode.TASK_NOT_EXIST);
        }
        
        String status = request.get("status");
        if (!"pending".equals(status) && !"processing".equals(status) &&
            !"completed".equals(status) && !"cancelled".equals(status) && !"overdue".equals(status)) {
            throw new com.house.keeping.service.common.BusinessException(ErrorCode.BAD_REQUEST, "状态值不正确");
        }
        
        // 检查状态是否允许修改
        if ("completed".equals(task.getStatus()) || "cancelled".equals(task.getStatus())) {
            throw new com.house.keeping.service.common.BusinessException(ErrorCode.TASK_STATUS_NOT_ALLOW);
        }
        
        task.setStatus(status);
        
        // 如果状态为已完成，设置完成时间
        if ("completed".equals(status)) {
            task.setCompletedAt(new Date());
        }
        
        task.setUpdatedAt(new Date());
        taskService.updateById(task);
        
        Map<String, Object> data = new HashMap<>();
        data.put("id", task.getId());
        data.put("status", status);
        data.put("updateTime", DATETIME_FORMAT.format(task.getUpdatedAt()));
        
        return Result.success(data);
    }
    
    /**
     * 获取任务统计
     */
    @GetMapping("/stats")
    @Operation(summary = "获取任务统计", description = "获取任务相关的统计数据和趋势信息")
    public Result<Map<String, Object>> getTaskStats(
            @RequestParam(defaultValue = "week") String period,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        // 构建查询条件 - 只统计未删除的任务
        LambdaQueryWrapper<TaskEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TaskEntity::getIsDeleted, false);

        // 处理日期范围
        Date queryStartDate = null;
        Date queryEndDate = null;

        if (StringUtils.hasText(startDate)) {
            try {
                queryStartDate = DATE_FORMAT.parse(startDate);
                wrapper.ge(TaskEntity::getCreatedAt, queryStartDate);
            } catch (Exception e) {
                // 忽略解析错误
            }
        }
        if (StringUtils.hasText(endDate)) {
            try {
                queryEndDate = DATE_FORMAT.parse(endDate);
                wrapper.le(TaskEntity::getCreatedAt, queryEndDate);
            } catch (Exception e) {
                // 忽略解析错误
            }
        }

        // 如果没有指定日期范围，根据 period 参数设置
        if (queryStartDate == null && queryEndDate == null) {
            Date now = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(now);

            if ("week".equals(period)) {
                cal.add(Calendar.DAY_OF_MONTH, -7);
                queryStartDate = cal.getTime();
            } else if ("month".equals(period)) {
                cal.add(Calendar.MONTH, -1);
                queryStartDate = cal.getTime();
            } else if ("quarter".equals(period)) {
                cal.add(Calendar.MONTH, -3);
                queryStartDate = cal.getTime();
            } else if ("year".equals(period)) {
                cal.add(Calendar.YEAR, -1);
                queryStartDate = cal.getTime();
            }

            if (queryStartDate != null) {
                wrapper.ge(TaskEntity::getCreatedAt, queryStartDate);
            }
        }

        // 统计各状态任务数量
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", taskService.count(wrapper));

        // 统计各状态数量
        LambdaQueryWrapper<TaskEntity> pendingWrapper = new LambdaQueryWrapper<>();
        pendingWrapper.eq(TaskEntity::getIsDeleted, false);
        if (queryStartDate != null) {
            pendingWrapper.ge(TaskEntity::getCreatedAt, queryStartDate);
        }
        if (queryEndDate != null) {
            pendingWrapper.le(TaskEntity::getCreatedAt, queryEndDate);
        }
        pendingWrapper.eq(TaskEntity::getStatus, "pending");
        stats.put("pending", taskService.count(pendingWrapper));

        LambdaQueryWrapper<TaskEntity> processingWrapper = new LambdaQueryWrapper<>();
        processingWrapper.eq(TaskEntity::getIsDeleted, false);
        if (queryStartDate != null) {
            processingWrapper.ge(TaskEntity::getCreatedAt, queryStartDate);
        }
        if (queryEndDate != null) {
            processingWrapper.le(TaskEntity::getCreatedAt, queryEndDate);
        }
        processingWrapper.eq(TaskEntity::getStatus, "processing");
        stats.put("processing", taskService.count(processingWrapper));

        LambdaQueryWrapper<TaskEntity> completedWrapper = new LambdaQueryWrapper<>();
        completedWrapper.eq(TaskEntity::getIsDeleted, false);
        if (queryStartDate != null) {
            completedWrapper.ge(TaskEntity::getCreatedAt, queryStartDate);
        }
        if (queryEndDate != null) {
            completedWrapper.le(TaskEntity::getCreatedAt, queryEndDate);
        }
        completedWrapper.eq(TaskEntity::getStatus, "completed");
        stats.put("completed", taskService.count(completedWrapper));

        LambdaQueryWrapper<TaskEntity> cancelledWrapper = new LambdaQueryWrapper<>();
        cancelledWrapper.eq(TaskEntity::getIsDeleted, false);
        if (queryStartDate != null) {
            cancelledWrapper.ge(TaskEntity::getCreatedAt, queryStartDate);
        }
        if (queryEndDate != null) {
            cancelledWrapper.le(TaskEntity::getCreatedAt, queryEndDate);
        }
        cancelledWrapper.eq(TaskEntity::getStatus, "cancelled");
        stats.put("cancelled", taskService.count(cancelledWrapper));

        // 计算完成率
        Long total = (Long) stats.get("total");
        Long completed = (Long) stats.get("completed");
        double completionRate = total > 0 ? (completed * 100.0 / total) : 0.0;
        stats.put("completionRate", Math.round(completionRate * 10.0) / 10.0);

        // 统计各优先级任务数量
        Map<String, Long> priorityStats = new HashMap<>();

        LambdaQueryWrapper<TaskEntity> urgentWrapper = new LambdaQueryWrapper<>();
        urgentWrapper.eq(TaskEntity::getIsDeleted, false);
        if (queryStartDate != null) {
            urgentWrapper.ge(TaskEntity::getCreatedAt, queryStartDate);
        }
        if (queryEndDate != null) {
            urgentWrapper.le(TaskEntity::getCreatedAt, queryEndDate);
        }
        urgentWrapper.eq(TaskEntity::getPriority, "urgent");
        priorityStats.put("urgent", taskService.count(urgentWrapper));

        LambdaQueryWrapper<TaskEntity> highWrapper = new LambdaQueryWrapper<>();
        highWrapper.eq(TaskEntity::getIsDeleted, false);
        if (queryStartDate != null) {
            highWrapper.ge(TaskEntity::getCreatedAt, queryStartDate);
        }
        if (queryEndDate != null) {
            highWrapper.le(TaskEntity::getCreatedAt, queryEndDate);
        }
        highWrapper.eq(TaskEntity::getPriority, "high");
        priorityStats.put("high", taskService.count(highWrapper));

        LambdaQueryWrapper<TaskEntity> mediumWrapper = new LambdaQueryWrapper<>();
        mediumWrapper.eq(TaskEntity::getIsDeleted, false);
        if (queryStartDate != null) {
            mediumWrapper.ge(TaskEntity::getCreatedAt, queryStartDate);
        }
        if (queryEndDate != null) {
            mediumWrapper.le(TaskEntity::getCreatedAt, queryEndDate);
        }
        mediumWrapper.eq(TaskEntity::getPriority, "medium");
        priorityStats.put("medium", taskService.count(mediumWrapper));

        LambdaQueryWrapper<TaskEntity> lowWrapper = new LambdaQueryWrapper<>();
        lowWrapper.eq(TaskEntity::getIsDeleted, false);
        if (queryStartDate != null) {
            lowWrapper.ge(TaskEntity::getCreatedAt, queryStartDate);
        }
        if (queryEndDate != null) {
            lowWrapper.le(TaskEntity::getCreatedAt, queryEndDate);
        }
        lowWrapper.eq(TaskEntity::getPriority, "low");
        priorityStats.put("low", taskService.count(lowWrapper));

        stats.put("priority", priorityStats);

        // 获取趋势数据
        Map<String, Object> trendData = new HashMap<>();
        List<Map<String, Object>> trendList = new ArrayList<>();

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());

        int days = "week".equals(period) ? 7 : "month".equals(period) ? 30 : 7;
        for (int i = days - 1; i >= 0; i--) {
            Calendar dayCal = (Calendar) cal.clone();
            dayCal.add(Calendar.DAY_OF_MONTH, -i);

            Date dayStart = dayCal.getTime();
            dayCal.set(Calendar.HOUR_OF_DAY, 23);
            dayCal.set(Calendar.MINUTE, 59);
            dayCal.set(Calendar.SECOND, 59);
            Date dayEnd = dayCal.getTime();

            // 统计当天创建和完成的任务数
            LambdaQueryWrapper<TaskEntity> dayWrapper = new LambdaQueryWrapper<>();
            dayWrapper.eq(TaskEntity::getIsDeleted, false);
            dayWrapper.ge(TaskEntity::getCreatedAt, dayStart);
            dayWrapper.le(TaskEntity::getCreatedAt, dayEnd);
            long dayCount = taskService.count(dayWrapper);

            LambdaQueryWrapper<TaskEntity> completedDayWrapper = new LambdaQueryWrapper<>();
            completedDayWrapper.eq(TaskEntity::getIsDeleted, false);
            completedDayWrapper.eq(TaskEntity::getStatus, "completed");
            completedDayWrapper.ge(TaskEntity::getCompletedAt, dayStart);
            completedDayWrapper.le(TaskEntity::getCompletedAt, dayEnd);
            long dayCompleted = taskService.count(completedDayWrapper);

            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", DATE_FORMAT.format(dayStart));
            dayData.put("count", dayCount);
            dayData.put("completed", dayCompleted);
            trendList.add(dayData);
        }

        trendData.put(period, trendList);

        // 构建最终返回数据
        Map<String, Object> data = new HashMap<>();
        data.putAll(stats);
        data.put("trend", trendData);
        data.put("period", period);

        return Result.success(data);
    }
    
    /**
     * 构建任务响应
     */
    private TaskResponse buildTaskResponse(TaskEntity task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setType(task.getType());
        response.setStatus(task.getStatus());
        response.setPriority(task.getPriority());
        response.setCategory(task.getCategory());
        
        // 处理标签
        if (StringUtils.hasText(task.getTags())) {
            try {
                List<String> tags = OBJECT_MAPPER.readValue(task.getTags(), new TypeReference<List<String>>() {});
                response.setTags(tags);
            } catch (Exception e) {
                response.setTags(new ArrayList<>());
            }
        } else {
            response.setTags(new ArrayList<>());
        }
        
        // 处理日期
        if (task.getDueDate() != null) {
            response.setDueDate(DATE_FORMAT.format(task.getDueDate()));
        }
        if (task.getStartDate() != null) {
            response.setStartDate(DATE_FORMAT.format(task.getStartDate()));
        }
        if (task.getCompletedAt() != null) {
            response.setCompletedAt(DATETIME_FORMAT.format(task.getCompletedAt()));
        }
        if (task.getCreatedAt() != null) {
            response.setCreatedAt(DATETIME_FORMAT.format(task.getCreatedAt()));
        }
        if (task.getUpdatedAt() != null) {
            response.setUpdatedAt(DATETIME_FORMAT.format(task.getUpdatedAt()));
        }
        
        // 设置指派人信息
        if (task.getAssigneeId() != null) {
            UserEntity assignee = userService.getById(task.getAssigneeId());
            if (assignee != null) {
                response.setAssignee(buildUserInfoResponse(assignee));
            }
        }
        
        // 设置创建人信息
        if (task.getCreatorId() != null) {
            UserEntity creator = userService.getById(task.getCreatorId());
            if (creator != null) {
                response.setCreator(buildUserInfoResponse(creator));
            }
        }
        
        response.setComments(new ArrayList<>());
        response.setAttachments(new ArrayList<>());
        
        return response;
    }
    
    /**
     * 构建用户信息响应
     */
    private UserInfoResponse buildUserInfoResponse(UserEntity user) {
        UserInfoResponse response = new UserInfoResponse();
        response.setId(user.getId());
        response.setUsername(user.getName());
        response.setNickname(user.getNickname());
        response.setRole(user.getRole());
        response.setAvatar(user.getImageUrl());
        return response;
    }
    
    /**
     * 从Token获取用户ID（简化版）
     */
    private Long getUserIdFromToken(String token) {
        // TODO: 实现Token解析
        return 1L;
    }
}
