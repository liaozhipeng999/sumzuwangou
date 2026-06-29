package com.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.entity.ProductAttribute;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface ProductAttributeMapper extends BaseMapper<ProductAttribute> {

    @Select("SELECT attr_name, GROUP_CONCAT(DISTINCT attr_value ORDER BY sort) as attr_values FROM product_attributes WHERE product_id = #{productId} GROUP BY attr_name ORDER BY MIN(sort)")
    List<Map<String, String>> findAttributeGroups(Long productId);

    @Select("SELECT DISTINCT attr_name FROM product_attributes WHERE product_id = #{productId} ORDER BY MIN(sort)")
    List<String> findAttributeNames(Long productId);

    @Select("SELECT attr_value FROM product_attributes WHERE product_id = #{productId} AND attr_name = #{attrName} ORDER BY sort")
    List<String> findAttributeValues(Long productId, String attrName);

    @Select("SELECT * FROM product_attributes WHERE product_id = #{productId} ORDER BY sort")
    List<ProductAttribute> findByProductId(Long productId);
}