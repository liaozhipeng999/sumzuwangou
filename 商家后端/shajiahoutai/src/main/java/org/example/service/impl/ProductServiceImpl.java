package org.example.service.impl;

import org.example.dto.ProductAddDTO;
import org.example.dto.ProductListDTO;
import org.example.dto.ProductUpdateDTO;
import org.example.entity.TermProduct;
import org.example.entity.TermProductTag;
import org.example.mapper.ProductMapper;
import org.example.service.ProductService;
import org.example.service.ProductTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {
    
    @Autowired
    private ProductMapper productMapper;
    
    @Autowired
    private ProductTagService productTagService;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    private static final String PRODUCT_CODE_PREFIX = "SKU";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final String REDIS_PRODUCT_KEY_PREFIX = "product:info:";
    private static final String REDIS_MERCHANT_PRODUCTS_KEY_PREFIX = "merchant:products:";
    private static final long REDIS_EXPIRE_TIME = 30; // 缓存过期时间（分钟）
    
    /**
     * 生成商品编码
     */
    private String generateProductCode() {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        int random = new Random().nextInt(10000);
        return PRODUCT_CODE_PREFIX + timestamp + String.format("%04d", random);
    }
    
    @Override
    @Transactional
    public TermProduct addProduct(ProductAddDTO productAddDTO) {
        TermProduct product = new TermProduct();
        
        // 设置基本信息
        product.setMerchantId(productAddDTO.getMerchantId());
        product.setProductName(productAddDTO.getProductName());
        // 自动生成商品编码
        product.setProductCode(generateProductCode());
        product.setCategoryId(productAddDTO.getCategoryId());
        product.setPrice(productAddDTO.getPrice());
        product.setOriginalPrice(productAddDTO.getOriginalPrice());
        product.setStock(productAddDTO.getStock());
        product.setSales(0); // 初始销量为0
        product.setMainImage(productAddDTO.getMainImage());
        product.setBrief(productAddDTO.getBrief());
        
        // 设置默认值
        product.setStatus(productAddDTO.getStatus() != null ? productAddDTO.getStatus() : 1);
        product.setSort(productAddDTO.getSort() != null ? productAddDTO.getSort() : 0);
        product.setIsHot(productAddDTO.getIsHot() != null ? productAddDTO.getIsHot() : 0);
        product.setIsNew(productAddDTO.getIsNew() != null ? productAddDTO.getIsNew() : 0);
        
        // 保存商品到MySQL
        productMapper.insert(product);
        
        // 保存商品标签
        saveProductTags(product.getId(), productAddDTO.getTags());
        
        // 将新上架商品写入Redis缓存
        String redisKey = REDIS_PRODUCT_KEY_PREFIX + product.getId();
        redisTemplate.opsForValue().set(redisKey, product, REDIS_EXPIRE_TIME, TimeUnit.MINUTES);
        
        // 清除商家商品列表缓存（触发重新加载）
        String merchantProductsKey = REDIS_MERCHANT_PRODUCTS_KEY_PREFIX + product.getMerchantId();
        redisTemplate.delete(merchantProductsKey);
        
        return product;
    }
    
    /**
     * 保存商品标签
     */
    private void saveProductTags(Long productId, List<ProductAddDTO.TagDTO> tagDTOs) {
        if (tagDTOs == null || tagDTOs.isEmpty()) {
            return;
        }
        
        List<TermProductTag> tags = new ArrayList<>();
        for (ProductAddDTO.TagDTO tagDTO : tagDTOs) {
            if (tagDTO.getTagName() != null && !tagDTO.getTagName().trim().isEmpty()) {
                TermProductTag tag = new TermProductTag();
                tag.setTagName(tagDTO.getTagName().trim());
                tag.setTagColor(tagDTO.getTagColor() != null ? tagDTO.getTagColor() : "#333333");
                tags.add(tag);
            }
        }
        
        if (!tags.isEmpty()) {
            productTagService.saveTags(productId, tags);
        }
    }
    
    @Override
    public TermProduct getById(Long id) {
        String redisKey = REDIS_PRODUCT_KEY_PREFIX + id;
        
        // 先从Redis获取
        Object cachedProduct = redisTemplate.opsForValue().get(redisKey);
        if (cachedProduct != null && cachedProduct instanceof TermProduct) {
            return (TermProduct) cachedProduct;
        }
        
        // Redis未命中，从MySQL查询
        TermProduct product = productMapper.selectById(id);
        
        // 如果查询到，写入Redis缓存
        if (product != null) {
            redisTemplate.opsForValue().set(redisKey, product, REDIS_EXPIRE_TIME, TimeUnit.MINUTES);
        }
        
        return product;
    }
    
    @Override
    public List<ProductListDTO> getProductsByMerchantId(Long merchantId) {
        String redisKey = REDIS_MERCHANT_PRODUCTS_KEY_PREFIX + merchantId;
        
        // 先从Redis获取完整的商品列表（含标签）
        Object cachedList = redisTemplate.opsForValue().get(redisKey);
        if (cachedList != null && cachedList instanceof List) {
            @SuppressWarnings("unchecked")
            List<ProductListDTO> cachedProducts = (List<ProductListDTO>) cachedList;
            return cachedProducts;
        }
        
        // Redis未命中，从MySQL查询
        List<TermProduct> products = productMapper.selectByMerchantId(merchantId);
        
        if (products == null || products.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 转换为DTO并关联标签
        List<ProductListDTO> result = products.stream()
                .map(this::convertToProductListDTO)
                .collect(Collectors.toList());
        
        // 写入Redis缓存（30分钟过期）
        redisTemplate.opsForValue().set(redisKey, result, REDIS_EXPIRE_TIME, TimeUnit.MINUTES);
        
        return result;
    }
    
    @Override
    @Transactional
    public TermProduct updateProduct(ProductUpdateDTO productUpdateDTO) {
        // 先查询商品是否存在
        TermProduct product = productMapper.selectById(productUpdateDTO.getId());
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        
        // 更新商品信息
        product.setProductName(productUpdateDTO.getProductName());
        product.setCategoryId(productUpdateDTO.getCategoryId());
        product.setPrice(productUpdateDTO.getPrice());
        product.setOriginalPrice(productUpdateDTO.getOriginalPrice());
        product.setStock(productUpdateDTO.getStock());
        product.setMainImage(productUpdateDTO.getMainImage());
        product.setBrief(productUpdateDTO.getBrief());
        
        if (productUpdateDTO.getStatus() != null) {
            product.setStatus(productUpdateDTO.getStatus());
        }
        if (productUpdateDTO.getSort() != null) {
            product.setSort(productUpdateDTO.getSort());
        }
        if (productUpdateDTO.getIsHot() != null) {
            product.setIsHot(productUpdateDTO.getIsHot());
        }
        if (productUpdateDTO.getIsNew() != null) {
            product.setIsNew(productUpdateDTO.getIsNew());
        }
        
        // 更新到MySQL
        productMapper.updateById(product);
        
        // 更新标签（先删除原有标签，再插入新标签）
        updateProductTags(product.getId(), productUpdateDTO.getTags());
        
        // 更新Redis缓存
        String productKey = REDIS_PRODUCT_KEY_PREFIX + product.getId();
        redisTemplate.opsForValue().set(productKey, product, REDIS_EXPIRE_TIME, TimeUnit.MINUTES);
        
        // 清除商家商品列表缓存（触发重新加载）
        String merchantProductsKey = REDIS_MERCHANT_PRODUCTS_KEY_PREFIX + product.getMerchantId();
        redisTemplate.delete(merchantProductsKey);
        
        return product;
    }
    
    @Override
    @Transactional
    public TermProduct downProduct(Long id) {
        // 先查询商品是否存在
        TermProduct product = productMapper.selectById(id);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        
        // 将商品状态改为下架(0)
        product.setStatus(0);
        productMapper.updateById(product);
        
        // 更新Redis缓存
        String productKey = REDIS_PRODUCT_KEY_PREFIX + id;
        redisTemplate.opsForValue().set(productKey, product, REDIS_EXPIRE_TIME, TimeUnit.MINUTES);
        
        // 清除商家商品列表缓存（触发重新加载）
        String merchantProductsKey = REDIS_MERCHANT_PRODUCTS_KEY_PREFIX + product.getMerchantId();
        redisTemplate.delete(merchantProductsKey);
        
        return product;
    }
    
    @Override
    @Transactional
    public TermProduct upProduct(Long id) {
        // 先查询商品是否存在
        TermProduct product = productMapper.selectById(id);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        
        // 将商品状态改为上架(1)
        product.setStatus(1);
        productMapper.updateById(product);
        
        // 更新Redis缓存
        String productKey = REDIS_PRODUCT_KEY_PREFIX + id;
        redisTemplate.opsForValue().set(productKey, product, REDIS_EXPIRE_TIME, TimeUnit.MINUTES);
        
        // 清除商家商品列表缓存（触发重新加载）
        String merchantProductsKey = REDIS_MERCHANT_PRODUCTS_KEY_PREFIX + product.getMerchantId();
        redisTemplate.delete(merchantProductsKey);
        
        return product;
    }
    
    @Override
    @Transactional
    public void deleteProduct(Long id) {
        // 先查询商品是否存在
        TermProduct product = productMapper.selectById(id);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        
        // 删除商品标签
        productTagService.deleteTagsByProductId(id);
        
        // 删除商品
        productMapper.deleteById(id);
        
        // 清除Redis缓存
        String productKey = REDIS_PRODUCT_KEY_PREFIX + id;
        redisTemplate.delete(productKey);
        
        // 清除商家商品列表缓存（触发重新加载）
        String merchantProductsKey = REDIS_MERCHANT_PRODUCTS_KEY_PREFIX + product.getMerchantId();
        redisTemplate.delete(merchantProductsKey);
    }
    
    /**
     * 更新商品标签（先删后增）
     */
    private void updateProductTags(Long productId, List<ProductUpdateDTO.TagDTO> tagDTOs) {
        // 删除原有标签
        productTagService.deleteTagsByProductId(productId);
        
        // 添加新标签
        if (tagDTOs == null || tagDTOs.isEmpty()) {
            return;
        }
        
        List<TermProductTag> tags = new ArrayList<>();
        for (ProductUpdateDTO.TagDTO tagDTO : tagDTOs) {
            if (tagDTO.getTagName() != null && !tagDTO.getTagName().trim().isEmpty()) {
                TermProductTag tag = new TermProductTag();
                tag.setTagName(tagDTO.getTagName().trim());
                tag.setTagColor(tagDTO.getTagColor() != null ? tagDTO.getTagColor() : "#333333");
                tags.add(tag);
            }
        }
        
        if (!tags.isEmpty()) {
            productTagService.saveTags(productId, tags);
        }
    }
    
    /**
     * 转换为ProductListDTO并关联标签
     */
    private ProductListDTO convertToProductListDTO(TermProduct product) {
        ProductListDTO dto = new ProductListDTO();
        
        dto.setId(product.getId());
        dto.setMerchantId(product.getMerchantId());
        dto.setProductName(product.getProductName());
        dto.setProductCode(product.getProductCode());
        dto.setCategoryId(product.getCategoryId());
        dto.setPrice(product.getPrice());
        dto.setOriginalPrice(product.getOriginalPrice());
        dto.setStock(product.getStock());
        dto.setSales(product.getSales());
        dto.setMainImage(product.getMainImage());
        dto.setBrief(product.getBrief());
        dto.setStatus(product.getStatus());
        dto.setSort(product.getSort());
        dto.setIsHot(product.getIsHot());
        dto.setIsNew(product.getIsNew());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());
        
        // 获取商品标签
        List<TermProductTag> tags = productTagService.getTagsByProductId(product.getId());
        if (tags != null && !tags.isEmpty()) {
            List<ProductListDTO.TagDTO> tagDTOs = tags.stream()
                    .map(tag -> {
                        ProductListDTO.TagDTO tagDTO = new ProductListDTO.TagDTO();
                        tagDTO.setTagName(tag.getTagName());
                        tagDTO.setTagColor(tag.getTagColor());
                        return tagDTO;
                    })
                    .collect(Collectors.toList());
            dto.setTags(tagDTOs);
        } else {
            dto.setTags(new ArrayList<>());
        }
        
        return dto;
    }
}