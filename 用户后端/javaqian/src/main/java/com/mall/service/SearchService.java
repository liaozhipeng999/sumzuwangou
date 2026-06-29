package com.mall.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.entity.TermProducts;
import com.mall.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final ProductMapper productMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String SEARCH_HISTORY_KEY = "search:history:";
    private static final int MAX_HISTORY_SIZE = 10;

    public Map<String, Object> searchProducts(String keyword, Long categoryId, int page, int pageSize, String sortBy, String sortOrder, Double minPrice, Double maxPrice) {
        QueryWrapper<TermProducts> queryWrapper = new QueryWrapper<>();
        
        queryWrapper.isNull("deleted_at");
        queryWrapper.isNotNull("main_image");
        queryWrapper.ne("main_image", "");
        queryWrapper.like("product_name", keyword);
        
        if (categoryId != null && categoryId > 0) {
            queryWrapper.eq("category_id", categoryId);
        }
        
        if (minPrice != null) {
            queryWrapper.ge("price", minPrice);
        }
        
        if (maxPrice != null) {
            queryWrapper.le("price", maxPrice);
        }
        
        String validSortBy = sortBy;
        String validSortOrder = sortOrder.toLowerCase();
        
        if (sortBy.startsWith("price")) {
            validSortBy = "price";
            if (sortBy.contains("_asc")) {
                validSortOrder = "asc";
            } else if (sortBy.contains("_desc")) {
                validSortOrder = "desc";
            }
        } else if (!sortBy.equals("sales") && !sortBy.equals("price") && !sortBy.equals("created_at") && !sortBy.equals("sort") && !sortBy.equals("default")) {
            validSortBy = "sales";
        }
        
        if (sortBy.equals("default")) {
            validSortBy = "sales";
            validSortOrder = "desc";
        }
        
        if (!validSortOrder.equals("asc") && !validSortOrder.equals("desc")) {
            validSortOrder = "desc";
        }
        
        if (validSortOrder.equals("asc")) {
            queryWrapper.orderByAsc(validSortBy);
        } else {
            queryWrapper.orderByDesc(validSortBy);
        }
        
        Page<TermProducts> pageParam = new Page<>(page, pageSize);
        Page<TermProducts> resultPage = productMapper.selectPage(pageParam, queryWrapper);
        
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("products", resultPage.getRecords());
        result.put("total", resultPage.getTotal());
        result.put("page", page);
        result.put("pageSize", pageSize);
        result.put("totalPages", (int) resultPage.getPages());
        
        return result;
    }

    public List<TermProducts> searchProductsSimple(String keyword, int limit) {
        return productMapper.searchByName(keyword, limit);
    }

    public void saveSearchHistory(String userId, String keyword) {
        String key = SEARCH_HISTORY_KEY + userId;
        
        List<String> history = getSearchHistory(userId);
        
        history.remove(keyword);
        history.add(0, keyword);
        
        if (history.size() > MAX_HISTORY_SIZE) {
            history = history.subList(0, MAX_HISTORY_SIZE);
        }
        
        redisTemplate.opsForValue().set(key, history);
    }

    @SuppressWarnings("unchecked")
    public List<String> getSearchHistory(String userId) {
        String key = SEARCH_HISTORY_KEY + userId;
        Object result = redisTemplate.opsForValue().get(key);
        
        if (result instanceof List) {
            return (List<String>) result;
        }
        
        return new ArrayList<>();
    }

    public void clearSearchHistory(String userId) {
        String key = SEARCH_HISTORY_KEY + userId;
        redisTemplate.delete(key);
    }

    public List<TermProducts> getHotSearchProducts(int limit) {
        return productMapper.findProductsBySales(limit);
    }

    public List<String> getHotSearchKeywords(int limit) {
        List<TermProducts> hotProducts = productMapper.findProductsBySales(limit);
        List<String> keywords = new ArrayList<>();
        
        for (TermProducts product : hotProducts) {
            String name = product.getProductName();
            if (name != null && name.length() > 0) {
                int endIndex = Math.min(name.length(), 8);
                keywords.add(name.substring(0, endIndex));
            }
        }
        
        return keywords;
    }
}