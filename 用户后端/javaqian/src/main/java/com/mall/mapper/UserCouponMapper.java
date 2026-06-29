package com.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.entity.UserCoupon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserCouponMapper extends BaseMapper<UserCoupon> {

    @Select("SELECT uc.* FROM user_coupons uc WHERE uc.user_id = #{userId} AND uc.status = 1")
    List<UserCoupon> findUnusedCouponsByUserId(@Param("userId") Long userId);

    @Select("SELECT uc.* FROM user_coupons uc WHERE uc.user_id = #{userId} AND uc.coupon_id = #{couponId} AND uc.status = 1")
    UserCoupon findUserCoupon(@Param("userId") Long userId, @Param("couponId") Long couponId);

    @Select("SELECT COUNT(*) FROM user_coupons uc WHERE uc.user_id = #{userId} AND uc.coupon_id = #{couponId}")
    int countUserCoupon(@Param("userId") Long userId, @Param("couponId") Long couponId);

    @Select("SELECT uc.* FROM user_coupons uc WHERE uc.user_id = #{userId}")
    List<UserCoupon> findByUserId(@Param("userId") Long userId);
}
