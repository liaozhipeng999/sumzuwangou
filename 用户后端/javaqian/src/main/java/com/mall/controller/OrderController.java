package com.mall.controller;

import com.mall.entity.*;
import com.mall.mapper.*;
import com.mall.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserAddressMapper userAddressMapper;

    @Autowired
    private ProductSkuMapper productSkuMapper;

    @Autowired
    private ProductAttributeMapper productAttributeMapper;

    @Autowired
    private UserCouponMapper userCouponMapper;

    @Autowired
    private CouponMapper couponMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private MerchantMapper merchantMapper;

    @Autowired
    private ProductMapper productMapper;

    /**
     * 获取用户地址列表
     */
    @GetMapping("/addresses")
    public Map<String, Object> getUserAddresses(@RequestParam Long userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<UserAddress> addresses = userAddressMapper.findByUserId(userId);
            result.put("code", 200);
            result.put("message", "success");
            result.put("data", addresses);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "获取地址失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 获取商品规格列表
     */
    @GetMapping("/product/skus")
    public Map<String, Object> getProductSkus(@RequestParam Long productId) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<ProductSku> skus = productSkuMapper.findByProductId(productId);
            List<Map<String, String>> attributes = productAttributeMapper.findAttributeGroups(productId);
            
            Map<String, Object> data = new HashMap<>();
            data.put("skus", skus);
            data.put("attributeGroups", attributes);
            
            result.put("code", 200);
            result.put("message", "success");
            result.put("data", data);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "获取规格失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 获取可用优惠券
     */
    @GetMapping("/coupons")
    public Map<String, Object> getAvailableCoupons(@RequestParam Long userId, @RequestParam(required = false) BigDecimal amount) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<UserCoupon> userCoupons = userCouponMapper.findByUserId(userId);
            List<Map<String, Object>> availableCoupons = new ArrayList<>();
            
            for (UserCoupon uc : userCoupons) {
                if (uc.getStatus() != 1) continue;
                
                Coupon coupon = couponMapper.selectById(uc.getCouponId());
                if (coupon == null || coupon.getStatus() != 1) continue;
                
                LocalDateTime now = LocalDateTime.now();
                if (now.isBefore(coupon.getStartTime()) || now.isAfter(coupon.getEndTime())) continue;
                
                if (amount != null && amount.compareTo(coupon.getMinAmount()) < 0) continue;
                
                Map<String, Object> couponInfo = new HashMap<>();
                couponInfo.put("userCouponId", uc.getId());
                couponInfo.put("couponId", coupon.getId());
                couponInfo.put("name", coupon.getCouponName());
                couponInfo.put("type", coupon.getCouponType());
                couponInfo.put("discountValue", coupon.getDiscountValue());
                couponInfo.put("minAmount", coupon.getMinAmount());
                couponInfo.put("maxDiscount", coupon.getDiscountValue());
                couponInfo.put("startTime", coupon.getStartTime());
                couponInfo.put("endTime", coupon.getEndTime());
                
                availableCoupons.add(couponInfo);
            }
            
            result.put("code", 200);
            result.put("message", "success");
            result.put("data", availableCoupons);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "获取优惠券失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 计算订单价格
     */
    @PostMapping("/calculate")
    public Map<String, Object> calculatePrice(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Map<String, Object>> items = (List<Map<String, Object>>) request.get("items");
            Long couponId = request.get("couponId") != null ? ((Number) request.get("couponId")).longValue() : null;
            
            BigDecimal totalPrice = orderService.calculateTotalPrice(items);
            BigDecimal couponAmount = orderService.calculateDiscount(totalPrice, couponId);
            
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
            
            Map<String, Object> data = new HashMap<>();
            data.put("totalPrice", totalPrice);
            data.put("discountAmount", discountAmount);
            data.put("couponAmount", couponAmount);
            data.put("payAmount", payAmount);
            
            result.put("code", 200);
            result.put("message", "success");
            result.put("data", data);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "计算失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 创建订单
     */
    @PostMapping("/create")
    public Map<String, Object> createOrder(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        try {
            Long userId = ((Number) request.get("userId")).longValue();
            Long addressId = ((Number) request.get("addressId")).longValue();
            List<Map<String, Object>> items = (List<Map<String, Object>>) request.get("items");
            Long couponId = request.get("couponId") != null ? ((Number) request.get("couponId")).longValue() : null;
            
            Order order = orderService.createOrder(userId, addressId, items, couponId);
            
            Map<String, Object> data = new HashMap<>();
            data.put("orderId", order.getId());
            data.put("orderNo", order.getOrderNo());
            data.put("totalAmount", order.getTotalAmount());
            data.put("discountAmount", order.getDiscountAmount());
            data.put("couponAmount", order.getCouponAmount());
            data.put("payAmount", order.getPayAmount());
            
            result.put("code", 200);
            result.put("message", "下单成功");
            result.put("data", data);
        } catch (RuntimeException e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "下单失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/detail")
    public Map<String, Object> getOrderDetail(@RequestParam Long orderId) {
        Map<String, Object> result = new HashMap<>();
        try {
            Order order = orderService.getOrderById(orderId);
            if (order == null) {
                result.put("code", 404);
                result.put("message", "订单不存在");
                return result;
            }
            
            UserAddress address = userAddressMapper.selectById(order.getAddressId());
            
            List<Map<String, Object>> items = new ArrayList<>();
            List<OrderItem> orderItems = orderItemMapper.findByOrderId(orderId);
            
            for (OrderItem item : orderItems) {
                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("id", item.getId());
                itemMap.put("productId", item.getProductId());
                itemMap.put("skuId", item.getSkuId());
                itemMap.put("productName", item.getProductName());
                itemMap.put("skuAttributes", item.getSkuAttributes());
                itemMap.put("price", item.getPrice());
                itemMap.put("originalPrice", item.getOriginalPrice());
                itemMap.put("quantity", item.getQuantity());
                itemMap.put("imageUrl", item.getImageUrl());
                items.add(itemMap);
            }
            
            Map<String, Object> data = new HashMap<>();
            data.put("order", order);
            data.put("address", address);
            data.put("items", items);
            
            result.put("code", 200);
            result.put("message", "success");
            result.put("data", data);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "获取订单失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 获取用户订单列表（包含商家信息和商品详情）
     */
    @GetMapping("/list")
    public Map<String, Object> getOrderList(@RequestParam Long userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Order> orders = orderService.getOrdersByUserId(userId);
            
            // 为每个订单添加详细信息
            List<Map<String, Object>> orderListWithDetails = new ArrayList<>();
            for (Order order : orders) {
                Map<String, Object> orderMap = new HashMap<>();
                orderMap.put("id", order.getId());
                orderMap.put("orderNo", order.getOrderNo());
                orderMap.put("userId", order.getUserId());
                orderMap.put("merchantId", order.getMerchantId());
                orderMap.put("addressId", order.getAddressId());
                orderMap.put("totalAmount", order.getTotalAmount());
                orderMap.put("discountAmount", order.getDiscountAmount());
                orderMap.put("couponAmount", order.getCouponAmount());
                orderMap.put("payAmount", order.getPayAmount());
                orderMap.put("status", order.getStatus());
                orderMap.put("payTime", order.getPayTime());
                orderMap.put("shipTime", order.getShipTime());
                orderMap.put("receiveTime", order.getReceiveTime());
                orderMap.put("cancelTime", order.getCancelTime());
                orderMap.put("shipCompany", order.getShipCompany());
                orderMap.put("shipNo", order.getShipNo());
                orderMap.put("shipRemark", order.getShipRemark());
                orderMap.put("createdAt", order.getCreatedAt());
                orderMap.put("updatedAt", order.getUpdatedAt());
                
                // 获取订单项及商家信息
                List<OrderItem> items = orderItemMapper.findByOrderId(order.getId());
                List<Map<String, Object>> itemsWithMerchant = new ArrayList<>();
                Merchant firstMerchant = null; // 记录第一个商品的商家作为订单的主要商家
                
                for (OrderItem item : items) {
                    Map<String, Object> itemMap = new HashMap<>();
                    itemMap.put("id", item.getId());
                    itemMap.put("productId", item.getProductId());
                    itemMap.put("skuId", item.getSkuId());
                    itemMap.put("productName", item.getProductName());
                    itemMap.put("skuAttributes", item.getSkuAttributes());
                    itemMap.put("price", item.getPrice());
                    itemMap.put("originalPrice", item.getOriginalPrice());
                    itemMap.put("quantity", item.getQuantity());
                    itemMap.put("imageUrl", item.getImageUrl());
                    
                    // 获取商家信息
                    try {
                        var product = productMapper.selectById(item.getProductId());
                        if (product != null && product.getMerchantId() != null && product.getMerchantId() > 0) {
                            Merchant merchant = merchantMapper.selectById(product.getMerchantId());
                            if (merchant != null) {
                                Map<String, Object> merchantInfo = new HashMap<>();
                                merchantInfo.put("id", merchant.getId());
                                merchantInfo.put("name", merchant.getMerchantName());
                                merchantInfo.put("logo", merchant.getMerchantLogo());
                                merchantInfo.put("brief", merchant.getMerchantBrief());
                                itemMap.put("merchant", merchantInfo);
                                
                                // 记录第一个商家作为订单的主要商家
                                if (firstMerchant == null) {
                                    firstMerchant = merchant;
                                }
                            }
                        }
                    } catch (Exception e) {
                        // 如果获取商家信息失败，不影响主流程
                    }
                    
                    itemsWithMerchant.add(itemMap);
                }
                
                orderMap.put("items", itemsWithMerchant);
                
                // 添加订单级别的主要商家信息（取第一个商品的商家）
                if (firstMerchant != null) {
                    Map<String, Object> mainMerchantInfo = new HashMap<>();
                    mainMerchantInfo.put("id", firstMerchant.getId());
                    mainMerchantInfo.put("name", firstMerchant.getMerchantName());
                    mainMerchantInfo.put("logo", firstMerchant.getMerchantLogo());
                    orderMap.put("merchant", mainMerchantInfo);
                }
                
                orderListWithDetails.add(orderMap);
            }
            
            result.put("code", 200);
            result.put("message", "success");
            result.put("data", orderListWithDetails);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "获取订单失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 更新订单状态
     */
    @PutMapping("/status")
    public Map<String, Object> updateOrderStatus(@RequestParam Long orderId, @RequestParam Integer status) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = orderService.updateOrderStatus(orderId, status);
            if (success) {
                result.put("code", 200);
                result.put("message", "状态更新成功");
            } else {
                result.put("code", 400);
                result.put("message", "订单不存在");
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "更新失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 订单发货
     */
    @PostMapping("/ship")
    public Map<String, Object> shipOrder(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        try {
            Long orderId = ((Number) request.get("orderId")).longValue();
            String shipCompany = request.get("shipCompany") != null ? request.get("shipCompany").toString() : null;
            String shipNo = request.get("shipNo") != null ? request.get("shipNo").toString() : null;
            String shipRemark = request.get("shipRemark") != null ? request.get("shipRemark").toString() : null;

            if (shipCompany == null || shipNo == null) {
                result.put("code", 400);
                result.put("message", "物流公司和物流单号不能为空");
                return result;
            }

            boolean success = orderService.shipOrder(orderId, shipCompany, shipNo, shipRemark);
            if (success) {
                result.put("code", 200);
                result.put("message", "发货成功");
            } else {
                result.put("code", 404);
                result.put("message", "订单不存在");
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "发货失败: " + e.getMessage());
        }
        return result;
    }
}