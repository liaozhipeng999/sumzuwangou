package com.mall.service.impl;

import com.mall.entity.CustomerMessage;
import com.mall.entity.Merchant;
import com.mall.entity.QuickReply;
import com.mall.entity.TermProducts;
import com.mall.mapper.CustomerMessageMapper;
import com.mall.mapper.MerchantMapper;
import com.mall.mapper.ProductMapper;
import com.mall.mapper.QuickReplyMapper;
import com.mall.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private MerchantMapper merchantMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CustomerMessageMapper messageMapper;

    @Autowired
    private QuickReplyMapper quickReplyMapper;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public Map<String, Object> getShopInfo(Long shopId) {
        Map<String, Object> result = new HashMap<>();
        
        Merchant merchant = merchantMapper.selectById(shopId);
        
        if (merchant != null) {
            result.put("shopId", merchant.getId());
            result.put("shopName", merchant.getMerchantName());
            result.put("shopLogo", merchant.getMerchantLogo());
            result.put("onlineStatus", "ONLINE");
            result.put("lastOnlineTime", LocalDateTime.now().format(FORMATTER));
            
            List<String> tags = new ArrayList<>();
            tags.add("品牌官方正品");
            tags.add("全店总售900万+件");
            tags.add("正品险");
            result.put("tags", tags);
            
            result.put("serviceRating", 4.9);
            result.put("responseTime", "5分钟内");
        }
        
        return result;
    }

    @Override
    public Map<String, Object> getProductInfo(Long productId) {
        Map<String, Object> result = new HashMap<>();
        
        TermProducts product = productMapper.selectById(productId);
        
        if (product != null) {
            result.put("productId", product.getId());
            result.put("productName", product.getProductName());
            result.put("productImage", product.getMainImage());
            result.put("price", String.format("%.2f", product.getPrice()));
            result.put("originalPrice", String.format("%.2f", product.getOriginalPrice()));
            result.put("sales", product.getSales());
            result.put("stock", product.getStock());
        }
        
        return result;
    }

    @Override
    public Map<String, Object> getMessages(Long shopId, Long userId, int page, int pageSize) {
        Map<String, Object> result = new HashMap<>();
        
        int offset = (page - 1) * pageSize;
        int total = messageMapper.countByUserAndShop(userId, shopId);
        List<CustomerMessage> messages = messageMapper.findByUserAndShop(userId, shopId, offset, pageSize);
        
        List<Map<String, Object>> messageList = new ArrayList<>();
        for (CustomerMessage message : messages) {
            Map<String, Object> msg = new HashMap<>();
            msg.put("messageId", message.getMessageId());
            msg.put("senderId", message.getSenderId());
            msg.put("senderType", message.getSenderType());
            msg.put("content", message.getContent());
            msg.put("sendTime", message.getSendTime().format(FORMATTER));
            msg.put("messageType", message.getMessageType());
            messageList.add(msg);
        }
        
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);
        result.put("list", messageList);
        
        return result;
    }

    @Override
    public Map<String, Object> sendMessage(Long userId, Long shopId, Long productId, String content, String messageType) {
        Map<String, Object> result = new HashMap<>();
        
        CustomerMessage message = new CustomerMessage();
        message.setUserId(userId);
        message.setShopId(shopId);
        message.setProductId(productId);
        message.setSenderType("USER");
        message.setContent(content);
        message.setMessageType(messageType);
        message.setSendTime(LocalDateTime.now());
        message.setIsRead(0);
        message.setCreatedAt(LocalDateTime.now());
        
        messageMapper.insert(message);
        
        result.put("messageId", message.getMessageId());
        result.put("sendTime", message.getSendTime().format(FORMATTER));

        // 自动回复：仅在店铺从未回复过该用户时，才触发一次快捷回复
        int shopMessageCount = messageMapper.countShopMessages(userId, shopId);
        if (shopMessageCount == 0) {
            String autoReply = messageMapper.findAutoReply(shopId);
            if (autoReply != null && !autoReply.isEmpty()) {
                CustomerMessage reply = new CustomerMessage();
                reply.setUserId(userId);
                reply.setShopId(shopId);
                reply.setProductId(null);
                reply.setSenderType("SHOP");
                reply.setContent(autoReply);
                reply.setMessageType("TEXT");
                reply.setSendTime(LocalDateTime.now());
                reply.setIsRead(0);
                reply.setCreatedAt(LocalDateTime.now());
                messageMapper.insert(reply);

                Map<String, Object> autoReplyData = new HashMap<>();
                autoReplyData.put("messageId", reply.getMessageId());
                autoReplyData.put("content", autoReply);
                autoReplyData.put("sendTime", reply.getSendTime().format(FORMATTER));
                result.put("autoReply", autoReplyData);
            }
        }
        
        return result;
    }

    @Override
    public List<QuickReply> getQuickReplies(Long shopId) {
        return quickReplyMapper.findByShopId(shopId);
    }

    @Override
    public List<Map<String, Object>> getConversations(Long userId) {
        List<Map<String, Object>> conversations = messageMapper.getConversations(userId);
        // 补充店铺信息
        for (Map<String, Object> conv : conversations) {
            Long shopId = ((Number) conv.get("shopId")).longValue();
            Merchant merchant = merchantMapper.selectById(shopId);
            if (merchant != null) {
                conv.put("shopName", merchant.getMerchantName());
                conv.put("shopLogo", merchant.getMerchantLogo());
            }
        }
        return conversations;
    }

    @Override
    public int getUnreadCount(Long userId) {
        return messageMapper.countUnread(userId);
    }

    @Override
    public List<Map<String, Object>> getUnreadCountByShop(Long userId) {
        return messageMapper.countUnreadByShopGroupByShop(userId);
    }

    @Override
    public void markAsRead(Long userId, Long shopId) {
        messageMapper.markAsRead(userId, shopId);
    }

    @Override
    public boolean deleteMessage(Long messageId, Long userId) {
        return messageMapper.deleteMessage(messageId, userId) > 0;
    }

    @Override
    public void clearMessages(Long userId, Long shopId) {
        messageMapper.clearMessages(userId, shopId);
    }

    // ============ 商家端实现 ============

    @Override
    public Map<String, Object> sendMerchantMessage(Long shopId, Long userId, String content, String messageType) {
        Map<String, Object> result = new HashMap<>();

        CustomerMessage message = new CustomerMessage();
        message.setUserId(userId);
        message.setShopId(shopId);
        message.setProductId(null);
        message.setSenderType("SHOP");
        message.setContent(content);
        message.setMessageType(messageType != null ? messageType : "TEXT");
        message.setSendTime(LocalDateTime.now());
        message.setIsRead(0);
        message.setCreatedAt(LocalDateTime.now());

        messageMapper.insert(message);

        result.put("messageId", message.getMessageId());
        result.put("sendTime", message.getSendTime().format(FORMATTER));
        return result;
    }

    @Override
    public List<Map<String, Object>> getMerchantConversations(Long shopId) {
        List<Map<String, Object>> conversations = messageMapper.getConversationsByShop(shopId);
        // 补充用户名称
        for (Map<String, Object> conv : conversations) {
            Long userId = ((Number) conv.get("userId")).longValue();
            String username = messageMapper.findUsernameById(userId);
            conv.put("userName", username != null ? username : "用户" + userId);
        }
        return conversations;
    }

    @Override
    public int getMerchantUnreadCount(Long shopId) {
        return messageMapper.countUnreadByShop(shopId);
    }

    @Override
    public List<Map<String, Object>> getMerchantUnreadByUser(Long shopId) {
        return messageMapper.countUnreadByShopGroupByUser(shopId);
    }

    @Override
    public void markAsReadByShop(Long shopId, Long userId) {
        messageMapper.markAsReadByShop(shopId, userId);
    }

    // ============ 快捷回复管理实现 ============

    @Override
    public void addQuickReply(Long shopId, String content, Integer sort) {
        QuickReply quickReply = new QuickReply();
        quickReply.setShopId(shopId);
        quickReply.setContent(content);
        quickReply.setSort(sort != null ? sort : 0);
        quickReplyMapper.insert(quickReply);
    }

    @Override
    public boolean deleteQuickReply(Long id, Long shopId) {
        return quickReplyMapper.deleteById(id, shopId) > 0;
    }
}