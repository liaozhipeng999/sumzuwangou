package com.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.entity.GroupOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface GroupOrderMapper extends BaseMapper<GroupOrder> {

    @Select("SELECT * FROM group_orders WHERE product_id = #{productId} AND status = 1 LIMIT 1")
    GroupOrder findActiveByProductId(@Param("productId") Long productId);

    @Select("SELECT COUNT(*) FROM group_orders WHERE product_id = #{productId} AND status = 1")
    int countActiveByProductId(@Param("productId") Long productId);
}
