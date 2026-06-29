package com.mall.service;

import com.mall.entity.TermProducts;
import com.mall.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(RecommendService.class);

    private final ProductMapper productMapper;

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
        return recommendations.stream().limit(limit).collect(Collectors.toList());
    }

    public List<TermProducts> getHotProducts(int limit) {
        // 查询3倍limit的数据，用于随机选择
        List<TermProducts> products = productMapper.findHotProducts(limit * 3);
        // 随机打乱顺序
        Collections.shuffle(products);
        // 返回前limit条
        return products.stream().limit(limit).collect(Collectors.toList());
    }

    public List<TermProducts> getHotFlagProducts(int limit) {
        return productMapper.findHotFlagProducts(limit);
    }

    public List<TermProducts> getNewProducts(int limit) {
        return productMapper.findNewProducts(limit);
    }

    public List<TermProducts> getTopRatedProducts(int limit) {
        return productMapper.findTopRatedProducts(limit);
    }

    public List<TermProducts> getProductsByCategory(Long categoryId, int limit) {
        return productMapper.findByCategory(categoryId, limit);
    }

    public List<TermProducts> getRandomProducts(int limit) {
        return productMapper.findRandomProducts(limit);
    }

    public void recordUserView(Long userId, Long productId) {
        // 简化：不使用Redis记录浏览记录
        log.info("User {} viewed product {}", userId, productId);
    }

    public void clearUserRecommendCache(Long userId) {
        // 简化：无缓存可清除
    }
}