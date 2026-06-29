package com.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.entity.MerchantBrandInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MerchantBrandInfoMapper extends BaseMapper<MerchantBrandInfo> {

    @Select("SELECT * FROM merchant_brand_info WHERE merchant_id = #{merchantId} AND status = 1 LIMIT 1")
    MerchantBrandInfo findByMerchantId(@Param("merchantId") Long merchantId);
}
