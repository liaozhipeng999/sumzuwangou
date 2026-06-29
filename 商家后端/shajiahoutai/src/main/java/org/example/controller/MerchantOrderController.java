package org.example.controller;

import org.example.common.Result;
import org.example.service.MerchantOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/merchant/order")
public class MerchantOrderController {

    @Autowired
    private MerchantOrderService merchantOrderService;

    @GetMapping("/page")
    public Result<Map<String, Object>> page(
            @RequestParam Long shopId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        try {
            Map<String, Object> result = merchantOrderService.page(shopId, page, pageSize, status, keyword, startDate, endDate);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/detail")
    public Result<Map<String, Object>> detail(@RequestParam String orderNo,
                                            @RequestParam(required = false) Long shopId) {
        try {
            Map<String, Object> result = merchantOrderService.detail(orderNo, shopId);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/ship")
    public Result<Void> ship(@RequestBody Map<String, Object> body) {
        try {
            String orderNo = (String) body.get("orderNo");
            String express = (String) body.get("express");
            String trackingNo = (String) body.get("trackingNo");
            String remark = (String) body.get("remark");
            Long shopId = toLong(body.get("shopId"));
            String operator = (String) body.get("operator");
            if (orderNo == null || express == null || trackingNo == null) {
                return Result.error("orderNo / express / trackingNo 必填");
            }
            merchantOrderService.ship(orderNo, express, trackingNo, remark, shopId, operator);
            return Result.success("发货成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/cancel")
    public Result<Void> cancel(@RequestBody Map<String, Object> body) {
        try {
            String orderNo = (String) body.get("orderNo");
            String reason = (String) body.get("reason");
            Long shopId = toLong(body.get("shopId"));
            String operator = (String) body.get("operator");
            if (orderNo == null) {
                return Result.error("orderNo 必填");
            }
            merchantOrderService.cancel(orderNo, reason, shopId, operator);
            return Result.success("订单已取消", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    private Long toLong(Object v) {
        if (v == null) return null;
        if (v instanceof Number) return ((Number) v).longValue();
        try { return Long.parseLong(v.toString()); } catch (Exception e) { return null; }
    }
}
