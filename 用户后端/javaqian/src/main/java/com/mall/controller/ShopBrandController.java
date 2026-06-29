package com.mall.controller;

import com.mall.dto.BrandInfoDTO;
import com.mall.dto.ShopInfoDTO;
import com.mall.service.ShopBrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ShopBrandController {

    private final ShopBrandService shopBrandService;

    /**
     * 获取店铺信息
     * GET /api/shop/{shopId}
     */
    @GetMapping("/shop/{shopId}")
    public ResponseEntity<Map<String, Object>> getShopInfo(@PathVariable Long shopId) {
        Map<String, Object> response = new HashMap<>();
        
        ShopInfoDTO shopInfo = shopBrandService.getShopInfo(shopId);
        if (shopInfo == null) {
            response.put("code", 404);
            response.put("message", "店铺不存在");
            return ResponseEntity.ok(response);
        }

        response.put("code", 200);
        response.put("message", "success");
        response.put("data", shopInfo);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取品牌信息（含富文本）
     * GET /api/brand/{brandId}
     */
    @GetMapping("/brand/{brandId}")
    public ResponseEntity<Map<String, Object>> getBrandInfo(@PathVariable Long brandId) {
        Map<String, Object> response = new HashMap<>();
        
        BrandInfoDTO brandInfo = shopBrandService.getBrandInfo(brandId);
        if (brandInfo == null) {
            response.put("code", 404);
            response.put("message", "品牌不存在");
            return ResponseEntity.ok(response);
        }

        response.put("code", 200);
        response.put("message", "success");
        response.put("data", brandInfo);
        return ResponseEntity.ok(response);
    }
}
