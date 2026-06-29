package com.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.entity.ProductSku;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProductSkuMapper extends BaseMapper<ProductSku> {

    @Select("SELECT * FROM product_skus WHERE product_id = #{productId} AND status = 1 ORDER BY created_at")
    List<ProductSku> findByProductId(Long productId);

    @Select("SELECT * FROM product_skus WHERE sku_code = #{skuCode} AND status = 1 LIMIT 1")
    ProductSku findBySkuCode(String skuCode);
}