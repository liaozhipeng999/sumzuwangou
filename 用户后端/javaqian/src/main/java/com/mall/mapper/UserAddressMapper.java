package com.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.entity.UserAddress;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserAddressMapper extends BaseMapper<UserAddress> {

    @Select("SELECT * FROM user_addresses WHERE user_id = #{userId} AND status = 1 ORDER BY is_default DESC, created_at DESC")
    List<UserAddress> findByUserId(Long userId);

    @Select("SELECT * FROM user_addresses WHERE user_id = #{userId} AND is_default = 1 AND status = 1 LIMIT 1")
    UserAddress findDefaultAddress(Long userId);
}