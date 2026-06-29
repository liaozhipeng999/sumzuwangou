package org.example.service.impl;

import org.example.entity.TermOrder;
import org.example.mapper.MerchantOrderLogMapper;
import org.example.mapper.OrderMapper;
import org.example.entity.MerchantOrderLog;
import org.example.service.MerchantOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class MerchantOrderServiceImpl implements MerchantOrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private MerchantOrderLogMapper logMapper;

    private MerchantOrderLog findLatestLog(String orderNo) {
        List<MerchantOrderLog> list = logMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<MerchantOrderLog>()
                        .eq("order_no", orderNo)
                        .orderByDesc("id")
                        .last("LIMIT 1")
        );
        return list == null || list.isEmpty() ? null : list.get(0);
    }

    @Override
    public Map<String, Object> page(Long shopId, Integer page, Integer pageSize,
                                     String status, String keyword, String startDate, String endDate) {
        int p = page != null ? page : 1;
        int ps = pageSize != null ? pageSize : 10;
        int offset = (p - 1) * ps;

        List<Map<String, Object>> rows = orderMapper.selectMerchantOrderPage(
                shopId, status, keyword, startDate, endDate, offset, ps);

        List<Map<String, Object>> list = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("orderNo", row.get("orderNo"));
            item.put("createTime", row.get("createTime"));
            item.put("customerName", row.get("customerName") == null ? "" : row.get("customerName"));
            item.put("customerPhone", row.get("customerPhone") == null ? "" : row.get("customerPhone"));
            item.put("productInfo", row.get("productInfo") == null ? "" : row.get("productInfo"));
            item.put("totalAmount", row.get("totalAmount"));
            Integer dbStatus = (Integer) row.get("dbStatus");
            item.put("status", dbStatus == null ? "pending" : TermOrder.toFeStatus(dbStatus));
            list.add(item);
        }

        int total = orderMapper.countMerchantOrders(shopId, status, keyword, startDate, endDate);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("total", total);
        result.put("page", p);
        result.put("pageSize", ps);
        result.put("list", list);
        return result;
    }

    @Override
    public Map<String, Object> detail(String orderNo, Long shopId) {
        Map<String, Object> head = orderMapper.selectOrderDetailByNo(orderNo);
        if (head == null) {
            throw new RuntimeException("订单不存在");
        }

        Map<String, Object> wrapped = new LinkedHashMap<>();
        wrapped.put("orderNo", head.get("orderNo"));
        wrapped.put("createTime", head.get("createTime"));
        wrapped.put("customerName", head.get("customerName") == null ? "" : head.get("customerName"));
        wrapped.put("customerPhone", head.get("customerPhone") == null ? "" : head.get("customerPhone"));
        wrapped.put("address", head.get("address") == null ? "" : head.get("address"));
        wrapped.put("payMethod", "微信支付");
        wrapped.put("totalAmount", head.get("totalAmount"));
        Integer dbStatus = (Integer) head.get("dbStatus");
        wrapped.put("status", dbStatus == null ? "pending" : TermOrder.toFeStatus(dbStatus));

        List<Map<String, Object>> rawItems = orderMapper.selectOrderItemsByOrderNo(orderNo, shopId);
        List<Map<String, Object>> items = new ArrayList<>();
        for (Map<String, Object> ri : rawItems) {
            Map<String, Object> it = new LinkedHashMap<>();
            it.put("name", ri.get("name"));
            Object specRaw = ri.get("spec");
            if (specRaw == null) {
                it.put("spec", "");
            } else {
                String s = specRaw.toString();
                if (s.startsWith("{") || s.startsWith("[")) {
                    try {
                        StringBuilder sb = new StringBuilder();
                        if (s.startsWith("{")) {
                            String inner = s.substring(1, s.length() - 1);
                            String[] pairs = inner.split(",");
                            for (String pair : pairs) {
                                String[] kv = pair.split(":", 2);
                                if (kv.length == 2) {
                                    String k = kv[0].trim().replaceAll("\"", "");
                                    String v = kv[1].trim().replaceAll("\"", "");
                                    if (sb.length() > 0) sb.append(" ");
                                    sb.append(v);
                                }
                            }
                        }
                        it.put("spec", sb.toString());
                    } catch (Exception e) {
                        it.put("spec", s);
                    }
                } else {
                    it.put("spec", s);
                }
            }
            it.put("price", ri.get("price"));
            it.put("quantity", ri.get("quantity"));
            it.put("subtotal", ri.get("subtotal"));
            items.add(it);
        }
        wrapped.put("items", items);

        MerchantOrderLog latest = findLatestLog(orderNo);
        if (latest != null) {
            wrapped.put("express", latest.getExpress() == null ? "" : latest.getExpress());
            wrapped.put("trackingNo", latest.getTrackingNo() == null ? "" : latest.getTrackingNo());
            wrapped.put("shipRemark", latest.getRemark() == null ? "" : latest.getRemark());
        } else {
            wrapped.put("express", "");
            wrapped.put("trackingNo", "");
            wrapped.put("shipRemark", "");
        }

        return wrapped;
    }

    @Override
    @Transactional
    public void ship(String orderNo, String express, String trackingNo, String remark, Long shopId, String operator) {
        Map<String, Object> order = orderMapper.selectOrderByNo(orderNo);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        Integer dbStatus = (Integer) order.get("status");
        if (dbStatus == null || dbStatus != TermOrder.DB_PAID) {
            throw new RuntimeException("订单状态不允许发货，当前：" + TermOrder.toFeStatus(dbStatus == null ? 1 : dbStatus));
        }

        orderMapper.updateShipInfo(orderNo, express, trackingNo, remark);
        orderMapper.updateStatus(orderNo, TermOrder.DB_SHIPPED);

        MerchantOrderLog log = new MerchantOrderLog();
        log.setOrderNo(orderNo);
        log.setShopId(shopId);
        log.setAction("SHIP");
        log.setExpress(express);
        log.setTrackingNo(trackingNo);
        log.setRemark(remark);
        log.setOperator(operator);
        log.setActionTime(LocalDateTime.now());
        logMapper.insert(log);
    }

    @Override
    @Transactional
    public void cancel(String orderNo, String reason, Long shopId, String operator) {
        Map<String, Object> order = orderMapper.selectOrderByNo(orderNo);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        Integer dbStatus = (Integer) order.get("status");
        if (dbStatus == null) dbStatus = 1;
        if (dbStatus != TermOrder.DB_PENDING && dbStatus != TermOrder.DB_PAID) {
            throw new RuntimeException("当前状态不允许取消：" + TermOrder.toFeStatus(dbStatus));
        }

        orderMapper.updateStatus(orderNo, TermOrder.DB_CANCELLED);

        MerchantOrderLog log = new MerchantOrderLog();
        log.setOrderNo(orderNo);
        log.setShopId(shopId);
        log.setAction("CANCEL");
        log.setRemark(reason);
        log.setOperator(operator);
        log.setActionTime(LocalDateTime.now());
        logMapper.insert(log);
    }
}
