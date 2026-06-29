package com.mall.service;

import com.mall.dto.*;
import com.mall.entity.*;
import com.mall.mapper.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductDetailService {

    private final ProductMapper productMapper;
    private final ProductImageMapper productImageMapper;
    private final ProductReviewMapper productReviewMapper;
    private final ProductDetailMapper productDetailMapper;
    private final ProductTagMapper productTagMapper;
    private final ProductAttributeMapper productAttributeMapper;
    private final ProductSkuMapper productSkuMapper;
    private final MerchantMapper merchantMapper;
    private final MerchantBrandInfoMapper merchantBrandInfoMapper;
    private final GroupOrderMapper groupOrderMapper;
    private final GroupOrderUserMapper groupOrderUserMapper;
    private final UserFavoriteMapper userFavoriteMapper;
    private final UserMapper userMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String GROUP_CACHE_KEY = "group:order:product:%d";
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 获取商品详情（包含轮播图、商家信息、拼单、评论等）
     */
    public Map<String, Object> getProductDetail(Long productId, Long userId) {
        Map<String, Object> result = new HashMap<>();

        // 1. 商品基本信息
        TermProducts product = productMapper.selectById(productId);
        if (product == null) {
            result.put("code", 404);
            result.put("message", "商品不存在");
            return result;
        }

        // 2. 轮播图
        List<ProductImage> carouselImages = productImageMapper.findByProductIdAndType(productId, 1);
        if (carouselImages.isEmpty()) {
            // 如果没有轮播图，使用主图
            carouselImages = new ArrayList<>();
            ProductImage mainImage = new ProductImage();
            mainImage.setProductId(productId);
            mainImage.setImageUrl(product.getMainImage());
            mainImage.setSort(0);
            mainImage.setImageType(1);
            carouselImages.add(mainImage);
        }

        // 3. 商家信息
        Merchant merchant = merchantMapper.selectById(product.getMerchantId());

        // 4. 商家品牌介绍
        MerchantBrandInfo brandInfo = merchantBrandInfoMapper.findByMerchantId(product.getMerchantId());

        // 5. 拼单信息（Redis缓存 + 随机假数据）
        Map<String, Object> groupInfo = getGroupOrderInfo(productId, product.getMerchantId());

        // 6. 评论信息（头像为空则从用户表补齐）
        List<ProductReview> reviews = getProductReviews(productId, product.getMerchantId());
        Set<Long> reviewedUserIds = reviews.stream().map(ProductReview::getUserId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, String> userAvatarMap = new HashMap<>();
        for (Long uid : reviewedUserIds) {
            if (!userAvatarMap.containsKey(uid)) {
                var u = userMapper.selectById(uid);
                if (u != null && u.getAvatar() != null && !u.getAvatar().isBlank()) {
                    userAvatarMap.put(uid, u.getAvatar());
                } else {
                    userAvatarMap.put(uid, "https://api.dicebear.com/7.x/avataaars/svg?seed=" + uid);
                }
            }
        }
        List<Map<String, Object>> reviewDtos = reviews.stream().map(r -> {
            Map<String, Object> dto = convertReviewToDTO(r);
            if (dto.get("avatar") == null || dto.get("avatar").toString().isBlank()) {
                dto.put("avatar", userAvatarMap.getOrDefault(r.getUserId(), "https://api.dicebear.com/7.x/avataaars/svg?seed=" + r.getUserId()));
            }
            return dto;
        }).collect(Collectors.toList());

        // 7. 商品详情图
        List<ProductImage> detailImages = productImageMapper.findByProductIdAndType(productId, 2);

        // 8. 商品参数
        List<ProductDetail> details = productDetailMapper.findByProductId(productId);

        // 9. 商品标签
        List<ProductTag> tags = productTagMapper.findByProductId(productId);

        // 10. 商品属性（颜色、规格等）
        List<ProductAttribute> attributes = productAttributeMapper.findByProductId(productId);

        // 11. 商品SKU
        List<ProductSku> skus = productSkuMapper.findByProductId(productId);

        // 12. 收藏状态
        boolean isFavorited = false;
        if (userId != null) {
            UserFavorite favorite = userFavoriteMapper.findByUserIdAndProductId(userId, productId);
            isFavorited = favorite != null;
        }

        // 组装结果
        result.put("code", 200);
        result.put("message", "success");

        Map<String, Object> data = new HashMap<>();
        data.put("product", convertProductToDTO(product));
        data.put("carouselImages", carouselImages.stream().map(this::convertImageToDTO).collect(Collectors.toList()));
        data.put("merchant", merchant != null ? convertMerchantToDTO(merchant) : null);
        data.put("brandInfo", brandInfo != null ? convertBrandInfoToDTO(brandInfo, merchant) : null);
        data.put("groupInfo", groupInfo);
        data.put("reviews", reviewDtos);
        data.put("detailImages", detailImages.stream().map(this::convertImageToDTO).collect(Collectors.toList()));
        data.put("details", details.stream().map(this::convertDetailToDTO).collect(Collectors.toList()));
        data.put("tags", tags.stream().map(this::convertTagToDTO).collect(Collectors.toList()));
        data.put("attributes", attributes.stream().map(this::convertAttributeToDTO).collect(Collectors.toList()));
        data.put("skus", skus.stream().map(this::convertSkuToDTO).collect(Collectors.toList()));
        data.put("isFavorited", isFavorited);

        result.put("data", data);
        return result;
    }

    /**
     * 获取拼单信息（优先从Redis缓存，否则生成随机数据）
     */
    private Map<String, Object> getGroupOrderInfo(Long productId, Long merchantId) {
        String cacheKey = String.format(GROUP_CACHE_KEY, productId);

        // 尝试从Redis获取
        @SuppressWarnings("unchecked")
        Map<String, Object> cached = (Map<String, Object>) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached;
        }

        // 从数据库查询
        GroupOrder groupOrder = groupOrderMapper.findActiveByProductId(productId);
        Map<String, Object> groupInfo = new HashMap<>();

        if (groupOrder != null) {
            // 有真实拼单数据
            List<GroupOrderUser> users = groupOrderUserMapper.findByGroupOrderId(groupOrder.getId());

            groupInfo.put("groupId", groupOrder.getId());
            groupInfo.put("groupSize", groupOrder.getGroupSize());
            groupInfo.put("currentCount", groupOrder.getCurrentCount());
            groupInfo.put("groupPrice", groupOrder.getGroupPrice());
            groupInfo.put("expireSeconds", groupOrder.getExpireSeconds());
            groupInfo.put("status", groupOrder.getStatus());

            // 用户列表（最多显示2个）
            List<Map<String, Object>> userList = new ArrayList<>();
            int displayCount = Math.min(users.size(), 2);
            for (int i = 0; i < displayCount; i++) {
                GroupOrderUser user = users.get(i);
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("userName", user.getUserName());
                userMap.put("userAvatar", user.getUserAvatar());
                userList.add(userMap);
            }

            // 如果用户不足2个，补充随机假数据
            while (userList.size() < 2) {
                userList.add(generateFakeUser());
            }

            groupInfo.put("users", userList);
        } else {
            // 没有拼单数据，生成随机假数据
            int fakeCount = new Random().nextInt(150) + 50; // 50-200人
            groupInfo.put("groupId", null);
            groupInfo.put("groupSize", 2);
            groupInfo.put("currentCount", fakeCount);
            groupInfo.put("groupPrice", null); // 使用商品原价
            groupInfo.put("expireSeconds", 86400);
            groupInfo.put("status", 1);

            List<Map<String, Object>> userList = new ArrayList<>();
            userList.add(generateFakeUser());
            userList.add(generateFakeUser());
            groupInfo.put("users", userList);
        }

        // 缓存到Redis（5分钟）
        redisTemplate.opsForValue().set(cacheKey, groupInfo, 5, TimeUnit.MINUTES);

        return groupInfo;
    }

    /**
     * 生成随机假用户数据
     */
    private Map<String, Object> generateFakeUser() {
        String[] names = {"楚天装饰", "顺水兴洲", "酹月", "燕萍", "ZZX", "峰少", "阳光小筑", "花开富贵",
                "美食达人", "零食控", "健康生活", "口腔护理达人", "车友小李", "数码控", "水果爱好者",
                "健康饮食", "老四", "氰化呐", "小明", "益力多", "善解人意", "测试用户", "匿名用户"};
        Random random = new Random();
        String name = names[random.nextInt(names.length)];
        String seed = "fake" + System.currentTimeMillis() + random.nextInt(10000);

        Map<String, Object> user = new HashMap<>();
        user.put("userName", name);
        user.put("userAvatar", "https://api.dicebear.com/7.x/avataaars/svg?seed=" + seed);
        return user;
    }

    /**
     * 获取商品评论（优先商品评论，不足100条则补充商家所有商品评论）
     */
    private List<ProductReview> getProductReviews(Long productId, Long merchantId) {
        // 先获取该商品的评论
        List<ProductReview> productReviews = productReviewMapper.findByProductId(productId);

        // 如果评论数少于100条，补充商家其他商品的评论
        if (productReviews.size() < 100) {
            List<ProductReview> merchantReviews = productReviewMapper.findByMerchantId(merchantId);
            // 合并评论（去重）
            Set<Long> existingIds = productReviews.stream().map(ProductReview::getId).collect(Collectors.toSet());
            for (ProductReview review : merchantReviews) {
                if (!existingIds.contains(review.getId())) {
                    productReviews.add(review);
                }
            }
        }

        // 最多返回20条
        return productReviews.stream().limit(20).collect(Collectors.toList());
    }

    // DTO转换方法
    private Map<String, Object> convertProductToDTO(TermProducts product) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", product.getId());
        dto.put("productName", product.getProductName());
        dto.put("productCode", product.getProductCode());
        dto.put("categoryId", product.getCategoryId());
        dto.put("merchantId", product.getMerchantId());
        dto.put("price", product.getPrice());
        dto.put("originalPrice", product.getOriginalPrice());
        dto.put("stock", product.getStock());
        dto.put("sales", product.getSales());
        dto.put("mainImage", product.getMainImage());
        dto.put("brief", product.getBrief());
        return dto;
    }

    private Map<String, Object> convertImageToDTO(ProductImage image) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", image.getId());
        dto.put("imageUrl", image.getImageUrl());
        dto.put("sort", image.getSort());
        dto.put("imageType", image.getImageType());
        return dto;
    }

    private Map<String, Object> convertMerchantToDTO(Merchant merchant) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", merchant.getId());
        dto.put("merchantName", merchant.getMerchantName());
        dto.put("merchantLogo", merchant.getMerchantLogo());
        dto.put("merchantBrief", merchant.getMerchantBrief());
        return dto;
    }

    private Map<String, Object> convertBrandInfoToDTO(MerchantBrandInfo brandInfo, Merchant merchant) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("brandName", brandInfo.getBrandName());
        String brandLogo = brandInfo.getBrandLogo();
        if (brandLogo == null || brandLogo.isBlank()) {
            if (merchant != null && merchant.getMerchantLogo() != null && !merchant.getMerchantLogo().isBlank()) {
                brandLogo = merchant.getMerchantLogo();
            }
        }
        dto.put("brandLogo", brandLogo);
        dto.put("brandSlogan", brandInfo.getBrandSlogan());
        
        // 富文本格式的品牌介绍
        String introduction = buildRichTextIntroduction(brandInfo.getBrandStory());
        dto.put("introduction", introduction);
        
        // 特性列表（材质、性能、研发）
        List<Map<String, Object>> features = new ArrayList<>();
        features.add(createFeature("材质", brandInfo.getMaterialDesc()));
        features.add(createFeature("性能", brandInfo.getPerformanceDesc()));
        features.add(createFeature("研发", brandInfo.getRdDesc()));
        dto.put("features", features);
        
        // 其他字段保持不变
        dto.put("brandStory", brandInfo.getBrandStory());
        dto.put("brandIntro", brandInfo.getBrandIntro());
        dto.put("establishedYear", brandInfo.getEstablishedYear());
        dto.put("totalSales", brandInfo.getTotalSales());
        dto.put("goodReviewCount", brandInfo.getGoodReviewCount());
        dto.put("recentReviewCount", brandInfo.getRecentReviewCount());
        dto.put("recentGroupCount", brandInfo.getRecentGroupCount());
        dto.put("reviewerCount", brandInfo.getReviewerCount());
        dto.put("guaranteeTags", brandInfo.getGuaranteeTags() != null ? Arrays.asList(brandInfo.getGuaranteeTags().split("\\|")) : Collections.emptyList());
        dto.put("shopTags", brandInfo.getShopTags() != null ? Arrays.asList(brandInfo.getShopTags().split("\\|")) : Collections.emptyList());
        dto.put("materialDesc", brandInfo.getMaterialDesc());
        dto.put("performanceDesc", brandInfo.getPerformanceDesc());
        dto.put("rdDesc", brandInfo.getRdDesc());
        return dto;
    }
    
    /**
     * 构建富文本格式的品牌介绍（数据库中已存储富文本格式）
     */
    private String buildRichTextIntroduction(String brandStory) {
        if (brandStory == null || brandStory.isEmpty()) {
            return "<p>暂无品牌介绍</p>";
        }
        return brandStory;
    }
    
    /**
     * 创建特性项
     */
    private Map<String, Object> createFeature(String label, String value) {
        Map<String, Object> feature = new HashMap<>();
        feature.put("label", label);
        feature.put("value", value != null && !value.isEmpty() ? value : "暂无");
        return feature;
    }

    private Map<String, Object> convertReviewToDTO(ProductReview review) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", review.getId());
        dto.put("userId", review.getUserId());
        dto.put("userName", review.getUserName());
        dto.put("avatar", review.getAvatar());
        dto.put("rating", review.getRating());
        dto.put("content", review.getContent());
        dto.put("images", review.getImages());
        dto.put("helpfulCount", review.getHelpfulCount());
        dto.put("createdAt", review.getCreatedAt() != null ? review.getCreatedAt().format(TIME_FORMATTER) : null);
        return dto;
    }

    private Map<String, Object> convertDetailToDTO(ProductDetail detail) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("paramKey", detail.getParamKey());
        dto.put("paramValue", detail.getParamValue());
        return dto;
    }

    private Map<String, Object> convertTagToDTO(ProductTag tag) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("tagName", tag.getTagName());
        dto.put("tagColor", tag.getTagColor());
        return dto;
    }

    private Map<String, Object> convertAttributeToDTO(ProductAttribute attribute) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", attribute.getId());
        dto.put("productId", attribute.getProductId());
        dto.put("attrName", attribute.getAttrName());
        dto.put("attrValue", attribute.getAttrValue());
        dto.put("sort", attribute.getSort());
        return dto;
    }

    private Map<String, Object> convertSkuToDTO(ProductSku sku) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", sku.getId());
        dto.put("productId", sku.getProductId());
        dto.put("skuCode", sku.getSkuCode());
        dto.put("attributes", sku.getAttributes());
        dto.put("price", sku.getPrice());
        dto.put("originalPrice", sku.getOriginalPrice());
        dto.put("stock", sku.getStock());
        dto.put("sales", sku.getSales());
        dto.put("imageUrl", sku.getImageUrl());
        dto.put("status", sku.getStatus());
        return dto;
    }

    /**
     * 收藏/取消收藏商品
     */
    public Map<String, Object> toggleFavorite(Long userId, Long productId) {
        Map<String, Object> result = new HashMap<>();

        if (userId == null || productId == null) {
            result.put("code", 400);
            result.put("message", "参数错误");
            return result;
        }

        UserFavorite existing = userFavoriteMapper.findByUserIdAndProductId(userId, productId);

        if (existing != null) {
            // 取消收藏
            userFavoriteMapper.deleteByUserIdAndProductId(userId, productId);
            result.put("code", 200);
            result.put("message", "取消收藏成功");
            result.put("isFavorited", false);
        } else {
            // 添加收藏
            UserFavorite favorite = new UserFavorite();
            favorite.setUserId(userId);
            favorite.setProductId(productId);
            userFavoriteMapper.insert(favorite);
            result.put("code", 200);
            result.put("message", "收藏成功");
            result.put("isFavorited", true);
        }

        return result;
    }

    /**
     * 获取底部价格栏信息
     */
    public Map<String, Object> getBottomPriceBar(Long productId, Long userId) {
        Map<String, Object> result = new HashMap<>();

        TermProducts product = productMapper.selectById(productId);
        if (product == null) {
            result.put("code", 404);
            result.put("message", "商品不存在");
            return result;
        }

        // 获取拼单价
        GroupOrder groupOrder = groupOrderMapper.findActiveByProductId(productId);
        BigDecimal groupPrice = groupOrder != null ? groupOrder.getGroupPrice() : null;

        // 获取收藏状态
        boolean isFavorited = false;
        if (userId != null) {
            UserFavorite favorite = userFavoriteMapper.findByUserIdAndProductId(userId, productId);
            isFavorited = favorite != null;
        }

        result.put("code", 200);
        result.put("message", "success");

        Map<String, Object> data = new HashMap<>();
        data.put("productId", productId);
        data.put("price", product.getPrice());
        data.put("originalPrice", product.getOriginalPrice());
        data.put("groupPrice", groupPrice);
        data.put("isFavorited", isFavorited);
        data.put("stock", product.getStock());
        data.put("sales", product.getSales());

        result.put("data", data);
        return result;
    }

    /**
     * 获取用户收藏列表
     */
    public Map<String, Object> getUserFavorites(Long userId, int page, int pageSize) {
        Map<String, Object> result = new HashMap<>();

        if (userId == null) {
            result.put("code", 400);
            result.put("message", "参数错误");
            return result;
        }

        // 获取用户收藏记录
        List<UserFavorite> favorites = userFavoriteMapper.findByUserId(userId);
        
        // 分页处理
        int total = favorites.size();
        int totalPages = (int) Math.ceil((double) total / pageSize);
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, total);
        
        List<UserFavorite> pageFavorites = start < total ? favorites.subList(start, end) : new ArrayList<>();

        // 组装商品信息
        List<Map<String, Object>> productList = new ArrayList<>();
        for (UserFavorite favorite : pageFavorites) {
            TermProducts product = productMapper.selectById(favorite.getProductId());
            if (product != null) {
                Map<String, Object> productMap = convertProductToDTO(product);
                productMap.put("favoritedAt", favorite.getCreatedAt() != null ? favorite.getCreatedAt().format(TIME_FORMATTER) : null);
                productList.add(productMap);
            }
        }

        result.put("code", 200);
        result.put("message", "success");
        
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("totalPages", totalPages);
        data.put("page", page);
        data.put("pageSize", pageSize);
        data.put("products", productList);

        result.put("data", data);
        return result;
    }

    /**
     * 获取用户收藏数量
     */
    public Map<String, Object> getUserFavoriteCount(Long userId) {
        Map<String, Object> result = new HashMap<>();

        if (userId == null) {
            result.put("code", 400);
            result.put("message", "参数错误");
            return result;
        }

        int count = userFavoriteMapper.countByUserId(userId);

        result.put("code", 200);
        result.put("message", "success");
        result.put("data", count);

        return result;
    }
}
