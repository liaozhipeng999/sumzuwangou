package com.mall.controller;

import com.mall.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/products")
    public ResponseEntity<Map<String, Object>> searchProducts(
            @RequestParam String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "sales") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {
        
        Map<String, Object> result = searchService.searchProducts(keyword, categoryId, page, pageSize, sortBy, sortOrder, minPrice, maxPrice);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", result);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/suggest")
    public ResponseEntity<Map<String, Object>> getSearchSuggestions(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "5") int limit) {
        
        List<?> products = searchService.searchProductsSimple(keyword, limit);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", products);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<Map<String, Object>> getSearchHistory(
            @RequestParam(defaultValue = "default") String userId) {
        
        List<String> history = searchService.getSearchHistory(userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", history);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/history")
    public ResponseEntity<Map<String, Object>> saveSearchHistory(
            @RequestParam(defaultValue = "default") String userId,
            @RequestParam String keyword) {
        
        searchService.saveSearchHistory(userId, keyword);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "保存成功");
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/history")
    public ResponseEntity<Map<String, Object>> clearSearchHistory(
            @RequestParam(defaultValue = "default") String userId) {
        
        searchService.clearSearchHistory(userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "清除成功");
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/hot")
    public ResponseEntity<Map<String, Object>> getHotSearch(
            @RequestParam(defaultValue = "10") int limit) {
        
        List<String> hotKeywords = searchService.getHotSearchKeywords(limit);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", hotKeywords);
        
        return ResponseEntity.ok(response);
    }
}