package com.mall.controller;

import com.mall.service.ProductDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductDetailService productDetailService;

    @GetMapping("/detail/{id}")
    public ResponseEntity<Map<String, Object>> getProductDetail(
            @PathVariable Long id,
            @RequestParam(required = false) Long userId) {
        Map<String, Object> detail = productDetailService.getProductDetail(id, userId);
        
        if (detail == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 404);
            error.put("message", "商品不存在");
            return ResponseEntity.ok(error);
        }

        return ResponseEntity.ok(detail);
    }

    @GetMapping("/{id}/reviews")
    public ResponseEntity<Map<String, Object>> getProductReviews(
            @PathVariable Long id,
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        Map<String, Object> detail = productDetailService.getProductDetail(id, userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", detail.get("data"));
        return ResponseEntity.ok(response);
    }

    /**
     * 收藏/取消收藏商品（切换接口）
     * 点击收藏按钮时，如果已收藏则取消，如果未收藏则添加
     */
    @PostMapping("/favorite/toggle")
    public ResponseEntity<Map<String, Object>> toggleFavorite(
            @RequestParam Long userId,
            @RequestParam Long productId) {
        
        Map<String, Object> result = productDetailService.toggleFavorite(userId, productId);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取用户收藏列表
     */
    @GetMapping("/favorite/list")
    public ResponseEntity<Map<String, Object>> getUserFavorites(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        Map<String, Object> result = productDetailService.getUserFavorites(userId, page, pageSize);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取用户收藏数量
     */
    @GetMapping("/favorite/count")
    public ResponseEntity<Map<String, Object>> getUserFavoriteCount(
            @RequestParam Long userId) {
        
        Map<String, Object> result = productDetailService.getUserFavoriteCount(userId);
        return ResponseEntity.ok(result);
    }
}
