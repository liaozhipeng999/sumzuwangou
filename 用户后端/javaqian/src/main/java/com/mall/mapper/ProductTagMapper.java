package com.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.entity.ProductTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProductTagMapper extends BaseMapper<ProductTag> {

    @Select("SELECT * FROM term_product_tags WHERE product_id = #{productId}")
    List<ProductTag> findByProductId(@Param("productId") Long productId);
}
