package com.mall.service;

import java.util.List;
import java.util.Map;

public interface ProductAttributeService {

    Map<String, Object> getProductAttributeOptions(Long productId);

    Map<String, Object> getAttributeOptionsByType(String productType);

    Map<String, Object> calculateDiscount(Long productId, Long skuId, Long userId);

    List<String> getAllProductTypes();
}