package com.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.entity.Cart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface CartMapper extends BaseMapper<Cart> {

    @Select("SELECT * FROM cart WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<Cart> findByUserId(@Param("userId") Long userId);

    @Select("SELECT * FROM cart WHERE user_id = #{userId} AND product_id = #{productId} LIMIT 1")
    Cart findByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);

    @Select("SELECT COUNT(*) FROM cart WHERE user_id = #{userId}")
    int countByUserId(@Param("userId") Long userId);

    @Update("UPDATE cart SET selected = #{selected} WHERE user_id = #{userId}")
    int updateAllSelectedByUserId(@Param("userId") Long userId, @Param("selected") Integer selected);
}
