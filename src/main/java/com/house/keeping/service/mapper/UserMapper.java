package com.house.keeping.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.house.keeping.service.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {
    UserEntity findByOpenId(@Param("openId") String openId);
}