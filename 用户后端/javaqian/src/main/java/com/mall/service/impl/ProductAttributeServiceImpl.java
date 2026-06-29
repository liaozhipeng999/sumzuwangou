package com.mall.service.impl;

import com.mall.entity.*;
import com.mall.mapper.*;
import com.mall.service.ProductAttributeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductAttributeServiceImpl implements ProductAttributeService {

    @Autowired
    private ProductAttributeDimensionMapper dimensionMapper;

    @Autowired
    private ProductSkuMapper productSkuMapper;

    @Autowired
    private ProductAttributeMapper productAttributeMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private UserCouponMapper userCouponMapper;

    @Autowired
    private CouponMapper couponMapper;

    @Override
    public Map<String, Object> getProductAttributeOptions(Long productId) {
        Map<String, Object> result = new HashMap<>();

        TermProducts product = productMapper.selectById(productId);
        if (product == null) {
            result.put("code", 400);
            result.put("message", "商品不存在");
            return result;
        }

        String productType = matchProductType(product.getProductName());
        List<ProductSku> skus = productSkuMapper.findByProductId(productId);

        Map<String, DimensionRuntime> dimRuntime = new LinkedHashMap<>();

        List<ProductAttributeDimension> configured = dimensionMapper.findByProductType(productType);
        if (configured.isEmpty()) {
            configured = dimensionMapper.findByProductType("default");
        }
        for (ProductAttributeDimension d : configured) {
            dimRuntime.put(d.getDimensionKey(), new DimensionRuntime(d.getDimensionName(), d.getSortOrder()));
        }

        Map<String, Integer> autoOrder = new HashMap<>();
        int autoSeq = 100;
        for (ProductSku sku : skus) {
            for (String attrKey : sku.getAttributesMap().keySet()) {
                if ("name".equals(attrKey)) continue;
                if (!dimRuntime.containsKey(attrKey)) {
                    dimRuntime.put(attrKey, new DimensionRuntime(attrKey, autoSeq++));
                }
            }
        }

        Map<String, Object> attributeOptions = new LinkedHashMap<>();
        Map<Long, Map<String, Object>> skuMap = new HashMap<>();

        for (Map.Entry<String, DimensionRuntime> e : dimRuntime.entrySet()) {
            String key = e.getKey();
            attributeOptions.put(key, new LinkedHashMap<>());
            attributeOptions.put(key + "_label", e.getValue().name);
        }

        for (ProductSku sku : skus) {
            Map<String, Object> skuInfo = new HashMap<>();
            skuInfo.put("skuId", sku.getId());
            skuInfo.put("skuCode", sku.getSkuCode());
            skuInfo.put("price", sku.getPrice());
            skuInfo.put("originalPrice", sku.getOriginalPrice());
            skuInfo.put("stock", sku.getStock());
            skuInfo.put("imageUrl", sku.getImageUrl());
            skuInfo.put("attributes", sku.getAttributesMap());

            skuMap.put(sku.getId(), skuInfo);

            Map<String, String> attrs = sku.getAttributesMap();
            for (String key : dimRuntime.keySet()) {
                String raw = attrs.get(key);
                String value = (raw == null || raw.isEmpty())
                        ? getDefaultValueForKey(key, sku)
                        : raw;

                Map<String, Object> options = (Map<String, Object>) attributeOptions.get(key);
                if (!options.containsKey(value)) {
                    options.put(value, new ArrayList<Long>());
                }
                ((List<Long>) options.get(value)).add(sku.getId());
            }
        }

        List<Map<String, Object>> dimList = new ArrayList<>();
        dimRuntime.entrySet().stream()
                .sorted(Comparator.comparingInt(e -> e.getValue().order))
                .forEach(e -> {
                    Map<String, Object> dim = new HashMap<>();
                    dim.put("key", e.getKey());
                    dim.put("name", e.getValue().name);
                    dim.put("sortOrder", e.getValue().order);
                    dimList.add(dim);
                });

        Map<String, Object> data = new HashMap<>();
        data.put("productId", productId);
        data.put("productName", product.getProductName());
        data.put("productType", productType);
        data.put("dimensions", dimList);
        data.put("attributeOptions", attributeOptions);
        data.put("skus", skuMap);

        result.put("code", 200);
        result.put("message", "success");
        result.put("data", data);

        return result;
    }

    private static class DimensionRuntime {
        String name;
        int order;
        DimensionRuntime(String name, int order) { this.name = name; this.order = order; }
    }

    /**
     * 根据属性键获取默认值
     */
    private String getDefaultValueForKey(String key, ProductSku sku) {
        // 为缺失的属性提供合理的默认值
        switch (key) {
            case "颜色":
                return "默认";
            case "规格":
                return "标准版";
            case "材质":
                return "优质材料";
            case "内存":
                return sku != null ? "8GB" : "默认";
            case "存储":
                return sku != null ? "128GB" : "默认";
            case "尺码":
                return "M";
            case "尺寸":
                return "标准";
            case "口味":
                return "原味";
            case "产地":
                return "中国大陆";
            case "类型":
                return "标准";
            case "处理器":
                return "Intel i5";
            case "适用年龄":
                return "3-6岁";
            default:
                return "默认";
        }
    }

    @Override
    public Map<String, Object> getAttributeOptionsByType(String productType) {
        Map<String, Object> result = new HashMap<>();

        List<ProductAttributeDimension> dimensions = dimensionMapper.findByProductType(productType);
        if (dimensions.isEmpty()) {
            dimensions = dimensionMapper.findByProductType("default");
        }

        List<Map<String, Object>> dimensionList = dimensions.stream().map(d -> {
            Map<String, Object> dim = new HashMap<>();
            dim.put("key", d.getDimensionKey());
            dim.put("name", d.getDimensionName());
            dim.put("sortOrder", d.getSortOrder());
            return dim;
        }).collect(Collectors.toList());

        result.put("code", 200);
        result.put("message", "success");
        result.put("data", dimensionList);

        return result;
    }

    @Override
    public Map<String, Object> calculateDiscount(Long productId, Long skuId, Long userId) {
        Map<String, Object> result = new HashMap<>();

        ProductSku sku = productSkuMapper.selectById(skuId);
        if (sku == null) {
            result.put("code", 400);
            result.put("message", "SKU不存在");
            return result;
        }

        if (!sku.getProductId().equals(productId)) {
            result.put("code", 400);
            result.put("message", "SKU不属于该商品");
            return result;
        }

        BigDecimal originalPrice = sku.getOriginalPrice();
        BigDecimal currentPrice = sku.getPrice();
        BigDecimal discountAmount = originalPrice.subtract(currentPrice);
        BigDecimal discountRate = discountAmount.multiply(BigDecimal.valueOf(100)).divide(originalPrice, 2, BigDecimal.ROUND_HALF_UP);

        Map<String, Object> data = new HashMap<>();
        data.put("productId", productId);
        data.put("skuId", skuId);
        data.put("skuCode", sku.getSkuCode());
        data.put("originalPrice", originalPrice);
        data.put("currentPrice", currentPrice);
        data.put("discountAmount", discountAmount);
        data.put("discountRate", discountRate);
        data.put("discountLabel", getDiscountLabel(discountRate));

        List<Map<String, Object>> availableCoupons = new ArrayList<>();
        if (userId != null) {
            List<UserCoupon> userCoupons = userCouponMapper.findUnusedCouponsByUserId(userId);
            for (UserCoupon uc : userCoupons) {
                Coupon coupon = couponMapper.selectById(uc.getCouponId());
                if (coupon != null && coupon.getStatus() == 1) {
                    boolean valid = true;
                    if (coupon.getProductCategoryId() != null || coupon.getProductId() != null) {
                        valid = false;
                    }

                    if (valid) {
                        Map<String, Object> couponInfo = new HashMap<>();
                        couponInfo.put("userCouponId", uc.getId());
                        couponInfo.put("couponId", coupon.getId());
                        couponInfo.put("couponName", coupon.getCouponName());
                        couponInfo.put("couponType", coupon.getCouponType());
                        couponInfo.put("discountValue", coupon.getDiscountValue());
                        couponInfo.put("minAmount", coupon.getMinAmount());
                        couponInfo.put("startTime", coupon.getStartTime());
                        couponInfo.put("endTime", coupon.getEndTime());

                        BigDecimal couponDiscount = BigDecimal.ZERO;
                        if (currentPrice.compareTo(coupon.getMinAmount()) >= 0) {
                            if (coupon.getCouponType() == 1 || coupon.getCouponType() == 3) {
                                couponDiscount = coupon.getDiscountValue();
                            } else if (coupon.getCouponType() == 2) {
                                couponDiscount = currentPrice.multiply(coupon.getDiscountValue()).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
                            }
                        }
                        couponInfo.put("applicableDiscount", couponDiscount);

                        availableCoupons.add(couponInfo);
                    }
                }
            }
        }
        data.put("availableCoupons", availableCoupons);

        BigDecimal bestCouponDiscount = availableCoupons.stream()
                .map(c -> new BigDecimal(c.get("applicableDiscount").toString()))
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        BigDecimal finalPrice = currentPrice.subtract(bestCouponDiscount);
        if (finalPrice.compareTo(BigDecimal.ZERO) < 0) {
            finalPrice = BigDecimal.ZERO;
        }

        data.put("bestCouponDiscount", bestCouponDiscount);
        data.put("finalPrice", finalPrice);

        result.put("code", 200);
        result.put("message", "success");
        result.put("data", data);

        return result;
    }

    @Override
    public List<String> getAllProductTypes() {
        return dimensionMapper.findAllProductTypes();
    }

    private String matchProductType(String productName) {
        String nameLower = productName.toLowerCase();
        if (productName.contains("自行车") || productName.contains("单车") || productName.contains("轮胎") || productName.contains("内胎") || nameLower.contains("bike") || nameLower.contains("bicycle")) {
            return "bike";
        } else if (productName.contains("手机") || nameLower.contains("mate") || nameLower.contains("iphone") || productName.contains("小米") || nameLower.contains("oppo") || nameLower.contains("vivo")) {
            return "phone";
        } else if (productName.contains("笔记本") || nameLower.contains("macbook") || nameLower.contains("thinkpad") || nameLower.contains("dell") || nameLower.contains("rog") || productName.contains("拯救者") || nameLower.contains("surface")) {
            return "laptop";
        } else if (productName.contains("平板") || nameLower.contains("ipad") || nameLower.contains("tablet")) {
            return "tablet";
        } else if (productName.contains("耳机") || nameLower.contains("airpods") || nameLower.contains("earphone") || nameLower.contains("headphone")) {
            return "earphone";
        } else if (productName.contains("服装") || productName.contains("T恤") || productName.contains("衬衫") || productName.contains("裤子") || productName.contains("裙子") || productName.contains("外套") || nameLower.contains("clothes")) {
            return "clothing";
        } else if (productName.contains("零食") || productName.contains("饼干") || productName.contains("巧克力") || productName.contains("坚果") || productName.contains("糖果") || nameLower.contains("snack")) {
            return "food";
        } else if (productName.contains("水果") || productName.contains("苹果") || productName.contains("香蕉") || productName.contains("橙子") || productName.contains("葡萄")) {
            return "fruit";
        } else if (productName.contains("鞋") || productName.contains("运动鞋") || productName.contains("皮鞋") || productName.contains("凉鞋")) {
            return "shoes";
        } else if (productName.contains("手表") || nameLower.contains("watch") || productName.contains("手环")) {
            return "watch";
        } else if (productName.contains("键盘") || productName.contains("鼠标") || productName.contains("显示器")) {
            return "computer";
        } else if (productName.contains("充电宝") || productName.contains("充电器") || productName.contains("数据线")) {
            return "accessory";
        } else if (productName.contains("玩具") || productName.contains("积木") || nameLower.contains("lego")) {
            return "toy";
        } else if (productName.contains("生鲜") || productName.contains("海鲜") || productName.contains("虾") || productName.contains("蟹") || productName.contains("鱼")) {
            return "fresh";
        } else if (productName.contains("家电") || productName.contains("冰箱") || productName.contains("洗衣机") || productName.contains("空调") || productName.contains("电视")) {
            return "appliance";
        } else if (productName.contains("家居") || productName.contains("家具") || productName.contains("床") || productName.contains("沙发")) {
            return "home";
        } else if (productName.contains("母婴") || productName.contains("奶粉") || productName.contains("纸尿裤")) {
            return "baby";
        } else if (productName.contains("运动") || productName.contains("健身") || productName.contains("球")) {
            return "sports";
        } else if (productName.contains("车品") || productName.contains("汽车") || nameLower.contains("car")) {
            return "car";
        }
        return "default";
    }

    private String getDiscountLabel(BigDecimal discountRate) {
        if (discountRate.compareTo(BigDecimal.valueOf(90)) >= 0) {
            return "一折";
        } else if (discountRate.compareTo(BigDecimal.valueOf(80)) >= 0) {
            return "两折";
        } else if (discountRate.compareTo(BigDecimal.valueOf(70)) >= 0) {
            return "三折";
        } else if (discountRate.compareTo(BigDecimal.valueOf(60)) >= 0) {
            return "四折";
        } else if (discountRate.compareTo(BigDecimal.valueOf(50)) >= 0) {
            return "五折";
        } else if (discountRate.compareTo(BigDecimal.valueOf(40)) >= 0) {
            return "六折";
        } else if (discountRate.compareTo(BigDecimal.valueOf(30)) >= 0) {
            return "七折";
        } else if (discountRate.compareTo(BigDecimal.valueOf(20)) >= 0) {
            return "八折";
        } else if (discountRate.compareTo(BigDecimal.valueOf(10)) >= 0) {
            return "九折";
        } else if (discountRate.compareTo(BigDecimal.valueOf(0)) > 0) {
            return "优惠";
        }
        return "";
    }
}