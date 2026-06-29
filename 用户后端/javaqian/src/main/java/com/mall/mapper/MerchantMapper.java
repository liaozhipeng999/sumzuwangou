package com.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.entity.Merchant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface MerchantMapper extends BaseMapper<Merchant> {

    @Select("SELECT id, merchant_name, merchant_logo, merchant_brief, contact_name, contact_phone, merchant_level, status, created_at FROM term_merchants WHERE id = #{id} AND deleted_at IS NULL")
    Map<String, Object> getMerchantDetail(@Param("id") Long id);

    @Select("SELECT id, merchant_name, merchant_logo, merchant_level FROM term_merchants WHERE deleted_at IS NULL ORDER BY merchant_level DESC, id DESC LIMIT #{limit}")
    List<Map<String, Object>> getMerchantList(@Param("limit") int limit);

    @Select("SELECT p.id, p.product_name, p.price, p.original_price, p.main_image, p.sales, p.brief, p.is_hot, p.is_new FROM term_products p WHERE p.merchant_id = #{merchantId} AND p.deleted_at IS NULL ORDER BY p.sales DESC LIMIT #{limit}")
    List<Map<String, Object>> getMerchantProducts(@Param("merchantId") Long merchantId, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM term_products WHERE merchant_id = #{merchantId} AND deleted_at IS NULL")
    int getMerchantProductCount(@Param("merchantId") Long merchantId);
}