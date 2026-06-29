package com.mall.controller;

import com.mall.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    /**
     * 获取消息分类概览
     * GET /api/messages/summary?userId=1
     */
    @GetMapping("/summary")
    public Map<String, Object> getMessageSummary(@RequestParam Long userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            result.put("code", 200);
            result.put("message", "success");
            result.put("data", messageService.getMessageSummary(userId));
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "获取消息概览失败: " + e.getMessage());
            result.put("data", null);
        }
        return result;
    }

    /**
     * 获取总未读数
     * GET /api/messages/unread-count?userId=1
     */
    @GetMapping("/unread-count")
    public Map<String, Object> getUnreadCount(@RequestParam Long userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("total", messageService.getUnreadCount(userId));
            result.put("code", 200);
            result.put("message", "success");
            result.put("data", data);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "获取未读数失败: " + e.getMessage());
            result.put("data", null);
        }
        return result;
    }

    /**
     * 获取某分类的消息列表（分页）
     * GET /api/messages/:type?userId=1&page=1&pageSize=20
     */
    @GetMapping("/{type}")
    public Map<String, Object> getMessagesByType(
            @PathVariable String type,
            @RequestParam Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        Map<String, Object> result = new HashMap<>();
        try {
            result.put("code", 200);
            result.put("message", "success");
            result.put("data", messageService.getMessagesByType(userId, type, page, pageSize));
        } catch (IllegalArgumentException e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
            result.put("data", null);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "获取消息列表失败: " + e.getMessage());
            result.put("data", null);
        }
        return result;
    }

    /**
     * 标记单条消息已读
     * PUT /api/messages/:id/read?userId=1
     */
    @PutMapping("/{id}/read")
    public Map<String, Object> markAsRead(@PathVariable Long id, @RequestParam Long userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = messageService.markAsRead(id, userId);
            if (success) {
                Map<String, Object> data = new HashMap<>();
                data.put("id", id);
                result.put("code", 200);
                result.put("message", "success");
                result.put("data", data);
            } else {
                result.put("code", 404);
                result.put("message", "消息不存在或无权限");
                result.put("data", null);
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "标记已读失败: " + e.getMessage());
            result.put("data", null);
        }
        return result;
    }

    /**
     * 标记某分类全部已读
     * PUT /api/messages/:type/read-all?userId=1
     */
    @PutMapping("/{type}/read-all")
    public Map<String, Object> markAllAsReadByType(@PathVariable String type, @RequestParam Long userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            messageService.markAllAsReadByType(userId, type);
            result.put("code", 200);
            result.put("message", "success");
            result.put("data", null);
        } catch (IllegalArgumentException e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
            result.put("data", null);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "批量标记已读失败: " + e.getMessage());
            result.put("data", null);
        }
        return result;
    }

    /**
     * 标记所有消息已读
     * PUT /api/messages/read-all?userId=1
     */
    @PutMapping("/read-all")
    public Map<String, Object> markAllAsRead(@RequestParam Long userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            messageService.markAllAsRead(userId);
            result.put("code", 200);
            result.put("message", "success");
            result.put("data", null);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "标记全部已读失败: " + e.getMessage());
            result.put("data", null);
        }
        return result;
    }

    /**
     * 删除单条消息（软删除）
     * DELETE /api/messages/:id?userId=1
     */
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteMessage(@PathVariable Long id, @RequestParam Long userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = messageService.deleteMessage(id, userId);
            if (success) {
                result.put("code", 200);
                result.put("message", "success");
                result.put("data", null);
            } else {
                result.put("code", 404);
                result.put("message", "消息不存在或无权限");
                result.put("data", null);
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "删除消息失败: " + e.getMessage());
            result.put("data", null);
        }
        return result;
    }

    /**
     * 批量删除某类型消息（软删除）
     * DELETE /api/messages/type/:type?userId=1
     */
    @DeleteMapping("/type/{type}")
    public Map<String, Object> deleteMessagesByType(@PathVariable String type, @RequestParam Long userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            int count = messageService.deleteMessagesByType(userId, type);
            Map<String, Object> data = new HashMap<>();
            data.put("count", count);
            result.put("code", 200);
            result.put("message", "success");
            result.put("data", data);
        } catch (IllegalArgumentException e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
            result.put("data", null);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "批量删除失败: " + e.getMessage());
            result.put("data", null);
        }
        return result;
    }
}
