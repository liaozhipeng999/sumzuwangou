package com.mall.controller;

import com.mall.dto.CouponDTO;
import com.mall.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/coupon")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @GetMapping("/available")
    public ResponseEntity<Map<String, Object>> getAvailableCoupons() {
        List<CouponDTO> coupons = couponService.getAvailableCoupons();
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", coupons);
        result.put("message", "success");
        return ResponseEntity.ok(result);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserCoupons(@PathVariable Long userId) {
        List<CouponDTO> coupons = couponService.getUserCoupons(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", coupons);
        result.put("message", "success");
        return ResponseEntity.ok(result);
    }

    @PostMapping("/receive")
    public ResponseEntity<Map<String, Object>> receiveCoupon(@RequestParam Long userId, @RequestParam Long couponId) {
        boolean success = couponService.receiveCoupon(userId, couponId);
        Map<String, Object> result = new HashMap<>();
        if (success) {
            result.put("code", 200);
            result.put("message", "领取成功");
        } else {
            result.put("code", 400);
            result.put("message", "领取失败");
        }
        return ResponseEntity.ok(result);
    }
}
