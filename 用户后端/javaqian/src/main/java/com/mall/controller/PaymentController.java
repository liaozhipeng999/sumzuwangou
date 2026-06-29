package com.mall.controller;

import com.mall.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/create")
    public Map<String, Object> createPayment(@RequestBody Map<String, Object> request) {
        try {
            Long orderId = ((Number) request.get("orderId")).longValue();
            Long userId = ((Number) request.get("userId")).longValue();
            Integer payType = request.get("payType") != null ? ((Number) request.get("payType")).intValue() : 1;

            return paymentService.createPayment(orderId, userId, payType);
        } catch (RuntimeException e) {
            Map<String, Object> result = new HashMap<>();
            result.put("code", 400);
            result.put("message", e.getMessage());
            return result;
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("code", 500);
            result.put("message", "支付失败: " + e.getMessage());
            return result;
        }
    }

    @GetMapping("/check")
    public Map<String, Object> checkPayment(@RequestParam Long orderId) {
        return paymentService.checkPayment(orderId);
    }
}