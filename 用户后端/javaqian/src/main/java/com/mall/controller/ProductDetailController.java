package com.mall.controller;

import com.mall.service.ProductDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductDetailController {

    private final ProductDetailService productDetailService;

    /**
     * 获取商品详情（轮播图、商家信息、拼单、评论等）
     * GET /api/product/detail?productId=179&userId=1
     */
    @GetMapping("/detail")
    public Map<String, Object> getProductDetail(
            @RequestParam Long productId,
            @RequestParam(required = false) Long userId) {
        return productDetailService.getProductDetail(productId, userId);
    }

    /**
     * 收藏/取消收藏商品
     * POST /api/product/favorite
     */
    @PostMapping("/favorite")
    public Map<String, Object> toggleFavorite(
            @RequestParam Long userId,
            @RequestParam Long productId) {
        return productDetailService.toggleFavorite(userId, productId);
    }

    /**
     * 获取底部价格栏信息
     * GET /api/product/bottom-bar?productId=179&userId=1
     */
    @GetMapping("/bottom-bar")
    public Map<String, Object> getBottomPriceBar(
            @RequestParam Long productId,
            @RequestParam(required = false) Long userId) {
        return productDetailService.getBottomPriceBar(productId, userId);
    }
}
