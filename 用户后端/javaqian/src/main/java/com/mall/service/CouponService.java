package com.mall.service;

import com.mall.dto.CouponDTO;
import com.mall.dto.ProductWithCouponDTO;
import com.mall.entity.Coupon;
import com.mall.entity.UserCoupon;
import com.mall.mapper.CouponMapper;
import com.mall.mapper.UserCouponMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CouponService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CouponService.class);

    private final CouponMapper couponMapper;
    private final UserCouponMapper userCouponMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String COUPON_USER_LIMIT_KEY = "coupon:user:limit:%d:%d";
    private static final String COUPON_CACHE_KEY = "coupon:cache:%d";
    private static final String HOT_PRODUCTS_CACHE_KEY = "hot:products:page:%d";
    private static final String HOT_PRODUCTS_CACHE_TTL = "hot:products:ttl";

    private final Map<Long, List<ProductWithCouponDTO>> preloadedProducts = new ConcurrentHashMap<>();

    public List<CouponDTO> getAvailableCoupons() {
        List<Coupon> coupons = couponMapper.findAvailableCoupons(LocalDateTime.now());
        return coupons.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<CouponDTO> getUserCoupons(Long userId) {
        List<UserCoupon> userCoupons = userCouponMapper.findUnusedCouponsByUserId(userId);
        List<CouponDTO> result = new ArrayList<>();
        
        for (UserCoupon uc : userCoupons) {
            Coupon coupon = couponMapper.selectById(uc.getCouponId());
            if (coupon != null && coupon.getStatus() == 1 && 
                coupon.getEndTime().isAfter(LocalDateTime.now())) {
                CouponDTO dto = convertToDTO(coupon);
                result.add(dto);
            }
        }
        return result;
    }

    @Transactional
    public boolean receiveCoupon(Long userId, Long couponId) {
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null || coupon.getStatus() != 1) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        if (coupon.getStartTime().isAfter(now) || coupon.getEndTime().isBefore(now)) {
            return false;
        }

        String limitKey = String.format(COUPON_USER_LIMIT_KEY, userId, couponId);
        Integer count = (Integer) redisTemplate.opsForValue().get(limitKey);
        if (count == null) {
            count = userCouponMapper.countUserCoupon(userId, couponId);
            redisTemplate.opsForValue().set(limitKey, count, 5, TimeUnit.MINUTES);
        }

        if (count >= coupon.getPerUserLimit()) {
            return false;
        }

        if (coupon.getReceivedCount() >= coupon.getTotalCount()) {
            return false;
        }

        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUserId(userId);
        userCoupon.setCouponId(couponId);
        userCoupon.setStatus(1);
        
        userCouponMapper.insert(userCoupon);
        coupon.setReceivedCount(coupon.getReceivedCount() + 1);
        couponMapper.updateById(coupon);

        redisTemplate.opsForValue().increment(limitKey);
        cacheCoupon(coupon);

        return true;
    }

    public ProductWithCouponDTO calculateDiscountedPrice(Long userId, ProductWithCouponDTO product) {
        if (userId == null) {
            return product;
        }

        List<UserCoupon> userCoupons = userCouponMapper.findUnusedCouponsByUserId(userId);
        BigDecimal bestDiscount = BigDecimal.ZERO;
        Coupon bestCoupon = null;

        for (UserCoupon uc : userCoupons) {
            Coupon coupon = getCouponFromCache(uc.getCouponId());
            if (coupon == null) {
                coupon = couponMapper.selectById(uc.getCouponId());
                if (coupon != null) {
                    cacheCoupon(coupon);
                }
            }

            if (coupon == null || coupon.getStatus() != 1) {
                continue;
            }

            LocalDateTime now = LocalDateTime.now();
            if (coupon.getStartTime().isAfter(now) || coupon.getEndTime().isBefore(now)) {
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
            product.setApplicableCoupon(convertToDTO(bestCoupon));
        }

        return product;
    }

    public List<ProductWithCouponDTO> getHotProductsWithCoupon(Long userId, int page, int pageSize) {
        String cacheKey = String.format(HOT_PRODUCTS_CACHE_KEY, page);
        
        @SuppressWarnings("unchecked")
        List<ProductWithCouponDTO> cachedProducts = (List<ProductWithCouponDTO>) redisTemplate.opsForValue().get(cacheKey);
        
        if (cachedProducts == null) {
            cachedProducts = fetchHotProductsFromDB(page, pageSize);
            redisTemplate.opsForValue().set(cacheKey, cachedProducts, 5, TimeUnit.MINUTES);
            preloadedProducts.put((long) page, cachedProducts);
            
            preloadNextPages(page, pageSize);
        }

        return cachedProducts.stream()
                .map(product -> calculateDiscountedPrice(userId, product))
                .collect(Collectors.toList());
    }

    private void preloadNextPages(int currentPage, int pageSize) {
        for (int i = 1; i <= 2; i++) {
            int nextPage = currentPage + i;
            String cacheKey = String.format(HOT_PRODUCTS_CACHE_KEY, nextPage);
            
            if (redisTemplate.hasKey(cacheKey)) {
                continue;
            }

            try {
                List<ProductWithCouponDTO> products = fetchHotProductsFromDB(nextPage, pageSize);
                if (!products.isEmpty()) {
                    redisTemplate.opsForValue().set(cacheKey, products, 5, TimeUnit.MINUTES);
                    preloadedProducts.put((long) nextPage, products);
                }
            } catch (Exception e) {
                log.error("Preload page {} failed", nextPage, e);
            }
        }
    }

    private List<ProductWithCouponDTO> fetchHotProductsFromDB(int page, int pageSize) {
        return new ArrayList<>();
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
        if (coupon.getMinAmount().compareTo(product.getPrice()) > 0) {
            return false;
        }
        return true;
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

    private CouponDTO convertToDTO(Coupon coupon) {
        CouponDTO dto = new CouponDTO();
        dto.setId(coupon.getId());
        dto.setCouponName(coupon.getCouponName());
        dto.setCouponType(coupon.getCouponType());
        dto.setCouponTypeName(getCouponTypeName(coupon.getCouponType()));
        dto.setDiscountValue(coupon.getDiscountValue());
        dto.setMinAmount(coupon.getMinAmount());
        dto.setProductCategoryId(coupon.getProductCategoryId());
        dto.setProductId(coupon.getProductId());
        dto.setMerchantId(coupon.getMerchantId());
        dto.setTotalCount(coupon.getTotalCount());
        dto.setReceivedCount(coupon.getReceivedCount());
        dto.setPerUserLimit(coupon.getPerUserLimit());
        
        LocalDateTime now = LocalDateTime.now();
        if (coupon.getEndTime().isAfter(now)) {
            long seconds = java.time.Duration.between(now, coupon.getEndTime()).getSeconds();
            dto.setRemainingSeconds(seconds);
        } else {
            dto.setRemainingSeconds(0L);
        }
        
        dto.setStatus(coupon.getStatus());
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

    private void cacheCoupon(Coupon coupon) {
        String key = String.format(COUPON_CACHE_KEY, coupon.getId());
        redisTemplate.opsForValue().set(key, coupon, 5, TimeUnit.MINUTES);
    }

    private Coupon getCouponFromCache(Long couponId) {
        String key = String.format(COUPON_CACHE_KEY, couponId);
        return (Coupon) redisTemplate.opsForValue().get(key);
    }
}
