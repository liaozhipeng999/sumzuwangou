package com.mall.service.impl;

import com.mall.entity.Message;
import com.mall.mapper.MessageMapper;
import com.mall.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MessageServiceImpl implements MessageService {

    // 消息类型定义
    private static final Map<String, String[]> MESSAGE_TYPES = new LinkedHashMap<>() {{
        put("logistics", new String[]{"物流通知", "logistics"});
        put("transaction", new String[]{"交易消息", "bill"});
        put("system", new String[]{"系统通知", "bell"});
        put("promotion", new String[]{"活动优惠", "gift"});
        put("merchant", new String[]{"商家消息", "chat"});
    }};

    @Autowired
    private MessageMapper messageMapper;

    @Override
    public List<Map<String, Object>> getMessageSummary(Long userId) {
        List<Map<String, Object>> summary = new ArrayList<>();

        for (Map.Entry<String, String[]> entry : MESSAGE_TYPES.entrySet()) {
            String type = entry.getKey();
            String label = entry.getValue()[0];
            String icon = entry.getValue()[1];

            // 获取未读数
            int unreadCount = messageMapper.countUnreadByUserIdAndType(userId, type);

            // 获取最新消息
            Message latest = messageMapper.findLatestByUserIdAndType(userId, type);

            Map<String, Object> item = new HashMap<>();
            item.put("type", type);
            item.put("label", label);
            item.put("icon", icon);
            item.put("unreadCount", unreadCount);
            item.put("latestMessage", latest);
            summary.add(item);
        }

        return summary;
    }

    @Override
    public int getUnreadCount(Long userId) {
        return messageMapper.countUnreadByUserId(userId);
    }

    @Override
    public Map<String, Object> getMessagesByType(Long userId, String type, int page, int pageSize) {
        // 验证消息类型
        if (!MESSAGE_TYPES.containsKey(type)) {
            throw new IllegalArgumentException("无效的消息类型");
        }

        // SQL 层分页，避免加载全部数据
        int total = messageMapper.countByUserIdAndType(userId, type);
        int offset = (page - 1) * pageSize;
        List<Message> pageMessages = offset < total
                ? messageMapper.findByUserIdAndTypePaged(userId, type, offset, pageSize)
                : new ArrayList<>();

        Map<String, Object> result = new HashMap<>();
        result.put("list", pageMessages);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);

        return result;
    }

    @Override
    public boolean markAsRead(Long id, Long userId) {
        // 验证消息是否存在且属于该用户
        Message message = messageMapper.selectById(id);
        if (message == null || !message.getUserId().equals(userId)) {
            return false;
        }
        return messageMapper.markAsRead(id) > 0;
    }

    @Override
    public boolean markAllAsReadByType(Long userId, String type) {
        // 验证消息类型
        if (!MESSAGE_TYPES.containsKey(type)) {
            throw new IllegalArgumentException("无效的消息类型");
        }
        return messageMapper.markAllAsReadByType(userId, type) > 0;
    }

    @Override
    public boolean markAllAsRead(Long userId) {
        return messageMapper.markAllAsRead(userId) > 0;
    }

    @Override
    public boolean deleteMessage(Long id, Long userId) {
        Message message = messageMapper.selectById(id);
        if (message == null || !message.getUserId().equals(userId)) {
            return false;
        }
        return messageMapper.softDelete(id, userId) > 0;
    }

    @Override
    public int deleteMessagesByType(Long userId, String type) {
        if (!MESSAGE_TYPES.containsKey(type)) {
            throw new IllegalArgumentException("无效的消息类型");
        }
        return messageMapper.softDeleteByType(userId, type);
    }
}
