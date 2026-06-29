package com.mall.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/merchant")
@RequiredArgsConstructor
public class MerchantController {

    @Resource
    private com.mall.mapper.MerchantMapper merchantMapper;

    @GetMapping("/detail/{id}")
    public ResponseEntity<Map<String, Object>> getMerchantDetail(@PathVariable Long id) {
        Map<String, Object> merchant = merchantMapper.getMerchantDetail(id);
        
        if (merchant == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 404);
            error.put("message", "商家不存在");
            return ResponseEntity.ok(error);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", merchant);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/products")
    public ResponseEntity<Map<String, Object>> getMerchantProducts(
            @PathVariable Long id,
            @RequestParam(defaultValue = "20") int limit) {
        
        Map<String, Object> merchant = merchantMapper.getMerchantDetail(id);
        if (merchant == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 404);
            error.put("message", "商家不存在");
            return ResponseEntity.ok(error);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", merchantMapper.getMerchantProducts(id, limit));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getMerchantList(
            @RequestParam(defaultValue = "10") int limit) {
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", merchantMapper.getMerchantList(limit));
        return ResponseEntity.ok(response);
    }
}