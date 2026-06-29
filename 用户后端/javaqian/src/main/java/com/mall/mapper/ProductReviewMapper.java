package com.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.entity.ProductReview;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProductReviewMapper extends BaseMapper<ProductReview> {

    @Select("SELECT * FROM term_product_reviews WHERE product_id = #{productId} ORDER BY helpful_count DESC")
    List<ProductReview> findByProductId(@Param("productId") Long productId);

    @Select("SELECT r.* FROM term_product_reviews r JOIN term_products p ON r.product_id = p.id WHERE p.merchant_id = #{merchantId} ORDER BY r.helpful_count DESC")
    List<ProductReview> findByMerchantId(@Param("merchantId") Long merchantId);
}
