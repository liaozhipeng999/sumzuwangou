package com.mall.service;

import com.mall.entity.CustomerMessage;
import com.mall.entity.QuickReply;

import java.util.List;
import java.util.Map;

public interface CustomerService {

    Map<String, Object> getShopInfo(Long shopId);

    Map<String, Object> getProductInfo(Long productId);

    Map<String, Object> getMessages(Long shopId, Long userId, int page, int pageSize);

    Map<String, Object> sendMessage(Long userId, Long shopId, Long productId, String content, String messageType);

    List<QuickReply> getQuickReplies(Long shopId);

    // 获取用户所有会话列表
    List<Map<String, Object>> getConversations(Long userId);

    // 获取用户未读消息总数
    int getUnreadCount(Long userId);

    // 获取用户每个店铺的未读消息数
    List<Map<String, Object>> getUnreadCountByShop(Long userId);

    // 标记某店铺消息已读
    void markAsRead(Long userId, Long shopId);

    // 删除单条消息
    boolean deleteMessage(Long messageId, Long userId);

    // 清空与某店铺的聊天记录
    void clearMessages(Long userId, Long shopId);

    // ============ 商家端方法 ============

    // 商家发送消息
    Map<String, Object> sendMerchantMessage(Long shopId, Long userId, String content, String messageType);

    // 商家会话列表
    List<Map<String, Object>> getMerchantConversations(Long shopId);

    // 商家未读消息总数
    int getMerchantUnreadCount(Long shopId);

    // 商家按用户分组的未读数
    List<Map<String, Object>> getMerchantUnreadByUser(Long shopId);

    // 商家标记某用户消息已读
    void markAsReadByShop(Long shopId, Long userId);

    // ============ 快捷回复管理 ============

    // 新增快捷回复
    void addQuickReply(Long shopId, String content, Integer sort);

    // 删除快捷回复
    boolean deleteQuickReply(Long id, Long shopId);
}