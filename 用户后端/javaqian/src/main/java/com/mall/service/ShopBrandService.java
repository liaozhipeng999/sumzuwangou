package com.mall.service;

import com.mall.dto.BrandInfoDTO;
import com.mall.dto.ShopInfoDTO;

public interface ShopBrandService {

    /**
     * 获取店铺信息
     */
    ShopInfoDTO getShopInfo(Long shopId);

    /**
     * 获取品牌信息（含富文本）
     */
    BrandInfoDTO getBrandInfo(Long brandId);
}
