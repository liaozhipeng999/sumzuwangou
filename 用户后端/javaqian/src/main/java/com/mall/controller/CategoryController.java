package com.mall.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {

    @Resource
    private com.mall.mapper.CategoryMapper categoryMapper;

    @Resource
    private com.mall.mapper.ProductMapper productMapper;

    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getMainCategories() {
        List<Map<String, Object>> categories = categoryMapper.findMainCategories();
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", categories);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/children")
    public ResponseEntity<Map<String, Object>> getSubCategories(@PathVariable Long id) {
        List<Map<String, Object>> subCategories = categoryMapper.findSubCategories(id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", subCategories);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/products")
    public ResponseEntity<Map<String, Object>> getProductsByCategory(
            @PathVariable Long id,
            @RequestParam(defaultValue = "20") int limit) {
        
        List<Map<String, Object>> products = productMapper.findProductsByMainCategory(id, limit);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", products);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sub/{id}/products")
    public ResponseEntity<Map<String, Object>> getProductsBySubCategory(
            @PathVariable Long id,
            @RequestParam(defaultValue = "8") int limit) {
        
        List<com.mall.entity.TermProducts> products = productMapper.findByLevel2CategoryIncludeChildren(id, limit);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", products);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取二级分类下的三级分类列表
     */
    @GetMapping("/level3/{id}")
    public ResponseEntity<Map<String, Object>> getThirdLevelCategories(@PathVariable Long id) {
        List<Map<String, Object>> thirdLevelCategories = categoryMapper.findThirdLevelCategories(id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", thirdLevelCategories);
        return ResponseEntity.ok(response);
    }

    /**
     * 根据二级分类ID获取商品列表（默认8个）
     */
    @GetMapping("/level2/{id}/products")
    public ResponseEntity<Map<String, Object>> getProductsByLevel2Category(
            @PathVariable Long id,
            @RequestParam(defaultValue = "8") int limit) {
        
        List<com.mall.entity.TermProducts> products = productMapper.findByLevel2CategoryIncludeChildren(id, limit);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", products);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/level3/{id}/products")
    public ResponseEntity<Map<String, Object>> getProductsByLevel3Category(
            @PathVariable Long id,
            @RequestParam(defaultValue = "8") int limit) {
        
        List<com.mall.entity.TermProducts> products = productMapper.findByCategory(id, limit);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", products);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取一级分类和二级分类（各10个）
     * 每个一级分类对应1个二级分类
     */
    @GetMapping("/level1-level2")
    public ResponseEntity<Map<String, Object>> getLevel1AndLevel2Categories() {
        List<Map<String, Object>> level1Categories = categoryMapper.findMainCategories();
        
        // 只取前10个一级分类
        level1Categories = level1Categories.stream().limit(10).collect(Collectors.toList());
        
        // 为每个一级分类获取对应的二级分类
        for (Map<String, Object> level1 : level1Categories) {
            Long level1Id = ((Number) level1.get("id")).longValue();
            List<Map<String, Object>> level2Categories = categoryMapper.findSubCategories(level1Id);
            
            // 每个一级分类只取1个二级分类
            if (!level2Categories.isEmpty()) {
                level1.put("level2", level2Categories.get(0));
            }
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", level1Categories);
        return ResponseEntity.ok(response);
    }
}
