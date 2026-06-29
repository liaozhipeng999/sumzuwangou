package org.example.service;

import java.util.List;
import java.util.Map;

public interface MerchantOrderService {

    Map<String, Object> page(Long shopId, Integer page, Integer pageSize,
                            String status, String keyword, String startDate, String endDate);

    Map<String, Object> detail(String orderNo, Long shopId);

    void ship(String orderNo, String express, String trackingNo, String remark, Long shopId, String operator);

    void cancel(String orderNo, String reason, Long shopId, String operator);
}
