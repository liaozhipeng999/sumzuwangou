package com.mall.controller;

import com.mall.service.ProductAttributeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/product/attributes")
@CrossOrigin(origins = "*")
public class ProductAttributeController {

    @Autowired
    private ProductAttributeService productAttributeService;

    @GetMapping("/options")
    public Map<String, Object> getProductAttributeOptions(@RequestParam Long productId) {
        return productAttributeService.getProductAttributeOptions(productId);
    }

    @GetMapping("/dimensions")
    public Map<String, Object> getAttributeOptionsByType(@RequestParam(defaultValue = "default") String productType) {
        return productAttributeService.getAttributeOptionsByType(productType);
    }

    @GetMapping("/discount")
    public Map<String, Object> calculateDiscount(
            @RequestParam Long productId,
            @RequestParam Long skuId,
            @RequestParam(required = false) Long userId) {
        return productAttributeService.calculateDiscount(productId, skuId, userId);
    }

    @GetMapping("/types")
    public Map<String, Object> getAllProductTypes() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<String> types = productAttributeService.getAllProductTypes();
            result.put("code", 200);
            result.put("message", "success");
            result.put("data", types);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "获取商品类型失败: " + e.getMessage());
        }
        return result;
    }
}