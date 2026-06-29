package org.example.service.impl;

import org.example.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private JdbcTemplate jdbc;

    @Override
    public Map<String, Object> stats(Long shopId, String range) {
        Map<String, Object> result = new LinkedHashMap<>();

        LocalDate today = LocalDate.now();
        LocalDate thirty = today.minusDays(30);
        LocalDate prevMonth = today.minusMonths(1);

        String sqlTotals = "SELECT " +
                "COUNT(*) AS totalOrders, " +
                "IFNULL(SUM(total_amount),0) AS totalSales, " +
                "SUM(CASE WHEN status=1 THEN 1 ELSE 0 END) AS pending, " +
                "SUM(CASE WHEN status=2 THEN 1 ELSE 0 END) AS paid, " +
                "SUM(CASE WHEN status=3 THEN 1 ELSE 0 END) AS shipped, " +
                "SUM(CASE WHEN status=4 THEN 1 ELSE 0 END) AS completed, " +
                "SUM(CASE WHEN status=0 THEN 1 ELSE 0 END) AS cancelled " +
                "FROM orders WHERE merchant_id=?";
        Map<String, Object> totals = jdbc.queryForMap(sqlTotals, shopId);

        String sqlToday = "SELECT " +
                "COUNT(*) AS cnt, IFNULL(SUM(total_amount),0) AS amt " +
                "FROM orders WHERE merchant_id=? AND DATE(created_at)=?";
        Map<String, Object> todayRow = jdbc.queryForMap(sqlToday, shopId, today.toString());
        int todayOrders = toInt(todayRow.get("cnt"));
        BigDecimal todaySales = toDecimal(todayRow.get("amt"));

        String sqlPrev30 = "SELECT " +
                "COUNT(*) AS cnt, IFNULL(SUM(total_amount),0) AS amt " +
                "FROM orders WHERE merchant_id=? AND created_at < ? AND created_at >= ?";
        Map<String, Object> prevRow = jdbc.queryForMap(sqlPrev30, shopId,
                LocalDateTime.of(today, LocalTime.MIN).toString(),
                LocalDateTime.of(prevMonth, LocalTime.MIN).toString());
        int prevOrders = toInt(prevRow.get("cnt"));
        BigDecimal prevSales = toDecimal(prevRow.get("amt"));

        int curOrders = toInt(totals.get("totalOrders"));
        BigDecimal curSales = toDecimal(totals.get("totalSales"));

        double ordersGrowth = prevOrders == 0 ? (curOrders > 0 ? 100.0 : 0)
                : round(((double)(curOrders - prevOrders) / prevOrders) * 100, 1);
        double salesGrowth = prevSales.compareTo(BigDecimal.ZERO) == 0
                ? (curSales.compareTo(BigDecimal.ZERO) > 0 ? 100.0 : 0)
                : round((curSales.subtract(prevSales).divide(prevSales, 2, java.math.RoundingMode.HALF_UP).doubleValue()) * 100, 1);

        Map<String, Object> statCards = new LinkedHashMap<>();
        statCards.put("todaySales", todaySales);
        statCards.put("todayOrders", todayOrders);
        statCards.put("pendingShip", toInt(totals.get("paid")));
        statCards.put("totalOrders", curOrders);
        statCards.put("totalSales", curSales);
        statCards.put("ordersGrowth", ordersGrowth);
        statCards.put("salesGrowth", salesGrowth);
        result.put("statCards", statCards);

        result.put("trend", buildTrend(shopId, range, today));

        String topSql = "SELECT p.product_name AS name, p.sales, p.stock " +
                "FROM term_products p WHERE p.merchant_id=? ORDER BY p.sales DESC LIMIT 5";
        List<Map<String, Object>> topRaw = jdbc.queryForList(topSql, shopId);
        List<Map<String, Object>> topProducts = new ArrayList<>();
        for (Map<String, Object> r : topRaw) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("name", r.get("name"));
            item.put("sales", toInt(r.get("sales")));
            item.put("stock", toInt(r.get("stock")));
            topProducts.add(item);
        }
        result.put("topProducts", topProducts);

        String catSql = "SELECT c.category_name AS name, IFNULL(SUM(p.sales),0) AS sales " +
                "FROM term_products p LEFT JOIN term_product_categories c ON p.category_id = c.id " +
                "WHERE p.merchant_id=? GROUP BY p.category_id, c.category_name " +
                "ORDER BY sales DESC LIMIT 8";
        List<Map<String, Object>> catRaw = jdbc.queryForList(catSql, shopId);
        List<Map<String, Object>> categories = new ArrayList<>();
        for (Map<String, Object> r : catRaw) {
            Map<String, Object> item = new LinkedHashMap<>();
            String name = r.get("name") == null ? "未分类" : r.get("name").toString();
            item.put("name", name);
            item.put("value", toInt(r.get("sales")));
            categories.add(item);
        }
        result.put("categories", categories);

        String recentSql = "SELECT order_no, total_amount, status, created_at " +
                "FROM orders WHERE merchant_id=? ORDER BY id DESC LIMIT 5";
        List<Map<String, Object>> recRaw = jdbc.queryForList(recentSql, shopId);
        List<Map<String, Object>> recentOrders = new ArrayList<>();
        String[] customerNames = {"张三", "李四", "王五", "赵六", "钱七", "孙八"};
        int ci = 0;
        for (Map<String, Object> r : recRaw) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("orderNo", r.get("order_no"));
            item.put("totalAmount", toDecimal(r.get("total_amount")));
            int st = toInt(r.get("status"));
            String feStatus = toFeStatus(st);
            item.put("status", feStatus);
            item.put("statusText", toStatusText(feStatus));
            item.put("createTime", r.get("created_at") == null ? "" : r.get("created_at").toString());
            item.put("customerName", customerNames[ci++ % customerNames.length]);
            recentOrders.add(item);
        }
        result.put("recentOrders", recentOrders);

        return result;
    }

    private List<Map<String, Object>> buildTrend(Long shopId, String range, LocalDate today) {
        LocalDate start;
        String groupFmt;
        DateTimeFormatter labelFmt;
        if ("year".equalsIgnoreCase(range)) {
            start = today.withDayOfMonth(1);
            start = start.minusMonths(11);
            groupFmt = "%Y-%m";
            labelFmt = DateTimeFormatter.ofPattern("yyyy-MM");
        } else if ("week".equalsIgnoreCase(range)) {
            start = today.minusDays(6);
            groupFmt = "%Y-%m-%d";
            labelFmt = DateTimeFormatter.ofPattern("MM-dd");
        } else {
            start = today.minusDays(29);
            groupFmt = "%Y-%m-%d";
            labelFmt = DateTimeFormatter.ofPattern("MM-dd");
        }

        List<Map<String, Object>> raw = jdbc.queryForList(
                "SELECT DATE_FORMAT(created_at, ?) AS bucket, COUNT(*) AS cnt, IFNULL(SUM(total_amount),0) AS amt " +
                        "FROM orders WHERE merchant_id=? AND created_at >= ? " +
                        "GROUP BY DATE_FORMAT(created_at, ?) ORDER BY bucket ASC",
                groupFmt, shopId, start.toString() + " 00:00:00", groupFmt);

        Map<String, int[]> map = new LinkedHashMap<>();
        for (Map<String, Object> r : raw) {
            String key = r.get("bucket") == null ? "" : r.get("bucket").toString();
            map.put(key, new int[]{toInt(r.get("cnt")), toInt(toDecimal(r.get("amt")).doubleValue())});
        }

        List<String> labels = new ArrayList<>();
        List<Integer> sales = new ArrayList<>();
        List<Integer> orders = new ArrayList<>();

        LocalDate cur = start;
        while (!cur.isAfter(today)) {
            String key;
            if ("year".equalsIgnoreCase(range)) {
                key = cur.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            } else {
                key = cur.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }
            String label;
            if ("year".equalsIgnoreCase(range)) {
                label = cur.format(DateTimeFormatter.ofPattern("yyyy年M月"));
            } else {
                label = cur.format(labelFmt);
            }
            labels.add(label);
            int[] v = map.getOrDefault(key, new int[]{0, 0});
            sales.add(v[1]);
            orders.add(v[0]);
            if ("year".equalsIgnoreCase(range)) {
                cur = cur.plusMonths(1);
                if (cur.isAfter(today)) break;
            } else {
                cur = cur.plusDays(1);
            }
        }

        Map<String, Object> packed = new LinkedHashMap<>();
        packed.put("labels", labels);
        packed.put("sales", sales);
        packed.put("orders", orders);
        return Collections.singletonList(packed);
    }

    private int toInt(Object v) {
        if (v == null) return 0;
        if (v instanceof Number) return ((Number) v).intValue();
        try { return Integer.parseInt(v.toString()); } catch (Exception e) { return 0; }
    }

    private BigDecimal toDecimal(Object v) {
        if (v == null) return BigDecimal.ZERO;
        if (v instanceof BigDecimal) return (BigDecimal) v;
        if (v instanceof Number) return new BigDecimal(v.toString());
        try { return new BigDecimal(v.toString()); } catch (Exception e) { return BigDecimal.ZERO; }
    }

    private double round(double v, int scale) {
        return Math.round(v * Math.pow(10, scale)) / Math.pow(10, scale);
    }

    private String toFeStatus(int db) {
        switch (db) {
            case 0: return "cancelled";
            case 1: return "pending";
            case 2: return "paid";
            case 3: return "shipped";
            case 4: return "completed";
            default: return "pending";
        }
    }

    private String toStatusText(String fe) {
        switch (fe) {
            case "pending": return "待付款";
            case "paid": return "待发货";
            case "shipped": return "已发货";
            case "completed": return "已完成";
            case "cancelled": return "已取消";
            default: return fe;
        }
    }
}
