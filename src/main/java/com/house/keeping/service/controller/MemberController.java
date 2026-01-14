package com.house.keeping.service.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.house.keeping.service.entity.MemberEntity;
import com.house.keeping.service.entity.OrderEntity;
import com.house.keeping.service.service.MemberService;
import com.house.keeping.service.util.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 会员管理控制器
 */
@RestController
@RequestMapping("/member")
@Tag(name = "会员管理", description = "会员管理相关接口")
public class MemberController {
    @Autowired
    private MemberService memberService;

    /**
     * 获取会员列表
     */
    @GetMapping("/query")
    @Operation(summary = "获取会员列表", description = "分页获取会员列表")
    public IPage<MemberEntity> getAllMembers(@RequestParam(defaultValue = "1") Integer current,
                                             @RequestParam(defaultValue = "10") Integer size,
                                             @RequestBody MemberEntity memberEntity) {
        IPage<MemberEntity> page = new Page<>(current,size);
        LambdaQueryWrapper<MemberEntity> wrapper = new LambdaQueryWrapper<MemberEntity>();
        if(!ObjectUtils.isEmpty(memberEntity) && null != memberEntity.getUserId()){
            wrapper.eq(MemberEntity::getUserId,memberEntity.getUserId());
        }
        return memberService.page(page);
    }

    /**
     * 新增会员
     */
    @PostMapping("/add")
    @Operation(summary = "新增会员", description = "创建新的会员")
    public R addMember(@RequestBody MemberEntity member) {
        return new R(memberService.save(member));
    }

    /**
     * 删除会员
     */
    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除会员", description = "根据ID删除会员")
    public R deleteMember(@PathVariable Long id) {
        return new R(memberService.removeById(id));
    }

    /**
     * 修改会员
     */
    @PutMapping("/update/{id}")
    @Operation(summary = "修改会员", description = "更新会员信息")
    public R updateMember(@PathVariable Long id, @RequestBody MemberEntity member) {
        member.setId(id);
        return new R(memberService.updateById(member));
    }

    /**
     * 获取会员详情
     */
    @GetMapping("/info/{id}")
    @Operation(summary = "获取会员详情", description = "根据ID获取会员详情")
    public R getMember(@PathVariable Long id){
        return new R(memberService.getById(id));
    }
}