package org.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

@Controller
public class WebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * 用户订阅新消息通知
     * 用户订阅 /topic/user/{userId}/messages
     * 商家订阅 /topic/shop/{shopId}/messages
     */

    /**
     * 推送新消息给指定用户
     */
    public void notifyUser(Long userId, Map<String, Object> message) {
        messagingTemplate.convertAndSend("/topic/user/" + userId + "/messages", message);
    }

    /**
     * 推送新消息给指定商家
     */
    public void notifyShop(Long shopId, Map<String, Object> message) {
        messagingTemplate.convertAndSend("/topic/shop/" + shopId + "/messages", message);
    }

    /**
     * 推送未读数更新给商家
     */
    public void notifyShopUnreadCount(Long shopId, int unreadCount) {
        Map<String, Object> data = new HashMap<>();
        data.put("type", "unread_count");
        data.put("shopId", shopId);
        data.put("unreadCount", unreadCount);
        messagingTemplate.convertAndSend("/topic/shop/" + shopId + "/unread", data);
    }

    /**
     * 推送未读数更新给用户
     */
    public void notifyUserUnreadCount(Long userId, int unreadCount) {
        Map<String, Object> data = new HashMap<>();
        data.put("type", "unread_count");
        data.put("userId", userId);
        data.put("unreadCount", unreadCount);
        messagingTemplate.convertAndSend("/topic/user/" + userId + "/unread", data);
    }
}
