package com.mall.service;

import com.mall.entity.Payment;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface PaymentService {

    Map<String, Object> createPayment(Long orderId, Long userId, Integer payType);

    Map<String, Object> checkPayment(Long orderId);

    Payment findByOrderId(Long orderId);

    Payment findByOrderNo(String orderNo);

    List<Payment> findByUserId(Long userId);
}