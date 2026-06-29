package com.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.entity.ProductAttributeDimension;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProductAttributeDimensionMapper extends BaseMapper<ProductAttributeDimension> {

    @Select("SELECT * FROM product_attribute_dimensions WHERE product_type = #{productType} AND status = 1 ORDER BY sort_order ASC")
    List<ProductAttributeDimension> findByProductType(String productType);

    @Select("SELECT DISTINCT product_type FROM product_attribute_dimensions WHERE status = 1")
    List<String> findAllProductTypes();
}