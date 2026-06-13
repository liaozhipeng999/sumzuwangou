package com.mall.service;

import com.mall.entity.TermProducts;
import com.mall.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendService {

    private final ProductMapper productMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String REDIS_KEY_HOT_PRODUCTS = "recommend:hot";
    private static final String REDIS_KEY_TOP_RATED = "recommend:top_rated";
    private static final String REDIS_KEY_CATEGORY = "recommend:category:%s";
    private static final String REDIS_KEY_USER_RECOMMEND = "recommend:user:%s";
    private static final String REDIS_KEY_NEW_PRODUCTS = "recommend:new";
    private static final Duration CACHE_DURATION = Duration.ofHours(1);

    public List<TermProducts> getRecommendations(Long userId, int limit) {
        List<TermProducts> recommendations = new ArrayList<>();
        Set<Long> selectedProductIds = new HashSet<>();

        List<TermProducts> hotFlagProducts = getHotFlagProducts(Math.min(limit / 4, 3));
        for (TermProducts product : hotFlagProducts) {
            if (!selectedProductIds.contains(product.getId())) {
                recommendations.add(product);
                selectedProductIds.add(product.getId());
            }
        }

        List<TermProducts> newProducts = getNewProducts(Math.min(limit / 4, 3));
        for (TermProducts product : newProducts) {
            if (!selectedProductIds.contains(product.getId()) && recommendations.size() < limit) {
                recommendations.add(product);
                selectedProductIds.add(product.getId());
            }
        }

        List<TermProducts> userRecommend = getUserRecommendations(userId, limit / 3);
        for (TermProducts product : userRecommend) {
            if (!selectedProductIds.contains(product.getId()) && recommendations.size() < limit) {
                recommendations.add(product);
                selectedProductIds.add(product.getId());
            }
        }

        List<TermProducts> hotProducts = getHotProducts(limit - recommendations.size());
        for (TermProducts product : hotProducts) {
            if (!selectedProductIds.contains(product.getId()) && recommendations.size() < limit) {
                recommendations.add(product);
                selectedProductIds.add(product.getId());
            }
        }

        if (recommendations.size() < limit) {
            List<TermProducts> randomProducts = getRandomProducts(limit - recommendations.size());
            for (TermProducts product : randomProducts) {
                if (!selectedProductIds.contains(product.getId()) && recommendations.size() < limit) {
                    recommendations.add(product);
                    selectedProductIds.add(product.getId());
                }
            }
        }

        Collections.shuffle(recommendations);
        return recommendations;
    }

    public List<TermProducts> getUserRecommendations(Long userId, int limit) {
        if (userId == null) {
            return getRandomProducts(limit);
        }

        String cacheKey = String.format(REDIS_KEY_USER_RECOMMEND, userId);
        List<TermProducts> cached = getCachedRecommendations(cacheKey);
        if (cached != null && !cached.isEmpty()) {
            return cached.stream().limit(limit).collect(Collectors.toList());
        }

        List<Long> categoryIds = productMapper.findAllCategoryIds();
        if (categoryIds.isEmpty()) {
            return getRandomProducts(limit);
        }

        List<TermProducts> recommendations = new ArrayList<>();
        Random random = new Random();
        
        int categoriesToPick = Math.min(3, categoryIds.size());
        Set<Long> selectedCategories = new HashSet<>();
        
        while (selectedCategories.size() < categoriesToPick) {
            Long categoryId = categoryIds.get(random.nextInt(categoryIds.size()));
            selectedCategories.add(categoryId);
        }

        for (Long categoryId : selectedCategories) {
            List<TermProducts> products = getProductsByCategory(categoryId, limit);
            recommendations.addAll(products);
        }

        Collections.shuffle(recommendations);
        recommendations = recommendations.stream().limit(limit).collect(Collectors.toList());

        cacheRecommendations(cacheKey, recommendations);
        return recommendations;
    }

    public List<TermProducts> getHotProducts(int limit) {
        List<TermProducts> cached = getCachedRecommendations(REDIS_KEY_HOT_PRODUCTS);
        if (cached != null && !cached.isEmpty()) {
            return cached.stream().limit(limit).collect(Collectors.toList());
        }

        List<TermProducts> products = productMapper.findHotProducts(limit * 2);
        cacheRecommendations(REDIS_KEY_HOT_PRODUCTS, products);
        return products.stream().limit(limit).collect(Collectors.toList());
    }

    public List<TermProducts> getHotFlagProducts(int limit) {
        return productMapper.findHotFlagProducts(limit);
    }

    public List<TermProducts> getNewProducts(int limit) {
        List<TermProducts> cached = getCachedRecommendations(REDIS_KEY_NEW_PRODUCTS);
        if (cached != null && !cached.isEmpty()) {
            return cached.stream().limit(limit).collect(Collectors.toList());
        }

        List<TermProducts> products = productMapper.findNewProducts(limit * 2);
        cacheRecommendations(REDIS_KEY_NEW_PRODUCTS, products);
        return products.stream().limit(limit).collect(Collectors.toList());
    }

    public List<TermProducts> getTopRatedProducts(int limit) {
        List<TermProducts> cached = getCachedRecommendations(REDIS_KEY_TOP_RATED);
        if (cached != null && !cached.isEmpty()) {
            return cached.stream().limit(limit).collect(Collectors.toList());
        }

        List<TermProducts> products = productMapper.findTopRatedProducts(limit * 2);
        cacheRecommendations(REDIS_KEY_TOP_RATED, products);
        return products.stream().limit(limit).collect(Collectors.toList());
    }

    public List<TermProducts> getProductsByCategory(Long categoryId, int limit) {
        String cacheKey = String.format(REDIS_KEY_CATEGORY, categoryId);
        List<TermProducts> cached = getCachedRecommendations(cacheKey);
        if (cached != null && !cached.isEmpty()) {
            return cached.stream().limit(limit).collect(Collectors.toList());
        }

        List<TermProducts> products = productMapper.findByCategory(categoryId, limit * 2);
        cacheRecommendations(cacheKey, products);
        return products.stream().limit(limit).collect(Collectors.toList());
    }

    public List<TermProducts> getRandomProducts(int limit) {
        return productMapper.findRandomProducts(limit);
    }

    private List<TermProducts> getCachedRecommendations(String key) {
        try {
            List<Object> cached = redisTemplate.opsForList().range(key, 0, -1);
            if (cached != null && !cached.isEmpty()) {
                return cached.stream()
                        .map(obj -> (TermProducts) obj)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.warn("Failed to get cached recommendations: {}", e.getMessage());
        }
        return null;
    }

    private void cacheRecommendations(String key, List<TermProducts> products) {
        try {
            redisTemplate.delete(key);
            if (!products.isEmpty()) {
                redisTemplate.opsForList().rightPushAll(key, products.toArray());
                redisTemplate.expire(key, CACHE_DURATION);
            }
        } catch (Exception e) {
            log.warn("Failed to cache recommendations: {}", e.getMessage());
        }
    }

    public void recordUserView(Long userId, Long productId) {
        if (userId == null || productId == null) return;
        
        try {
            String viewKey = "user:view:" + userId;
            redisTemplate.opsForList().leftPush(viewKey, productId);
            redisTemplate.opsForList().trim(viewKey, 0, 49);
            redisTemplate.expire(viewKey, Duration.ofDays(7));

            String productViewKey = "product:views:" + productId;
            redisTemplate.opsForValue().increment(productViewKey);
        } catch (Exception e) {
            log.warn("Failed to record user view: {}", e.getMessage());
        }
    }

    public void clearUserRecommendCache(Long userId) {
        if (userId != null) {
            String cacheKey = String.format(REDIS_KEY_USER_RECOMMEND, userId);
            redisTemplate.delete(cacheKey);
        }
    }
}