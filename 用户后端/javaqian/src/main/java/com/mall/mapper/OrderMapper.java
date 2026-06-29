package com.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    @Select("SELECT * FROM orders WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<Order> findByUserId(Long userId);

    @Select("SELECT * FROM orders WHERE order_no = #{orderNo} LIMIT 1")
    Order findByOrderNo(String orderNo);
}