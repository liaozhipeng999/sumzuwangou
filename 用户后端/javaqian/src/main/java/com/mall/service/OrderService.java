package com.mall.service;

import com.mall.entity.Order;
import com.mall.entity.OrderItem;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface OrderService {

    Order createOrder(Long userId, Long addressId, List<Map<String, Object>> items, Long couponId);

    BigDecimal calculateTotalPrice(List<Map<String, Object>> items);

    BigDecimal calculateDiscount(BigDecimal totalPrice, Long couponId);

    Order getOrderById(Long id);

    Order getOrderByNo(String orderNo);

    List<Order> getOrdersByUserId(Long userId);

    boolean updateOrderStatus(Long orderId, Integer status);

    boolean shipOrder(Long orderId, String shipCompany, String shipNo, String shipRemark);
}