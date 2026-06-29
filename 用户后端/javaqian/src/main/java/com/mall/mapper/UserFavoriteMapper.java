package com.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.entity.UserFavorite;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserFavoriteMapper extends BaseMapper<UserFavorite> {

    @Select("SELECT * FROM user_favorites WHERE user_id = #{userId} AND product_id = #{productId} LIMIT 1")
    UserFavorite findByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);

    @Delete("DELETE FROM user_favorites WHERE user_id = #{userId} AND product_id = #{productId}")
    int deleteByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);

    @Select("SELECT * FROM user_favorites WHERE user_id = #{userId} ORDER BY created_at DESC")
    java.util.List<UserFavorite> findByUserId(@Param("userId") Long userId);

    @Select("SELECT COUNT(*) FROM user_favorites WHERE user_id = #{userId}")
    int countByUserId(@Param("userId") Long userId);

    @Select("SELECT p.id, p.product_name, p.price, p.original_price, p.main_image, p.sales, p.brief, f.created_at as favorite_time " +
            "FROM user_favorites f LEFT JOIN term_products p ON f.product_id = p.id " +
            "WHERE f.user_id = #{userId} AND p.deleted_at IS NULL " +
            "ORDER BY f.created_at DESC LIMIT #{offset}, #{pageSize}")
    java.util.List<java.util.Map<String, Object>> findFavoriteProducts(@Param("userId") Long userId, 
                                                                       @Param("offset") int offset, 
                                                                       @Param("pageSize") int pageSize);

    @Select("SELECT COUNT(*) FROM user_favorites f LEFT JOIN term_products p ON f.product_id = p.id " +
            "WHERE f.user_id = #{userId} AND p.deleted_at IS NULL")
    int countFavoriteProducts(@Param("userId") Long userId);

    @Select("SELECT p.id, p.product_name, p.price, p.original_price, p.main_image, p.sales, p.brief, f.created_at as favorite_time " +
            "FROM user_favorites f LEFT JOIN term_products p ON f.product_id = p.id " +
            "WHERE f.user_id = #{userId} AND p.deleted_at IS NULL " +
            "ORDER BY f.created_at DESC")
    java.util.List<java.util.Map<String, Object>> findAllFavoriteProducts(@Param("userId") Long userId);
}
