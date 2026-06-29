package com.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.entity.Coupon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface CouponMapper extends BaseMapper<Coupon> {

    @Select("SELECT c.* FROM term_coupons c WHERE c.status = 1 AND c.start_time <= #{now} AND c.end_time >= #{now} AND c.received_count < c.total_count")
    List<Coupon> findAvailableCoupons(@Param("now") LocalDateTime now);

    @Select("SELECT c.* FROM term_coupons c WHERE c.status = 1 AND c.start_time <= #{now} AND c.end_time >= #{now} AND " +
            "(c.product_category_id = #{categoryId} OR c.product_category_id IS NULL) AND " +
            "(c.product_id = #{productId} OR c.product_id IS NULL) AND " +
            "(c.merchant_id = #{merchantId} OR c.merchant_id IS NULL)")
    List<Coupon> findApplicableCoupons(@Param("categoryId") Long categoryId, 
                                       @Param("productId") Long productId, 
                                       @Param("merchantId") Long merchantId,
                                       @Param("now") LocalDateTime now);
}
