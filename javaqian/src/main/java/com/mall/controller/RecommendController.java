package com.mall.controller;

import com.mall.entity.TermProducts;
import com.mall.service.RecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/recommend")
@RequiredArgsConstructor
public class RecommendController {

    private final RecommendService recommendService;

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
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        
        List<TermProducts> products = recommendService.getHotProducts(limit);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", products);
        
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