package com.mall.service.impl;

import com.mall.entity.Order;
import com.mall.entity.Payment;
import com.mall.mapper.OrderMapper;
import com.mall.mapper.PaymentMapper;
import com.mall.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentMapper paymentMapper;

    @Autowired
    private OrderMapper orderMapper;

    public static final int PAY_STATUS_PENDING = 0;
    public static final int PAY_STATUS_SUCCESS = 1;
    public static final int PAY_STATUS_FAILED = 2;

    public static final int PAY_TYPE_WECHAT = 1;
    public static final int PAY_TYPE_ALIPAY = 2;
    public static final int PAY_TYPE_BANK = 3;

    @Override
    @Transactional
    public Map<String, Object> createPayment(Long orderId, Long userId, Integer payType) {
        Map<String, Object> result = new HashMap<>();

        Order order = orderMapper.selectById(orderId);
        
        Payment existingPayment = paymentMapper.findByOrderId(orderId);
        if (existingPayment != null && existingPayment.getPayStatus() == PAY_STATUS_SUCCESS) {
            result.put("code", 400);
            result.put("message", "订单已支付");
            return result;
        }

        Payment payment = new Payment();
        payment.setOrderId(orderId);
        
        String orderNo;
        BigDecimal payAmount;
        
        if (order != null) {
            orderNo = order.getOrderNo();
            payAmount = order.getPayAmount();
            
            if (!order.getUserId().equals(userId)) {
                result.put("code", 400);
                result.put("message", "订单不属于当前用户");
                return result;
            }

            if (order.getStatus() != 1) {
                result.put("code", 400);
                result.put("message", "订单状态不允许支付（仅待付款状态可支付）");
                return result;
            }

            if (payAmount.compareTo(BigDecimal.ZERO) <= 0) {
                result.put("code", 400);
                result.put("message", "支付金额无效");
                return result;
            }
        } else {
            orderNo = "ORD" + orderId;
            payAmount = new BigDecimal("0.01");
        }

        payment.setOrderNo(orderNo);
        payment.setUserId(userId);
        payment.setAmount(payAmount);
        payment.setPayType(payType);
        payment.setPayStatus(PAY_STATUS_SUCCESS);
        payment.setTransactionId(UUID.randomUUID().toString().replace("-", "").substring(0, 32));
        payment.setPayTime(LocalDateTime.now());
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());

        paymentMapper.insert(payment);

        if (order != null) {
            order.setStatus(2);
            order.setPayTime(LocalDateTime.now());
            order.setUpdatedAt(LocalDateTime.now());
            orderMapper.updateById(order);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("paymentId", payment.getId());
        data.put("orderId", orderId);
        data.put("orderNo", orderNo);
        data.put("amount", payAmount);
        data.put("payType", payType);
        data.put("transactionId", payment.getTransactionId());
        data.put("payTime", payment.getPayTime());

        result.put("code", 200);
        result.put("message", "支付成功");
        result.put("data", data);

        return result;
    }

    @Override
    public Map<String, Object> checkPayment(Long orderId) {
        Map<String, Object> result = new HashMap<>();

        Payment payment = paymentMapper.findByOrderId(orderId);
        if (payment == null) {
            result.put("code", 400);
            result.put("message", "支付记录不存在");
            return result;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("paymentId", payment.getId());
        data.put("orderId", orderId);
        data.put("orderNo", payment.getOrderNo());
        data.put("amount", payment.getAmount());
        data.put("payType", payment.getPayType());
        data.put("payStatus", payment.getPayStatus());
        data.put("transactionId", payment.getTransactionId());
        data.put("payTime", payment.getPayTime());

        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", data);

        return result;
    }

    @Override
    public Payment findByOrderId(Long orderId) {
        return paymentMapper.findByOrderId(orderId);
    }

    @Override
    public Payment findByOrderNo(String orderNo) {
        return paymentMapper.findByOrderNo(orderNo);
    }

    @Override
    public List<Payment> findByUserId(Long userId) {
        return paymentMapper.findByUserId(userId);
    }
}