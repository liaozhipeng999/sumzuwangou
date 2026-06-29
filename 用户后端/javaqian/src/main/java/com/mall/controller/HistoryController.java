package com.mall.controller;

import com.mall.entity.HistoryRecord;
import com.mall.mapper.HistoryRecordMapper;
import com.mall.mapper.ProductMapper;
import com.mall.entity.TermProducts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/history")
public class HistoryController {

    @Autowired
    private HistoryRecordMapper historyRecordMapper;

    @Autowired
    private ProductMapper productMapper;

    /**
     * 同步历史浏览记录
     * POST /api/history/sync
     */
    @PostMapping("/sync")
    public Map<String, Object> syncHistory(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<Map<String, Object>> records = (List<Map<String, Object>>) request.get("records");
            Long userId = 1L;
            
            int syncedCount = 0;
            for (Map<String, Object> record : records) {
                Long productId = ((Number) record.get("productId")).longValue();
                String productName = (String) record.get("productName");
                java.math.BigDecimal price = new java.math.BigDecimal(record.get("price").toString());
                java.math.BigDecimal originalPrice = record.get("originalPrice") != null 
                    ? new java.math.BigDecimal(record.get("originalPrice").toString()) 
                    : null;
                String mainImage = (String) record.get("mainImage");
                Long browseTime = ((Number) record.get("browseTime")).longValue();
                Integer browseCount = ((Number) record.get("browseCount")).intValue();

                HistoryRecord existing = historyRecordMapper.findByUserIdAndProductId(userId, productId);
                
                if (existing != null) {
                    historyRecordMapper.updateBrowseCount(userId, productId, browseTime, browseCount);
                } else {
                    HistoryRecord newRecord = new HistoryRecord();
                    newRecord.setUserId(userId);
                    newRecord.setProductId(productId);
                    newRecord.setProductName(productName);
                    newRecord.setPrice(price);
                    newRecord.setOriginalPrice(originalPrice);
                    newRecord.setMainImage(mainImage);
                    newRecord.setBrowseTime(browseTime);
                    newRecord.setBrowseCount(browseCount);
                    newRecord.setCreatedAt(LocalDateTime.now());
                    newRecord.setUpdatedAt(LocalDateTime.now());
                    historyRecordMapper.insert(newRecord);
                }
                syncedCount++;
            }

            int totalCount = historyRecordMapper.countByUserId(userId);

            Map<String, Object> data = new HashMap<>();
            data.put("syncedCount", syncedCount);
            data.put("totalCount", totalCount);

            result.put("code", 200);
            result.put("message", "同步成功");
            result.put("data", data);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "同步失败: " + e.getMessage());
            result.put("data", null);
        }

        return result;
    }

    /**
     * 获取历史浏览记录列表
     * GET /api/history/list
     */
    @GetMapping("/list")
    public Map<String, Object> getHistoryList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            Long userId = 1L;
            int offset = (page - 1) * size;
            int total = historyRecordMapper.countByUserId(userId);
            List<Map<String, Object>> records = historyRecordMapper.findByUserIdWithProduct(userId, offset, size);

            List<Map<String, Object>> list = new ArrayList<>();
            for (Map<String, Object> record : records) {
                Map<String, Object> item = new HashMap<>(record);
                
                Long productId = ((Number) record.get("product_id")).longValue();
                TermProducts product = productMapper.selectById(productId);
                
                if (product != null) {
                    Map<String, Object> productInfo = new HashMap<>();
                    productInfo.put("id", product.getId());
                    productInfo.put("productName", product.getProductName());
                    productInfo.put("price", product.getPrice());
                    productInfo.put("originalPrice", product.getOriginalPrice());
                    productInfo.put("sales", product.getSales());
                    productInfo.put("mainImage", product.getMainImage());
                    productInfo.put("brief", product.getBrief());
                    item.put("product", productInfo);
                } else {
                    item.put("product", null);
                }
                
                list.add(item);
            }

            Map<String, Object> data = new HashMap<>();
            data.put("list", list);
            data.put("total", total);
            data.put("page", page);
            data.put("size", size);

            result.put("code", 200);
            result.put("message", "获取成功");
            result.put("data", data);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "获取失败: " + e.getMessage());
            result.put("data", null);
        }

        return result;
    }

    /**
     * 删除单条历史记录
     * DELETE /api/history/{id}
     */
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteHistory(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            int deleted = historyRecordMapper.deleteById(id);
            
            if (deleted > 0) {
                result.put("code", 200);
                result.put("message", "删除成功");
                result.put("data", null);
            } else {
                result.put("code", 400);
                result.put("message", "记录不存在");
                result.put("data", null);
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "删除失败: " + e.getMessage());
            result.put("data", null);
        }

        return result;
    }

    /**
     * 清空历史浏览记录
     * DELETE /api/history/clear
     */
    @DeleteMapping("/clear")
    public Map<String, Object> clearHistory() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Long userId = 1L;
            int deletedCount = historyRecordMapper.deleteByUserId(userId);

            Map<String, Object> data = new HashMap<>();
            data.put("deletedCount", deletedCount);

            result.put("code", 200);
            result.put("message", "清空成功");
            result.put("data", data);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "清空失败: " + e.getMessage());
            result.put("data", null);
        }

        return result;
    }
}