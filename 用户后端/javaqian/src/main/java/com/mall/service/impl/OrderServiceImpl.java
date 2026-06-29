package com.mall.service.impl;

import com.mall.entity.*;
import com.mall.mapper.*;
import com.mall.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private ProductSkuMapper productSkuMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private UserCouponMapper userCouponMapper;

    @Autowired
    private CouponMapper couponMapper;

    @Autowired
    private UserAddressMapper userAddressMapper;

    @Override
    @Transactional
    public Order createOrder(Long userId, Long addressId, List<Map<String, Object>> items, Long couponId) {
        UserAddress address = userAddressMapper.selectById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new RuntimeException("地址不存在或不属于当前用户");
        }

        BigDecimal totalPrice = calculateTotalPrice(items);
        BigDecimal couponAmount = BigDecimal.ZERO;

        if (couponId != null) {
            couponAmount = calculateDiscount(totalPrice, couponId);
            if (couponAmount.compareTo(totalPrice) > 0) {
                couponAmount = totalPrice;
            }
        }

        BigDecimal discountAmount = BigDecimal.ZERO;
        for (Map<String, Object> item : items) {
            Long skuId = ((Number) item.get("skuId")).longValue();
            Integer quantity = ((Number) item.get("quantity")).intValue();
            ProductSku sku = productSkuMapper.selectById(skuId);
            if (sku != null) {
                BigDecimal skuDiscount = sku.getOriginalPrice().subtract(sku.getPrice()).multiply(BigDecimal.valueOf(quantity));
                discountAmount = discountAmount.add(skuDiscount);
            }
        }

        BigDecimal payAmount = totalPrice.subtract(discountAmount).subtract(couponAmount);
        if (payAmount.compareTo(BigDecimal.ZERO) < 0) {
            payAmount = BigDecimal.ZERO;
        }

        Long firstMerchantId = null;
        for (Map<String, Object> item : items) {
            Long skuId = ((Number) item.get("skuId")).longValue();
            Integer quantity = ((Number) item.get("quantity")).intValue();
            ProductSku sku = productSkuMapper.selectById(skuId);
            if (sku == null) {
                var productSkus = productSkuMapper.findByProductId(skuId);
                if (productSkus != null && !productSkus.isEmpty()) {
                    throw new RuntimeException(
                        String.format("错误的SKU ID：%d 是商品ID而非SKU ID。该商品的可用SKU ID包括：%s", 
                            skuId,
                            productSkus.stream()
                                .map(s -> String.valueOf(s.getId()))
                                .limit(5)
                                .collect(java.util.stream.Collectors.joining(", ")) +
                            (productSkus.size() > 5 ? " 等" : "")
                        )
                    );
                }
                throw new RuntimeException("商品规格不存在（SKU ID: " + skuId + "）");
            }
            if (sku.getStock() < quantity) {
                throw new RuntimeException("库存不足，当前库存：" + sku.getStock() + "，需要数量：" + quantity);
            }
            sku.setStock(sku.getStock() - quantity);
            sku.setSales(sku.getSales() + quantity);
            productSkuMapper.updateById(sku);

            if (firstMerchantId == null) {
                TermProducts product = productMapper.selectById(sku.getProductId());
                if (product != null) {
                    firstMerchantId = product.getMerchantId();
                }
            }
        }

        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setMerchantId(firstMerchantId);
        order.setAddressId(addressId);
        order.setTotalAmount(totalPrice);
        order.setDiscountAmount(discountAmount);
        order.setCouponAmount(couponAmount);
        order.setPayAmount(payAmount);
        order.setStatus(1);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        orderMapper.insert(order);

        for (Map<String, Object> item : items) {
            Long skuId = ((Number) item.get("skuId")).longValue();
            Integer quantity = ((Number) item.get("quantity")).intValue();
            ProductSku sku = productSkuMapper.selectById(skuId);

            String productName = sku.getAttributesMap().getOrDefault("name", null);
            if (productName == null || productName.isEmpty() || "商品".equals(productName)) {
                TermProducts product = productMapper.selectById(sku.getProductId());
                if (product != null) {
                    productName = product.getProductName();
                }
            }
            if (productName == null || productName.isEmpty()) {
                productName = "商品";
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(sku.getProductId());
            orderItem.setSkuId(skuId);
            orderItem.setProductName(productName);
            orderItem.setSkuAttributes(sku.getAttributes());
            orderItem.setPrice(sku.getPrice());
            orderItem.setOriginalPrice(sku.getOriginalPrice());
            orderItem.setQuantity(quantity);
            orderItem.setImageUrl(sku.getImageUrl());
            orderItem.setCreatedAt(LocalDateTime.now());
            orderItemMapper.insert(orderItem);
        }

        if (couponId != null) {
            UserCoupon userCoupon = userCouponMapper.selectById(couponId);
            if (userCoupon != null) {
                userCoupon.setStatus(2);
                userCoupon.setUsedTime(LocalDateTime.now());
                userCoupon.setOrderId(order.getId());
                userCouponMapper.updateById(userCoupon);
            }
        }

        return order;
    }

    @Override
    public BigDecimal calculateTotalPrice(List<Map<String, Object>> items) {
        BigDecimal total = BigDecimal.ZERO;
        for (Map<String, Object> item : items) {
            Long skuId = ((Number) item.get("skuId")).longValue();
            Integer quantity = ((Number) item.get("quantity")).intValue();
            ProductSku sku = productSkuMapper.selectById(skuId);
            if (sku != null) {
                total = total.add(sku.getPrice().multiply(BigDecimal.valueOf(quantity)));
            }
        }
        return total;
    }

    @Override
    public BigDecimal calculateDiscount(BigDecimal totalPrice, Long couponId) {
        if (couponId == null) {
            return BigDecimal.ZERO;
        }

        UserCoupon userCoupon = userCouponMapper.selectById(couponId);
        if (userCoupon == null || userCoupon.getStatus() != 1) {
            return BigDecimal.ZERO;
        }

        Coupon coupon = couponMapper.selectById(userCoupon.getCouponId());
        if (coupon == null || coupon.getStatus() != 1) {
            return BigDecimal.ZERO;
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(coupon.getStartTime()) || now.isAfter(coupon.getEndTime())) {
            return BigDecimal.ZERO;
        }

        if (totalPrice.compareTo(coupon.getMinAmount()) < 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal discount = BigDecimal.ZERO;
        switch (coupon.getCouponType()) {
            case 1:
                discount = coupon.getDiscountValue();
                break;
            case 2:
                discount = totalPrice.multiply(coupon.getDiscountValue()).divide(BigDecimal.valueOf(100));
                break;
            case 3:
                discount = coupon.getDiscountValue();
                break;
        }

        return discount;
    }

    @Override
    public Order getOrderById(Long id) {
        return orderMapper.selectById(id);
    }

    @Override
    public Order getOrderByNo(String orderNo) {
        return orderMapper.findByOrderNo(orderNo);
    }

    @Override
    public List<Order> getOrdersByUserId(Long userId) {
        return orderMapper.findByUserId(userId);
    }

    @Override
    public boolean updateOrderStatus(Long orderId, Integer status) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            return false;
        }
        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        
        if (status == 2) {
            order.setPayTime(LocalDateTime.now());
        } else if (status == 3) {
            order.setShipTime(LocalDateTime.now());
        } else if (status == 4) {
            order.setReceiveTime(LocalDateTime.now());
        } else if (status == 5) {
            order.setCancelTime(LocalDateTime.now());
        }
        
        orderMapper.updateById(order);
        return true;
    }

    @Override
    public boolean shipOrder(Long orderId, String shipCompany, String shipNo, String shipRemark) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            return false;
        }
        order.setStatus(3);
        order.setShipCompany(shipCompany);
        order.setShipNo(shipNo);
        order.setShipRemark(shipRemark);
        order.setShipTime(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        orderMapper.updateById(order);
        return true;
    }

    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "ORD" + timestamp + uuid;
    }
}