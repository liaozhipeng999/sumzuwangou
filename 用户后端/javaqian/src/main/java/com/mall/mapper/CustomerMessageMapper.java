package com.mall.mapper;

import com.mall.entity.CustomerMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Delete;

import java.util.List;
import java.util.Map;

@Mapper
public interface CustomerMessageMapper {

    @Insert("INSERT INTO customer_message (user_id, shop_id, product_id, sender_type, content, message_type, send_time, is_read, created_at) " +
            "VALUES (#{userId}, #{shopId}, #{productId}, #{senderType}, #{content}, #{messageType}, #{sendTime}, #{isRead}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "messageId", keyColumn = "message_id")
    int insert(CustomerMessage message);

    @Select("SELECT COUNT(*) FROM customer_message WHERE user_id = #{userId} AND shop_id = #{shopId}")
    int countByUserAndShop(@Param("userId") Long userId, @Param("shopId") Long shopId);

    @Select("SELECT message_id as messageId, user_id as userId, shop_id as shopId, product_id as productId, " +
            "sender_type as senderType, content, message_type as messageType, send_time as sendTime, is_read as isRead " +
            "FROM customer_message WHERE user_id = #{userId} AND shop_id = #{shopId} " +
            "ORDER BY send_time DESC LIMIT #{offset}, #{pageSize}")
    List<CustomerMessage> findByUserAndShop(@Param("userId") Long userId, @Param("shopId") Long shopId,
                                            @Param("offset") int offset, @Param("pageSize") int pageSize);

    @Update("UPDATE customer_message SET is_read = 1 WHERE user_id = #{userId} AND shop_id = #{shopId} AND is_read = 0")
    int markAsRead(@Param("userId") Long userId, @Param("shopId") Long shopId);

    // 获取用户所有会话列表（按店铺分组，显示最后一条消息）
    @Select("SELECT m.shop_id as shopId, m.content as lastMessage, m.message_type as lastMessageType, " +
            "m.send_time as lastSendTime, m.sender_type as lastSenderType, " +
            "COALESCE(unread.cnt, 0) as unreadCount " +
            "FROM customer_message m " +
            "INNER JOIN ( " +
            "  SELECT shop_id, MAX(send_time) as max_time " +
            "  FROM customer_message WHERE user_id = #{userId} " +
            "  GROUP BY shop_id " +
            ") latest ON m.shop_id = latest.shop_id AND m.send_time = latest.max_time " +
            "LEFT JOIN ( " +
            "  SELECT shop_id, COUNT(*) as cnt " +
            "  FROM customer_message WHERE user_id = #{userId} AND sender_type = 'SHOP' AND is_read = 0 " +
            "  GROUP BY shop_id " +
            ") unread ON m.shop_id = unread.shop_id " +
            "WHERE m.user_id = #{userId} " +
            "ORDER BY m.send_time DESC")
    List<Map<String, Object>> getConversations(@Param("userId") Long userId);

    // 获取用户未读消息总数
    @Select("SELECT COUNT(*) FROM customer_message WHERE user_id = #{userId} AND sender_type = 'SHOP' AND is_read = 0")
    int countUnread(@Param("userId") Long userId);

    // 获取用户每个店铺的未读消息数
    @Select("SELECT shop_id as shopId, COUNT(*) as unreadCount " +
            "FROM customer_message WHERE user_id = #{userId} AND sender_type = 'SHOP' AND is_read = 0 " +
            "GROUP BY shop_id")
    List<Map<String, Object>> countUnreadByShopGroupByShop(@Param("userId") Long userId);

    // 获取消息详情
    @Select("SELECT message_id as messageId, user_id as userId, shop_id as shopId, product_id as productId, " +
            "sender_type as senderType, content, message_type as messageType, send_time as sendTime, is_read as isRead " +
            "FROM customer_message WHERE message_id = #{messageId}")
    CustomerMessage findByMessageId(@Param("messageId") Long messageId);

    // 删除消息
    @Delete("DELETE FROM customer_message WHERE message_id = #{messageId} AND user_id = #{userId}")
    int deleteMessage(@Param("messageId") Long messageId, @Param("userId") Long userId);

    // 清空与某店铺的聊天记录
    @Delete("DELETE FROM customer_message WHERE user_id = #{userId} AND shop_id = #{shopId}")
    int clearMessages(@Param("userId") Long userId, @Param("shopId") Long shopId);

    // 根据最后一条消息查找回复触发的快捷回复
    @Select("SELECT content FROM quick_reply WHERE shop_id = #{shopId} ORDER BY sort ASC LIMIT 1")
    String findAutoReply(@Param("shopId") Long shopId);

    // 检查店铺是否已向该用户发过消息（用于控制自动回复只触发一次）
    @Select("SELECT COUNT(*) FROM customer_message WHERE user_id = #{userId} AND shop_id = #{shopId} AND sender_type = 'SHOP'")
    int countShopMessages(@Param("userId") Long userId, @Param("shopId") Long shopId);

    // ============ 商家端方法 ============

    // 商家会话列表：按用户分组，显示最后一条消息
    @Select("SELECT m.user_id as userId, m.content as lastMessage, m.message_type as lastMessageType, " +
            "m.send_time as lastSendTime, m.sender_type as lastSenderType, " +
            "COALESCE(unread.cnt, 0) as unreadCount " +
            "FROM customer_message m " +
            "INNER JOIN ( " +
            "  SELECT user_id, MAX(send_time) as max_time " +
            "  FROM customer_message WHERE shop_id = #{shopId} " +
            "  GROUP BY user_id " +
            ") latest ON m.user_id = latest.user_id AND m.send_time = latest.max_time " +
            "LEFT JOIN ( " +
            "  SELECT user_id, COUNT(*) as cnt " +
            "  FROM customer_message WHERE shop_id = #{shopId} AND sender_type = 'USER' AND is_read = 0 " +
            "  GROUP BY user_id " +
            ") unread ON m.user_id = unread.user_id " +
            "WHERE m.shop_id = #{shopId} " +
            "ORDER BY m.send_time DESC")
    List<Map<String, Object>> getConversationsByShop(@Param("shopId") Long shopId);

    // 商家未读总数（USER发给SHOP且未读）
    @Select("SELECT COUNT(*) FROM customer_message WHERE shop_id = #{shopId} AND sender_type = 'USER' AND is_read = 0")
    int countUnreadByShop(@Param("shopId") Long shopId);

    // 商家按用户分组的未读数
    @Select("SELECT user_id as userId, COUNT(*) as unreadCount " +
            "FROM customer_message WHERE shop_id = #{shopId} AND sender_type = 'USER' AND is_read = 0 " +
            "GROUP BY user_id")
    List<Map<String, Object>> countUnreadByShopGroupByUser(@Param("shopId") Long shopId);

    // 商家标记某用户消息已读（标记USER发的未读消息）
    @Update("UPDATE customer_message SET is_read = 1 WHERE shop_id = #{shopId} AND user_id = #{userId} AND sender_type = 'USER' AND is_read = 0")
    int markAsReadByShop(@Param("shopId") Long shopId, @Param("userId") Long userId);

    // 获取某个用户名称（用于商家端会话列表显示）
    @Select("SELECT username FROM mall_user WHERE id = #{userId}")
    String findUsernameById(@Param("userId") Long userId);
}