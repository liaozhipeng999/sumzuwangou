package com.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.entity.Payment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PaymentMapper extends BaseMapper<Payment> {

    @Select("SELECT * FROM payments WHERE order_id = #{orderId} ORDER BY created_at DESC LIMIT 1")
    Payment findByOrderId(Long orderId);

    @Select("SELECT * FROM payments WHERE order_no = #{orderNo} ORDER BY created_at DESC LIMIT 1")
    Payment findByOrderNo(String orderNo);

    @Select("SELECT * FROM payments WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<Payment> findByUserId(Long userId);
}