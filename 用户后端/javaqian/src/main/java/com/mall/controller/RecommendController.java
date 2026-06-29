package com.mall.controller;

import com.mall.dto.ProductWithCouponDTO;
import com.mall.entity.ProductDetail;
import com.mall.entity.ProductImage;
import com.mall.entity.ProductTag;
import com.mall.entity.TermProducts;
import com.mall.mapper.ProductDetailMapper;
import com.mall.mapper.ProductImageMapper;
import com.mall.mapper.ProductTagMapper;
import com.mall.service.EnhancedRecommendService;
import com.mall.service.RecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/recommend")
@RequiredArgsConstructor
public class RecommendController {

    private final RecommendService recommendService;
    private final EnhancedRecommendService enhancedRecommendService;
    private final ProductImageMapper productImageMapper;
    private final ProductDetailMapper productDetailMapper;
    private final ProductTagMapper productTagMapper;

    @GetMapping("/products")
    public ResponseEntity<Map<String, Object>> getRecommendations(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        
        List<TermProducts> products = recommendService.getRecommendations(userId, limit);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", products);
        response.put("count", products.size());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/hot")
    public ResponseEntity<Map<String, Object>> getHotProducts(
            @RequestParam(value = "limit", defaultValue = "8") int limit) {
        
        List<TermProducts> products = recommendService.getHotProducts(limit);
        
        List<Map<String, Object>> productList = products.stream().map(product -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", product.getId());
            map.put("productName", product.getProductName());
            map.put("price", product.getPrice());
            map.put("originalPrice", product.getOriginalPrice());
            map.put("mainImage", product.getMainImage());
            map.put("sales", product.getSales());
            map.put("brief", product.getBrief());
            map.put("isHot", product.getIsHot());
            map.put("isNew", product.getIsNew());
            map.put("categoryId", product.getCategoryId());
            map.put("merchantId", product.getMerchantId());
            map.put("stock", product.getStock());
            
            // 获取商品轮播图（image_type = 1）
            List<ProductImage> carouselImages = productImageMapper.findByProductIdAndType(product.getId(), 1);
            if (carouselImages.isEmpty()) {
                // 如果没有轮播图，使用主图
                List<Map<String, Object>> mainImageList = new ArrayList<>();
                Map<String, Object> mainImageMap = new HashMap<>();
                mainImageMap.put("imageUrl", product.getMainImage());
                mainImageList.add(mainImageMap);
                map.put("carouselImages", mainImageList);
            } else {
                map.put("carouselImages", carouselImages.stream().map(img -> {
                    Map<String, Object> imgMap = new HashMap<>();
                    imgMap.put("id", img.getId());
                    imgMap.put("imageUrl", img.getImageUrl());
                    imgMap.put("sort", img.getSort());
                    return imgMap;
                }).collect(Collectors.toList()));
            }
            
            // 获取商品详情图（image_type = 2）
            List<ProductImage> detailImages = productImageMapper.findByProductIdAndType(product.getId(), 2);
            map.put("detailImages", detailImages.stream().map(img -> {
                Map<String, Object> imgMap = new HashMap<>();
                imgMap.put("id", img.getId());
                imgMap.put("imageUrl", img.getImageUrl());
                imgMap.put("sort", img.getSort());
                return imgMap;
            }).collect(Collectors.toList()));
            
            // 获取商品参数详情
            List<ProductDetail> details = productDetailMapper.findByProductId(product.getId());
            map.put("details", details.stream().map(detail -> {
                Map<String, Object> detailMap = new HashMap<>();
                detailMap.put("paramKey", detail.getParamKey());
                detailMap.put("paramValue", detail.getParamValue());
                return detailMap;
            }).collect(Collectors.toList()));
            
            // 获取商品标签
            List<ProductTag> tags = productTagMapper.findByProductId(product.getId());
            map.put("tags", tags.stream().map(tag -> {
                Map<String, Object> tagMap = new HashMap<>();
                tagMap.put("tagName", tag.getTagName());
                tagMap.put("tagColor", tag.getTagColor());
                return tagMap;
            }).collect(Collectors.toList()));
            
            return map;
        }).collect(Collectors.toList());
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", productList);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/hot-with-coupon")
    public ResponseEntity<Map<String, Object>> getHotProductsWithCoupon(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "page", defaultValue = "1") int page) {
        
        Map<String, Object> result = enhancedRecommendService.getHotProductsWithPagination(userId, page);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", result.get("data"));
        response.put("page", result.get("page"));
        response.put("pageSize", result.get("pageSize"));
        response.put("hasMore", result.get("hasMore"));
        response.put("preloadedPages", result.get("preloadedPages"));
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/new")
    public ResponseEntity<Map<String, Object>> getNewProducts(
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        
        List<TermProducts> products = recommendService.getNewProducts(limit);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", products);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/top-rated")
    public ResponseEntity<Map<String, Object>> getTopRatedProducts(
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        
        List<TermProducts> products = recommendService.getTopRatedProducts(limit);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", products);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Map<String, Object>> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        
        List<TermProducts> products = recommendService.getProductsByCategory(categoryId, limit);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", products);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/random")
    public ResponseEntity<Map<String, Object>> getRandomProducts(
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        
        List<TermProducts> products = recommendService.getRandomProducts(limit);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", products);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/record-view")
    public ResponseEntity<Map<String, Object>> recordView(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam Long productId) {
        
        recommendService.recordUserView(userId, productId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "记录成功");
        
        return ResponseEntity.ok(response);
    }
}