package com.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.entity.ProductImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProductImageMapper extends BaseMapper<ProductImage> {

    @Select("SELECT * FROM term_product_images WHERE product_id = #{productId} AND image_type = #{imageType} ORDER BY sort ASC")
    List<ProductImage> findByProductIdAndType(@Param("productId") Long productId, @Param("imageType") Integer imageType);

    @Select("SELECT * FROM term_product_images WHERE product_id = #{productId} ORDER BY sort ASC")
    List<ProductImage> findByProductId(@Param("productId") Long productId);
}
