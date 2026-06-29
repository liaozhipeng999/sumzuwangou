package org.example.controller;

import org.example.common.Result;
import org.example.dto.*;
import org.example.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customer-service")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    // ========== 用户端接口 ==========

    /**
     * 用户发送消息
     */
    @PostMapping("/messages")
    public Result<Map<String, Object>> userSendMessage(@Validated @RequestBody SendMessageDTO dto) {
        try {
            Map<String, Object> data = customerService.userSendMessage(dto);
            return Result.success("发送成功", data);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 用户获取会话列表
     */
    @GetMapping("/conversations")
    public Result<List<ConversationVO>> getUserConversations(@RequestParam Long userId) {
        try {
            List<ConversationVO> list = customerService.getUserConversations(userId);
            return Result.success(list);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 用户获取未读消息总数
     */
    @GetMapping("/unread-count")
    public Result<UnreadCountVO> getUserUnreadCount(@RequestParam Long userId) {
        try {
            UnreadCountVO vo = customerService.getUserUnreadCount(userId);
            return Result.success(vo);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 用户标记某店铺消息已读
     */
    @PostMapping("/mark-read")
    public Result<Void> userMarkAsRead(@Validated @RequestBody MarkReadDTO dto) {
        try {
            customerService.userMarkAsRead(dto.getUserId(), dto.getShopId());
            return Result.success("标记成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取聊天记录（分页，用户/商家共用）
     */
    @GetMapping("/messages")
    public Result<Map<String, Object>> getMessages(
            @RequestParam Long shopId,
            @RequestParam Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        try {
            Map<String, Object> data = customerService.getMessages(shopId, userId, page, pageSize);
            return Result.success(data);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除单条消息
     */
    @DeleteMapping("/messages/{messageId}")
    public Result<Void> deleteMessage(@PathVariable Long messageId, @RequestParam Long userId) {
        try {
            customerService.deleteMessage(messageId, userId);
            return Result.success("删除成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 清空与某店铺的聊天记录
     */
    @DeleteMapping("/messages/clear")
    public Result<Void> clearMessages(@RequestParam Long userId, @RequestParam Long shopId) {
        try {
            customerService.clearMessages(userId, shopId);
            return Result.success("清空成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取店铺信息
     */
    @GetMapping("/shop/{shopId}")
    public Result<Map<String, Object>> getShopInfo(@PathVariable Long shopId) {
        try {
            Map<String, Object> data = customerService.getShopInfo(shopId);
            return Result.success(data);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取商品信息
     */
    @GetMapping("/product/{productId}")
    public Result<Map<String, Object>> getProductInfo(@PathVariable Long productId) {
        try {
            Map<String, Object> data = customerService.getProductInfo(productId);
            return Result.success(data);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 上传消息图片
     */
    @PostMapping("/upload/image")
    public Result<Map<String, Object>> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            Map<String, Object> data = customerService.uploadImage(file);
            return Result.success("上传成功", data);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 标记单条消息已读
     */
    @PutMapping("/messages/{messageId}/read")
    public Result<Void> markMessageAsRead(@PathVariable Long messageId) {
        try {
            customerService.markMessageAsRead(messageId);
            return Result.success("标记成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取快捷回复列表
     */
    @GetMapping("/quick-replies/{shopId}")
    public Result<List<Map<String, Object>>> getQuickReplies(@PathVariable Long shopId) {
        try {
            List<Map<String, Object>> list = customerService.getQuickReplies(shopId);
            return Result.success(list);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    // ========== 商家端接口 ==========

    /**
     * 商家发送消息
     */
    @PostMapping("/merchant/messages")
    public Result<Map<String, Object>> merchantSendMessage(@Validated @RequestBody SendMessageDTO dto) {
        try {
            Map<String, Object> data = customerService.merchantSendMessage(dto);
            return Result.success("发送成功", data);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 商家获取会话列表
     */
    @GetMapping("/merchant/conversations")
    public Result<List<ConversationVO>> getMerchantConversations(@RequestParam Long shopId) {
        try {
            List<ConversationVO> list = customerService.getMerchantConversations(shopId);
            return Result.success(list);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 商家获取未读消息数
     */
    @GetMapping("/merchant/unread-count")
    public Result<UnreadCountVO> getMerchantUnreadCount(@RequestParam Long shopId) {
        try {
            UnreadCountVO vo = customerService.getMerchantUnreadCount(shopId);
            return Result.success(vo);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 商家标记某用户消息已读
     */
    @PostMapping("/merchant/mark-read")
    public Result<Void> merchantMarkAsRead(@Validated @RequestBody MarkReadDTO dto) {
        try {
            customerService.merchantMarkAsRead(dto.getShopId(), dto.getUserId());
            return Result.success("标记成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 快捷回复管理：新增
     */
    @PostMapping("/merchant/quick-replies")
    public Result<Map<String, Object>> addQuickReply(@Validated @RequestBody QuickReplyDTO dto) {
        try {
            Map<String, Object> data = customerService.addQuickReply(dto);
            return Result.success("添加成功", data);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 快捷回复管理：删除
     */
    @DeleteMapping("/merchant/quick-replies/{id}")
    public Result<Void> deleteQuickReply(@PathVariable Long id, @RequestParam Long shopId) {
        try {
            customerService.deleteQuickReply(id, shopId);
            return Result.success("删除成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 快捷回复管理：获取全部
     */
    @GetMapping("/merchant/quick-replies")
    public Result<List<Map<String, Object>>> getAllQuickReplies(@RequestParam Long shopId) {
        try {
            List<Map<String, Object>> list = customerService.getAllQuickReplies(shopId);
            return Result.success(list);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
