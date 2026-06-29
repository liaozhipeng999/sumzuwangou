package org.example.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.dto.*;
import java.util.List;
import java.util.Map;

public interface CustomerService {

    // ========== 用户端接口 ==========

    /** 用户发送消息 */
    Map<String, Object> userSendMessage(SendMessageDTO dto);

    /** 用户获取会话列表 */
    List<ConversationVO> getUserConversations(Long userId);

    /** 用户获取未读消息总数 */
    UnreadCountVO getUserUnreadCount(Long userId);

    /** 用户标记某店铺消息已读 */
    void userMarkAsRead(Long userId, Long shopId);

    /** 获取聊天记录（分页，用户/商家共用） */
    Map<String, Object> getMessages(Long shopId, Long userId, int page, int pageSize);

    /** 删除单条消息 */
    void deleteMessage(Long messageId, Long userId);

    /** 清空与某店铺的聊天记录 */
    void clearMessages(Long userId, Long shopId);

    /** 上传消息图片 */
    Map<String, Object> uploadImage(org.springframework.web.multipart.MultipartFile file);

    /** 标记单条消息已读 */
    void markMessageAsRead(Long messageId);

    /** 获取店铺信息 */
    Map<String, Object> getShopInfo(Long shopId);

    /** 获取商品信息 */
    Map<String, Object> getProductInfo(Long productId);

    /** 获取快捷回复列表 */
    List<Map<String, Object>> getQuickReplies(Long shopId);

    // ========== 商家端接口 ==========

    /** 商家发送消息 */
    Map<String, Object> merchantSendMessage(SendMessageDTO dto);

    /** 商家获取会话列表 */
    List<ConversationVO> getMerchantConversations(Long shopId);

    /** 商家获取未读消息数 */
    UnreadCountVO getMerchantUnreadCount(Long shopId);

    /** 商家标记某用户消息已读 */
    void merchantMarkAsRead(Long shopId, Long userId);

    /** 快捷回复管理：新增 */
    Map<String, Object> addQuickReply(QuickReplyDTO dto);

    /** 快捷回复管理：删除 */
    void deleteQuickReply(Long id, Long shopId);

    /** 快捷回复管理：获取全部 */
    List<Map<String, Object>> getAllQuickReplies(Long shopId);
}
