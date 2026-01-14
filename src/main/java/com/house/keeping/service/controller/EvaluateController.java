package com.house.keeping.service.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.house.keeping.service.entity.EvaluateEntity;
import com.house.keeping.service.entity.MemberEntity;
import com.house.keeping.service.service.EvaluateService;
import com.house.keeping.service.util.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 评价管理控制器
 */
@RestController
@RequestMapping("/evaluate")
@Tag(name = "评价管理", description = "评价管理相关接口")
public class EvaluateController {
    @Autowired
    private EvaluateService evaluateService;

    /**
     * 获取评价列表
     */
    @GetMapping("/query")
    @Operation(summary = "获取评价列表", description = "分页获取评价列表")
    public IPage<EvaluateEntity> getAllEvaluates(@RequestParam(defaultValue = "1") Integer current,
                                                 @RequestParam(defaultValue = "10") Integer size,
                                                 @RequestBody EvaluateEntity evaluateEntity) {

        IPage<EvaluateEntity> page = new Page<>(current,size);
        LambdaQueryWrapper<EvaluateEntity> wrapper = new LambdaQueryWrapper<EvaluateEntity>();
        if(!ObjectUtils.isEmpty(evaluateEntity) && null != evaluateEntity.getOrderId()){
            wrapper.eq(EvaluateEntity::getOrderId,evaluateEntity.getOrderId());
        }
        return evaluateService.page(page,wrapper);
    }

    /**
     * 新增评价
     */
    @PostMapping("/add")
    @Operation(summary = "新增评价", description = "创建新的评价")
    public R addEvaluate(@RequestBody EvaluateEntity evaluate) {
        return new R(evaluateService.save(evaluate));
    }

    /**
     * 删除评价
     */
    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除评价", description = "根据ID删除评价")
    public R deleteEvaluate(@PathVariable Long id) {
        return new R(evaluateService.removeById(id));
    }

    /**
     * 修改评价
     */
    @PutMapping("/update/{id}")
    @Operation(summary = "修改评价", description = "更新评价信息")
    public R updateEvaluate(@PathVariable Long id, @RequestBody EvaluateEntity evaluate) {
        evaluate.setId(id);
        return new R(evaluateService.updateById(evaluate));
    }

    /**
     * 获取评价详情
     */
    @GetMapping("/info/{id}")
    @Operation(summary = "获取评价详情", description = "获取指定评价的详细信息")
    public com.house.keeping.service.common.Result<EvaluateEntity> getEvaluateById(@PathVariable Long id) {
        EvaluateEntity evaluate = evaluateService.getById(id);
        if (evaluate == null) {
            throw new com.house.keeping.service.common.BusinessException(
                com.house.keeping.service.common.ErrorCode.BAD_REQUEST, "评价不存在");
        }
        return com.house.keeping.service.common.Result.success(evaluate);
    }
}