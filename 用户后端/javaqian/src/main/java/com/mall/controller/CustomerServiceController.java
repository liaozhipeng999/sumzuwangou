package com.mall.controller;

import com.mall.entity.QuickReply;
import com.mall.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/customer-service")
public class CustomerServiceController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/shop/{shopId}")
    public ResponseEntity<Map<String, Object>> getShopInfo(@PathVariable Long shopId) {
        Map<String, Object> shopInfo = customerService.getShopInfo(shopId);
        
        Map<String, Object> response = new HashMap<>();
        if (!shopInfo.isEmpty()) {
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", shopInfo);
        } else {
            response.put("code", 404);
            response.put("message", "店铺不存在");
        }
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<Map<String, Object>> getProductInfo(@PathVariable Long productId) {
        Map<String, Object> productInfo = customerService.getProductInfo(productId);
        
        Map<String, Object> response = new HashMap<>();
        if (!productInfo.isEmpty()) {
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", productInfo);
        } else {
            response.put("code", 404);
            response.put("message", "商品不存在");
        }
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/messages")
    public ResponseEntity<Map<String, Object>> getMessages(
            @RequestParam Long shopId,
            @RequestParam Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        
        Map<String, Object> messages = customerService.getMessages(shopId, userId, page, pageSize);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", messages);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/messages")
    public ResponseEntity<Map<String, Object>> sendMessage(@RequestBody Map<String, Object> request) {
        Long userId = ((Number) request.get("userId")).longValue();
        Long shopId = ((Number) request.get("shopId")).longValue();
        Long productId = request.get("productId") != null ? ((Number) request.get("productId")).longValue() : null;
        String content = (String) request.get("content");
        String messageType = request.get("messageType") != null ? (String) request.get("messageType") : "TEXT";
        
        Map<String, Object> response = new HashMap<>();
        
        if (content == null || content.trim().isEmpty()) {
            response.put("code", 10002);
            response.put("message", "消息内容为空");
            return ResponseEntity.ok(response);
        }
        
        if (content.length() > 1000) {
            response.put("code", 10003);
            response.put("message", "消息内容过长");
            return ResponseEntity.ok(response);
        }
        
        Map<String, Object> result = customerService.sendMessage(userId, shopId, productId, content, messageType);
        
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", result);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/quick-replies/{shopId}")
    public ResponseEntity<Map<String, Object>> getQuickReplies(@PathVariable Long shopId) {
        List<QuickReply> quickReplies = customerService.getQuickReplies(shopId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", quickReplies);
        
        return ResponseEntity.ok(response);
    }

    // ============ 新增接口 ============

    /** 获取用户会话列表 */
    @GetMapping("/conversations")
    public ResponseEntity<Map<String, Object>> getConversations(@RequestParam Long userId) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", customerService.getConversations(userId));
        return ResponseEntity.ok(response);
    }

    /** 获取用户未读消息总数 */
    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Object>> getUnreadCount(@RequestParam Long userId) {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        data.put("total", customerService.getUnreadCount(userId));
        data.put("shops", customerService.getUnreadCountByShop(userId));
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", data);
        return ResponseEntity.ok(response);
    }

    /** 标记某店铺消息已读 */
    @PostMapping("/mark-read")
    public ResponseEntity<Map<String, Object>> markAsRead(@RequestBody Map<String, Object> request) {
        Long userId = ((Number) request.get("userId")).longValue();
        Long shopId = ((Number) request.get("shopId")).longValue();
        customerService.markAsRead(userId, shopId);

        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "已标记已读");
        return ResponseEntity.ok(response);
    }

    /** 删除单条消息 */
    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<Map<String, Object>> deleteMessage(
            @PathVariable Long messageId,
            @RequestParam Long userId) {
        Map<String, Object> response = new HashMap<>();
        if (customerService.deleteMessage(messageId, userId)) {
            response.put("code", 200);
            response.put("message", "删除成功");
        } else {
            response.put("code", 404);
            response.put("message", "消息不存在或无权删除");
        }
        return ResponseEntity.ok(response);
    }

    /** 清空与某店铺的聊天记录 */
    @DeleteMapping("/messages/clear")
    public ResponseEntity<Map<String, Object>> clearMessages(
            @RequestParam Long userId,
            @RequestParam Long shopId) {
        customerService.clearMessages(userId, shopId);

        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "已清空聊天记录");
        return ResponseEntity.ok(response);
    }

    // ============ 商家端接口 ============

    /** 商家发送消息 */
    @PostMapping("/merchant/messages")
    public ResponseEntity<Map<String, Object>> merchantSendMessage(@RequestBody Map<String, Object> request) {
        Long shopId = ((Number) request.get("shopId")).longValue();
        Long userId = ((Number) request.get("userId")).longValue();
        String content = (String) request.get("content");
        String messageType = request.get("messageType") != null ? (String) request.get("messageType") : "TEXT";

        Map<String, Object> response = new HashMap<>();
        if (content == null || content.trim().isEmpty()) {
            response.put("code", 10002);
            response.put("message", "消息内容为空");
            return ResponseEntity.ok(response);
        }
        if (content.length() > 1000) {
            response.put("code", 10003);
            response.put("message", "消息内容过长");
            return ResponseEntity.ok(response);
        }

        Map<String, Object> result = customerService.sendMerchantMessage(shopId, userId, content, messageType);
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", result);
        return ResponseEntity.ok(response);
    }

    /** 商家会话列表 */
    @GetMapping("/merchant/conversations")
    public ResponseEntity<Map<String, Object>> merchantConversations(@RequestParam Long shopId) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", customerService.getMerchantConversations(shopId));
        return ResponseEntity.ok(response);
    }

    /** 商家未读消息数 */
    @GetMapping("/merchant/unread-count")
    public ResponseEntity<Map<String, Object>> merchantUnreadCount(@RequestParam Long shopId) {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        data.put("total", customerService.getMerchantUnreadCount(shopId));
        data.put("users", customerService.getMerchantUnreadByUser(shopId));
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", data);
        return ResponseEntity.ok(response);
    }

    /** 商家标记某用户消息已读 */
    @PostMapping("/merchant/mark-read")
    public ResponseEntity<Map<String, Object>> merchantMarkRead(@RequestBody Map<String, Object> request) {
        Long shopId = ((Number) request.get("shopId")).longValue();
        Long userId = ((Number) request.get("userId")).longValue();
        customerService.markAsReadByShop(shopId, userId);

        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "已标记已读");
        return ResponseEntity.ok(response);
    }

    /** 商家获取快捷回复列表 */
    @GetMapping("/merchant/quick-replies")
    public ResponseEntity<Map<String, Object>> merchantQuickReplies(@RequestParam Long shopId) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", customerService.getQuickReplies(shopId));
        return ResponseEntity.ok(response);
    }

    /** 商家新增快捷回复 */
    @PostMapping("/merchant/quick-replies")
    public ResponseEntity<Map<String, Object>> addQuickReply(@RequestBody Map<String, Object> request) {
        Long shopId = ((Number) request.get("shopId")).longValue();
        String content = (String) request.get("content");
        Integer sort = request.get("sort") != null ? ((Number) request.get("sort")).intValue() : 0;

        Map<String, Object> response = new HashMap<>();
        if (content == null || content.trim().isEmpty()) {
            response.put("code", 10002);
            response.put("message", "回复内容不能为空");
            return ResponseEntity.ok(response);
        }

        customerService.addQuickReply(shopId, content, sort);
        response.put("code", 200);
        response.put("message", "添加成功");
        return ResponseEntity.ok(response);
    }

    /** 商家删除快捷回复 */
    @DeleteMapping("/merchant/quick-replies/{id}")
    public ResponseEntity<Map<String, Object>> deleteQuickReply(
            @PathVariable Long id,
            @RequestParam Long shopId) {
        Map<String, Object> response = new HashMap<>();
        if (customerService.deleteQuickReply(id, shopId)) {
            response.put("code", 200);
            response.put("message", "删除成功");
        } else {
            response.put("code", 404);
            response.put("message", "快捷回复不存在");
        }
        return ResponseEntity.ok(response);
    }
}