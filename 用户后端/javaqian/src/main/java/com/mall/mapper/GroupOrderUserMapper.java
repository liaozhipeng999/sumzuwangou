package com.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.entity.GroupOrderUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface GroupOrderUserMapper extends BaseMapper<GroupOrderUser> {

    @Select("SELECT * FROM group_order_users WHERE group_order_id = #{groupOrderId} ORDER BY joined_at ASC")
    List<GroupOrderUser> findByGroupOrderId(@Param("groupOrderId") Long groupOrderId);
}
