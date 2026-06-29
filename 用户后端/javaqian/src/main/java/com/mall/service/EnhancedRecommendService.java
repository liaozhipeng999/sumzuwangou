package com.mall.service;

import com.mall.dto.ProductWithCouponDTO;
import com.mall.entity.Coupon;
import com.mall.entity.TermProducts;
import com.mall.entity.UserCoupon;
import com.mall.mapper.CouponMapper;
import com.mall.mapper.ProductMapper;
import com.mall.mapper.UserCouponMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnhancedRecommendService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(EnhancedRecommendService.class);

    private final ProductMapper productMapper;
    private final CouponMapper couponMapper;
    private final UserCouponMapper userCouponMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String HOT_PRODUCTS_CACHE_KEY = "hot:products:page:%d";
    private static final String COUPON_CACHE_KEY = "coupon:cache:%d";
    private static final String USER_HOUR_COUPON_KEY = "user:hour:coupon:%d";
    private static final int PRELOAD_PAGES = 3;
    private static final int PAGE_SIZE = 10;
    private static final int HOUR_COUPON_DISCOUNT = 5;
    private static final int HOUR_COUPON_MIN_AMOUNT = 0;

    private final Map<Long, List<ProductWithCouponDTO>> preloadedPages = new ConcurrentHashMap<>();

    public List<ProductWithCouponDTO> getHotProductsWithCoupon(Long userId, int page) {
        List<ProductWithCouponDTO> products = getHotProductsFromCache(page);
        
        if (userId != null) {
            ensureHourCouponForUser(userId);
            products = products.stream()
                    .map(product -> applyCouponToProduct(userId, product))
                    .collect(Collectors.toList());
        }

        return products;
    }

    private void ensureHourCouponForUser(Long userId) {
        String redisKey = String.format(USER_HOUR_COUPON_KEY, userId);
        
        if (Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
            return;
        }

        synchronized (this) {
            if (Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
                return;
            }

            try {
                createHourCouponForUser(userId);
                redisTemplate.opsForValue().set(redisKey, "1", 1, TimeUnit.HOURS);
                log.info("Created 1-hour coupon for user: {}", userId);
            } catch (Exception e) {
                log.error("Failed to create hour coupon for user: {}", userId, e);
            }
        }
    }

    @Transactional
    public void createHourCouponForUser(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime = now.plusHours(1);

        Coupon hourCoupon = new Coupon();
        hourCoupon.setCouponName("限时1小时5元券");
        hourCoupon.setCouponType(3);
        hourCoupon.setDiscountValue(BigDecimal.valueOf(HOUR_COUPON_DISCOUNT));
        hourCoupon.setMinAmount(BigDecimal.valueOf(HOUR_COUPON_MIN_AMOUNT));
        hourCoupon.setStartTime(now);
        hourCoupon.setEndTime(endTime);
        hourCoupon.setStatus(1);
        hourCoupon.setTotalCount(1);
        hourCoupon.setReceivedCount(1);
        hourCoupon.setPerUserLimit(1);
        couponMapper.insert(hourCoupon);

        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUserId(userId);
        userCoupon.setCouponId(hourCoupon.getId());
        userCoupon.setStatus(1);
        userCouponMapper.insert(userCoupon);
    }

    public Map<String, Object> getHotProductsWithPagination(Long userId, int page) {
        List<ProductWithCouponDTO> products = getHotProductsWithCoupon(userId, page);
        
        preloadPagesAsync(page);

        Map<String, Object> result = new HashMap<>();
        result.put("data", products);
        result.put("page", page);
        result.put("pageSize", PAGE_SIZE);
        result.put("hasMore", hasMorePages(page));
        result.put("preloadedPages", getPreloadedPageNumbers(page));

        return result;
    }

    private List<ProductWithCouponDTO> getHotProductsFromCache(int page) {
        String cacheKey = String.format(HOT_PRODUCTS_CACHE_KEY, page);
        
        @SuppressWarnings("unchecked")
        List<ProductWithCouponDTO> cached = (List<ProductWithCouponDTO>) redisTemplate.opsForValue().get(cacheKey);
        
        if (cached != null && !cached.isEmpty()) {
            preloadedPages.put((long) page, cached);
            return new ArrayList<>(cached);
        }

        List<TermProducts> products = productMapper.findHotProductsPaged(page * PAGE_SIZE, PAGE_SIZE);
        List<ProductWithCouponDTO> dtoList = products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        redisTemplate.opsForValue().set(cacheKey, dtoList, 5, TimeUnit.MINUTES);
        preloadedPages.put((long) page, dtoList);

        return dtoList;
    }

    private ProductWithCouponDTO applyCouponToProduct(Long userId, ProductWithCouponDTO product) {
        List<UserCoupon> userCoupons = userCouponMapper.findUnusedCouponsByUserId(userId);
        BigDecimal bestDiscount = BigDecimal.ZERO;
        Coupon bestCoupon = null;

        for (UserCoupon uc : userCoupons) {
            Coupon coupon = couponMapper.selectById(uc.getCouponId());

            if (coupon == null || !isCouponValid(coupon)) {
                continue;
            }

            if (!isCouponApplicable(coupon, product)) {
                continue;
            }

            BigDecimal discount = calculateDiscount(coupon, product.getPrice());
            if (discount.compareTo(bestDiscount) > 0) {
                bestDiscount = discount;
                bestCoupon = coupon;
            }
        }

        if (bestCoupon != null && bestDiscount.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal discountedPrice = product.getPrice().subtract(bestDiscount);
            if (discountedPrice.compareTo(BigDecimal.ZERO) < 0) {
                discountedPrice = BigDecimal.ZERO;
            }
            product.setDiscountedPrice(discountedPrice);
            
            com.mall.dto.CouponDTO couponDTO = convertCouponToDTO(bestCoupon);
            product.setApplicableCoupon(couponDTO);
        }

        return product;
    }

    private boolean isCouponValid(Coupon coupon) {
        LocalDateTime now = LocalDateTime.now();
        return coupon.getStatus() == 1 && 
               !coupon.getStartTime().isAfter(now) && 
               !coupon.getEndTime().isBefore(now);
    }

    private boolean isCouponApplicable(Coupon coupon, ProductWithCouponDTO product) {
        if (coupon.getProductCategoryId() != null && !coupon.getProductCategoryId().equals(product.getCategoryId())) {
            return false;
        }
        if (coupon.getProductId() != null && !coupon.getProductId().equals(product.getId())) {
            return false;
        }
        if (coupon.getMerchantId() != null && !coupon.getMerchantId().equals(product.getMerchantId())) {
            return false;
        }
        return coupon.getMinAmount().compareTo(product.getPrice()) <= 0;
    }

    private BigDecimal calculateDiscount(Coupon coupon, BigDecimal price) {
        switch (coupon.getCouponType()) {
            case 1:
                return coupon.getDiscountValue();
            case 2:
                return price.subtract(price.multiply(coupon.getDiscountValue()));
            case 3:
                return coupon.getDiscountValue();
            default:
                return BigDecimal.ZERO;
        }
    }

    private ProductWithCouponDTO convertToDTO(TermProducts product) {
        ProductWithCouponDTO dto = new ProductWithCouponDTO();
        dto.setId(product.getId());
        dto.setProductName(product.getProductName());
        dto.setProductCode(product.getProductCode());
        dto.setCategoryId(product.getCategoryId());
        dto.setMerchantId(product.getMerchantId());
        dto.setPrice(product.getPrice());
        dto.setOriginalPrice(product.getOriginalPrice() != null ? product.getOriginalPrice() : product.getPrice());
        dto.setStock(product.getStock());
        dto.setSales(product.getSales());
        dto.setMainImage(product.getMainImage());
        dto.setBrief(product.getBrief());
        dto.setIsHot(product.getIsHot());
        dto.setIsNew(product.getIsNew());
        return dto;
    }

    private com.mall.dto.CouponDTO convertCouponToDTO(Coupon coupon) {
        com.mall.dto.CouponDTO dto = new com.mall.dto.CouponDTO();
        dto.setId(coupon.getId());
        dto.setCouponName(coupon.getCouponName());
        dto.setCouponType(coupon.getCouponType());
        dto.setCouponTypeName(getCouponTypeName(coupon.getCouponType()));
        dto.setDiscountValue(coupon.getDiscountValue());
        dto.setMinAmount(coupon.getMinAmount());
        
        LocalDateTime now = LocalDateTime.now();
        if (coupon.getEndTime().isAfter(now)) {
            long seconds = java.time.Duration.between(now, coupon.getEndTime()).getSeconds();
            dto.setRemainingSeconds(seconds);
        } else {
            dto.setRemainingSeconds(0L);
        }
        
        return dto;
    }

    private String getCouponTypeName(Integer type) {
        switch (type) {
            case 1:
                return "满减券";
            case 2:
                return "折扣券";
            case 3:
                return "无门槛券";
            default:
                return "未知";
        }
    }

    private void preloadPagesAsync(int currentPage) {
        for (int i = 1; i <= PRELOAD_PAGES; i++) {
            int nextPage = currentPage + i;
            String cacheKey = String.format(HOT_PRODUCTS_CACHE_KEY, nextPage);
            
            if (redisTemplate.hasKey(cacheKey)) {
                continue;
            }

            try {
                List<TermProducts> products = productMapper.findHotProductsPaged(nextPage * PAGE_SIZE, PAGE_SIZE);
                if (!products.isEmpty()) {
                    List<ProductWithCouponDTO> dtoList = products.stream()
                            .map(this::convertToDTO)
                            .collect(Collectors.toList());
                    redisTemplate.opsForValue().set(cacheKey, dtoList, 5, TimeUnit.MINUTES);
                    preloadedPages.put((long) nextPage, dtoList);
                    log.info("Preloaded page {} with {} products", nextPage, dtoList.size());
                }
            } catch (Exception e) {
                log.error("Failed to preload page {}", nextPage, e);
            }
        }
    }

    private boolean hasMorePages(int page) {
        int nextPage = page + 1;
        String cacheKey = String.format(HOT_PRODUCTS_CACHE_KEY, nextPage);
        
        if (redisTemplate.hasKey(cacheKey)) {
            @SuppressWarnings("unchecked")
            List<ProductWithCouponDTO> cached = (List<ProductWithCouponDTO>) redisTemplate.opsForValue().get(cacheKey);
            return cached != null && !cached.isEmpty();
        }

        List<TermProducts> products = productMapper.findHotProductsPaged(nextPage * PAGE_SIZE, PAGE_SIZE);
        return !products.isEmpty();
    }

    private List<Integer> getPreloadedPageNumbers(int currentPage) {
        List<Integer> preloaded = new ArrayList<>();
        for (int i = 1; i <= PRELOAD_PAGES; i++) {
            int pageNum = currentPage + i;
            if (preloadedPages.containsKey((long) pageNum)) {
                preloaded.add(pageNum);
            }
        }
        return preloaded;
    }

    private void cacheCoupon(Coupon coupon) {
        String key = String.format(COUPON_CACHE_KEY, coupon.getId());
        redisTemplate.opsForValue().set(key, coupon, 5, TimeUnit.MINUTES);
    }

    private Coupon getCouponFromCache(Long couponId) {
        String key = String.format(COUPON_CACHE_KEY, couponId);
        return (Coupon) redisTemplate.opsForValue().get(key);
    }
}
