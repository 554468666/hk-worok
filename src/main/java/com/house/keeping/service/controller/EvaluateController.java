package com.house.keeping.service.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.house.keeping.service.entity.EvaluateEntity;
import com.house.keeping.service.entity.MemberEntity;
import com.house.keeping.service.service.EvaluateService;
import com.house.keeping.service.util.R;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/evaluate")
@Tag(name = "评价管理", description = "评价管理相关接口")
public class EvaluateController {
    @Autowired
    private EvaluateService evaluateService;

    @Tag(name = "获取评价列表")
    @GetMapping
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

    @Tag(name = "新增评价")
    @PostMapping
    public R addEvaluate(@RequestBody EvaluateEntity evaluate) {
        return new R(evaluateService.save(evaluate));
    }

    @Tag(name = "删除评价")
    @DeleteMapping("/{id}")
    public R deleteEvaluate(@PathVariable Long id) {
        return new R(evaluateService.removeById(id));
    }

    @Tag(name = "修改评价")
    @PutMapping("/{id}")
    public R updateEvaluate(@PathVariable Long id, @RequestBody EvaluateEntity
            evaluate) {
        evaluate.setId(id);
        return new R(evaluateService.updateById(evaluate));
    }
}