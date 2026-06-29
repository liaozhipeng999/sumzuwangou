package com.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.entity.ProductDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface ProductDetailMapper extends BaseMapper<ProductDetail> {

    @Select("SELECT image_url FROM term_product_images WHERE product_id = #{productId} AND image_type = 0 ORDER BY sort")
    List<String> getProductCarouselImages(@Param("productId") Long productId);

    @Select("SELECT image_url FROM term_product_images WHERE product_id = #{productId} AND image_type = 1 ORDER BY sort")
    List<String> getProductDetailImages(@Param("productId") Long productId);

    @Select("SELECT * FROM term_product_reviews WHERE product_id = #{productId} ORDER BY created_at DESC LIMIT #{limit} OFFSET #{offset}")
    List<Map<String, Object>> getProductReviews(@Param("productId") Long productId, @Param("limit") int limit, @Param("offset") int offset);

    @Select("SELECT COUNT(*) FROM term_product_reviews WHERE product_id = #{productId}")
    int getReviewCount(@Param("productId") Long productId);

    @Select("SELECT AVG(rating) FROM term_product_reviews WHERE product_id = #{productId}")
    Double getAverageRating(@Param("productId") Long productId);

    @Select("SELECT * FROM term_product_details WHERE product_id = #{productId} ORDER BY sort")
    List<ProductDetail> findByProductId(@Param("productId") Long productId);

    @Select("SELECT * FROM term_merchants WHERE id = #{merchantId}")
    Map<String, Object> getMerchantInfo(@Param("merchantId") Long merchantId);

    @Select("SELECT COUNT(*) FROM term_product_reviews WHERE product_id = #{productId} AND rating = #{rating}")
    int getReviewCountByRating(@Param("productId") Long productId, @Param("rating") int rating);

    @Select("SELECT tag_name, tag_color FROM term_product_tags WHERE product_id = #{productId}")
    List<Map<String, Object>> getProductTags(@Param("productId") Long productId);
}
