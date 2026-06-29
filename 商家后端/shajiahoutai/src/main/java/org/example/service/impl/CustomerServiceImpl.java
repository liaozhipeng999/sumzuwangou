package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.dto.*;
import org.example.entity.CustomerMessage;
import org.example.entity.QuickReply;
import org.example.entity.TermMerchant;
import org.example.entity.TermProduct;
import org.example.mapper.CustomerMessageMapper;
import org.example.mapper.QuickReplyMapper;
import org.example.mapper.TermMerchantMapper;
import org.example.mapper.ProductMapper;
import org.example.controller.WebSocketController;
import org.example.config.SensitiveWordFilter;
import org.example.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerMessageMapper messageMapper;

    @Autowired
    private QuickReplyMapper quickReplyMapper;

    @Autowired
    private TermMerchantMapper merchantMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private SensitiveWordFilter sensitiveWordFilter;

    @Autowired
    private WebSocketController webSocketController;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // ========== 用户端接口 ==========

    @Override
    public Map<String, Object> userSendMessage(SendMessageDTO dto) {
        // 保存用户消息
        CustomerMessage message = new CustomerMessage();
        message.setUserId(dto.getUserId());
        message.setShopId(dto.getShopId());
        message.setProductId(dto.getProductId());
        message.setSenderType("USER");
        // 敏感词过滤
        String content = dto.getContent();
        if (content != null && (dto.getMessageType() == null || "TEXT".equals(dto.getMessageType()))) {
            content = sensitiveWordFilter.filter(content);
        }
        message.setContent(content);
        message.setMessageType(dto.getMessageType() != null ? dto.getMessageType() : "TEXT");
        message.setSendTime(LocalDateTime.now());
        message.setIsRead(0);
        message.setCreatedAt(LocalDateTime.now());
        messageMapper.insert(message);

        Map<String, Object> result = new HashMap<>();
        result.put("messageId", message.getMessageId());
        result.put("sendTime", message.getSendTime().format(TIME_FORMATTER));

        // WebSocket 推送新消息通知给商家
        Map<String, Object> wsMsg = new HashMap<>();
        wsMsg.put("type", "new_message");
        wsMsg.put("messageId", message.getMessageId());
        wsMsg.put("userId", dto.getUserId());
        wsMsg.put("shopId", dto.getShopId());
        wsMsg.put("senderType", "USER");
        wsMsg.put("content", message.getContent());
        wsMsg.put("messageType", message.getMessageType());
        wsMsg.put("sendTime", message.getSendTime().format(TIME_FORMATTER));
        webSocketController.notifyShop(dto.getShopId(), wsMsg);

        // 检查自动回复（快捷回复）
        LambdaQueryWrapper<QuickReply> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QuickReply::getShopId, dto.getShopId())
               .orderByAsc(QuickReply::getSort)
               .last("LIMIT 1");
        QuickReply autoReply = quickReplyMapper.selectOne(wrapper);

        if (autoReply != null) {
            CustomerMessage replyMsg = new CustomerMessage();
            replyMsg.setUserId(dto.getUserId());
            replyMsg.setShopId(dto.getShopId());
            replyMsg.setProductId(dto.getProductId());
            replyMsg.setSenderType("SHOP");
            replyMsg.setContent(autoReply.getContent());
            replyMsg.setMessageType("TEXT");
            replyMsg.setSendTime(LocalDateTime.now());
            replyMsg.setIsRead(0);
            replyMsg.setCreatedAt(LocalDateTime.now());
            messageMapper.insert(replyMsg);

            Map<String, Object> autoReplyMap = new HashMap<>();
            autoReplyMap.put("messageId", replyMsg.getMessageId());
            autoReplyMap.put("content", replyMsg.getContent());
            autoReplyMap.put("sendTime", replyMsg.getSendTime().format(TIME_FORMATTER));
            result.put("autoReply", autoReplyMap);
        }

        return result;
    }

    @Override
    public List<ConversationVO> getUserConversations(Long userId) {
        List<Map<String, Object>> rows = messageMapper.getConversationsByUser(userId);
        return rows.stream().map(row -> {
            ConversationVO vo = new ConversationVO();
            vo.setShopId(toLong(row.get("shopId")));
            vo.setLastMessage((String) row.get("lastMessage"));
            vo.setLastMessageType((String) row.get("lastMessageType"));
            vo.setLastSenderType((String) row.get("lastSenderType"));
            vo.setUnreadCount(toInt(row.get("unreadCount")));
            if (row.get("lastSendTime") != null) {
                vo.setLastSendTime(toLocalDateTime(row.get("lastSendTime")));
            }
            // 补充店铺信息
            TermMerchant merchant = merchantMapper.selectById(vo.getShopId());
            if (merchant != null) {
                vo.setShopName(merchant.getMerchantName());
                vo.setShopLogo(merchant.getMerchantLogo());
            }
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public UnreadCountVO getUserUnreadCount(Long userId) {
        UnreadCountVO vo = new UnreadCountVO();
        vo.setTotal(messageMapper.countUnreadByUser(userId));

        List<Map<String, Object>> groups = messageMapper.countUnreadByUserGroupByShop(userId);
        List<UnreadCountVO.ShopUnread> shops = groups.stream().map(row -> {
            UnreadCountVO.ShopUnread su = new UnreadCountVO.ShopUnread();
            su.setShopId(toLong(row.get("shopId")));
            su.setUnreadCount(toInt(row.get("unreadCount")));
            return su;
        }).collect(Collectors.toList());
        vo.setShops(shops);
        return vo;
    }

    @Override
    public void userMarkAsRead(Long userId, Long shopId) {
        messageMapper.markAsReadByUser(userId, shopId);
    }

    @Override
    public Map<String, Object> getMessages(Long shopId, Long userId, int page, int pageSize) {
        Page<CustomerMessage> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<CustomerMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CustomerMessage::getShopId, shopId)
               .eq(CustomerMessage::getUserId, userId)
               .orderByDesc(CustomerMessage::getSendTime);

        Page<CustomerMessage> result = messageMapper.selectPage(pageParam, wrapper);

        List<MessageVO> list = result.getRecords().stream().map(msg -> {
            MessageVO vo = new MessageVO();
            vo.setMessageId(msg.getMessageId());
            vo.setSenderType(msg.getSenderType());
            vo.setSenderId("USER".equals(msg.getSenderType()) ? msg.getUserId() : msg.getShopId());
            vo.setContent(msg.getContent());
            vo.setSendTime(msg.getSendTime());
            vo.setMessageType(msg.getMessageType());
            return vo;
        }).collect(Collectors.toList());

        Map<String, Object> data = new HashMap<>();
        data.put("total", result.getTotal());
        data.put("page", page);
        data.put("pageSize", pageSize);
        data.put("list", list);
        return data;
    }

    @Override
    public void deleteMessage(Long messageId, Long userId) {
        CustomerMessage msg = messageMapper.selectById(messageId);
        if (msg == null || !msg.getUserId().equals(userId)) {
            throw new RuntimeException("消息不存在或无权删除");
        }
        messageMapper.deleteById(messageId);
    }

    @Override
    public void clearMessages(Long userId, Long shopId) {
        messageMapper.clearMessages(userId, shopId);
    }

    @Value("${file.upload-dir:./uploads/images}")
    private String uploadDir;

    @Value("${file.base-url:http://localhost:8080/images}")
    private String baseUrl;

    @Override
    public Map<String, Object> uploadImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("请选择图片");
        }
        String originalName = file.getOriginalFilename();
        if (originalName == null || !originalName.matches("(?i).*\\.(jpg|jpeg|png|gif|webp)$")) {
            throw new RuntimeException("仅支持 jpg/png/gif/webp 格式");
        }
        // 生成唯一文件名
        String ext = originalName.substring(originalName.lastIndexOf('.'));
        String fileName = UUID.randomUUID().toString().replace("-", "") + ext;

        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            file.transferTo(new File(dir, fileName));
        } catch (IOException e) {
            throw new RuntimeException("图片上传失败: " + e.getMessage());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("url", baseUrl + "/" + fileName);
        result.put("fileName", fileName);
        return result;
    }

    @Override
    public void markMessageAsRead(Long messageId) {
        CustomerMessage msg = messageMapper.selectById(messageId);
        if (msg == null) {
            throw new RuntimeException("消息不存在");
        }
        if (msg.getIsRead() == 0) {
            msg.setIsRead(1);
            messageMapper.updateById(msg);
        }
    }

    @Override
    public Map<String, Object> getShopInfo(Long shopId) {
        TermMerchant merchant = merchantMapper.selectById(shopId);
        if (merchant == null) {
            throw new RuntimeException("店铺不存在");
        }
        Map<String, Object> data = new HashMap<>();
        data.put("shopId", merchant.getId());
        data.put("shopName", merchant.getMerchantName());
        data.put("shopLogo", merchant.getMerchantLogo());
        data.put("merchantBrief", merchant.getMerchantBrief());
        data.put("contactName", merchant.getContactName());
        data.put("contactPhone", merchant.getContactPhone());
        data.put("email", merchant.getEmail());
        data.put("merchantLevel", merchant.getMerchantLevel());
        data.put("status", merchant.getStatus());
        data.put("createdAt", merchant.getCreatedAt());
        return data;
    }

    @Override
    public Map<String, Object> getProductInfo(Long productId) {
        TermProduct product = productMapper.selectById(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        Map<String, Object> data = new HashMap<>();
        data.put("productId", product.getId());
        data.put("productName", product.getProductName());
        data.put("productImage", product.getMainImage());
        data.put("price", product.getPrice());
        data.put("originalPrice", product.getOriginalPrice());
        data.put("sales", product.getSales());
        data.put("stock", product.getStock());
        return data;
    }

    @Override
    public List<Map<String, Object>> getQuickReplies(Long shopId) {
        LambdaQueryWrapper<QuickReply> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QuickReply::getShopId, shopId)
               .orderByAsc(QuickReply::getSort);
        return quickReplyMapper.selectList(wrapper).stream().map(qr -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", qr.getId());
            map.put("content", qr.getContent());
            return map;
        }).collect(Collectors.toList());
    }

    // ========== 商家端接口 ==========

    @Override
    public Map<String, Object> merchantSendMessage(SendMessageDTO dto) {
        CustomerMessage message = new CustomerMessage();
        message.setUserId(dto.getUserId());
        message.setShopId(dto.getShopId());
        message.setProductId(dto.getProductId());
        message.setSenderType("SHOP");
        // 敏感词过滤
        String merchantContent = dto.getContent();
        if (merchantContent != null && (dto.getMessageType() == null || "TEXT".equals(dto.getMessageType()))) {
            merchantContent = sensitiveWordFilter.filter(merchantContent);
        }
        message.setContent(merchantContent);
        message.setMessageType(dto.getMessageType() != null ? dto.getMessageType() : "TEXT");
        message.setSendTime(LocalDateTime.now());
        message.setIsRead(0);
        message.setCreatedAt(LocalDateTime.now());
        messageMapper.insert(message);

        Map<String, Object> result = new HashMap<>();
        result.put("messageId", message.getMessageId());
        result.put("sendTime", message.getSendTime().format(TIME_FORMATTER));

        // WebSocket 推送新消息通知给用户
        Map<String, Object> wsMsg = new HashMap<>();
        wsMsg.put("type", "new_message");
        wsMsg.put("messageId", message.getMessageId());
        wsMsg.put("userId", dto.getUserId());
        wsMsg.put("shopId", dto.getShopId());
        wsMsg.put("senderType", "SHOP");
        wsMsg.put("content", message.getContent());
        wsMsg.put("messageType", message.getMessageType());
        wsMsg.put("sendTime", message.getSendTime().format(TIME_FORMATTER));
        webSocketController.notifyUser(dto.getUserId(), wsMsg);

        return result;
    }

    @Override
    public List<ConversationVO> getMerchantConversations(Long shopId) {
        List<Map<String, Object>> rows = messageMapper.getConversationsByShop(shopId);
        return rows.stream().map(row -> {
            ConversationVO vo = new ConversationVO();
            vo.setUserId(toLong(row.get("userId")));
            vo.setLastMessage((String) row.get("lastMessage"));
            vo.setLastMessageType((String) row.get("lastMessageType"));
            vo.setLastSenderType((String) row.get("lastSenderType"));
            vo.setUnreadCount(toInt(row.get("unreadCount")));
            if (row.get("lastSendTime") != null) {
                vo.setLastSendTime(toLocalDateTime(row.get("lastSendTime")));
            }
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public UnreadCountVO getMerchantUnreadCount(Long shopId) {
        UnreadCountVO vo = new UnreadCountVO();
        vo.setTotal(messageMapper.countUnreadByShop(shopId));

        List<Map<String, Object>> groups = messageMapper.countUnreadByShopGroupByUser(shopId);
        List<UnreadCountVO.UserUnread> users = groups.stream().map(row -> {
            UnreadCountVO.UserUnread uu = new UnreadCountVO.UserUnread();
            uu.setUserId(toLong(row.get("userId")));
            uu.setUnreadCount(toInt(row.get("unreadCount")));
            return uu;
        }).collect(Collectors.toList());
        vo.setUsers(users);
        return vo;
    }

    @Override
    public void merchantMarkAsRead(Long shopId, Long userId) {
        messageMapper.markAsReadByShop(shopId, userId);
    }

    @Override
    public Map<String, Object> addQuickReply(QuickReplyDTO dto) {
        QuickReply reply = new QuickReply();
        reply.setShopId(dto.getShopId());
        reply.setContent(dto.getContent());
        reply.setSort(dto.getSort());
        reply.setCreatedAt(LocalDateTime.now());
        quickReplyMapper.insert(reply);

        Map<String, Object> result = new HashMap<>();
        result.put("id", reply.getId());
        result.put("content", reply.getContent());
        result.put("sort", reply.getSort());
        return result;
    }

    @Override
    public void deleteQuickReply(Long id, Long shopId) {
        LambdaQueryWrapper<QuickReply> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QuickReply::getId, id).eq(QuickReply::getShopId, shopId);
        quickReplyMapper.delete(wrapper);
    }

    @Override
    public List<Map<String, Object>> getAllQuickReplies(Long shopId) {
        return getQuickReplies(shopId);
    }

    // ========== 工具方法 ==========

    private Long toLong(Object value) {
        if (value instanceof Long) return (Long) value;
        if (value instanceof Number) return ((Number) value).longValue();
        if (value instanceof String) return Long.parseLong((String) value);
        return null;
    }

    private Integer toInt(Object value) {
        if (value instanceof Integer) return (Integer) value;
        if (value instanceof Number) return ((Number) value).intValue();
        if (value instanceof String) return Integer.parseInt((String) value);
        return 0;
    }

    private LocalDateTime toLocalDateTime(Object value) {
        if (value instanceof LocalDateTime) return (LocalDateTime) value;
        if (value instanceof java.sql.Timestamp) return ((java.sql.Timestamp) value).toLocalDateTime();
        if (value instanceof String) return LocalDateTime.parse((String) value, TIME_FORMATTER);
        return null;
    }
}
