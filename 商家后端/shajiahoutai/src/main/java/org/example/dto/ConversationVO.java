package org.example.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ConversationVO {

    /** 商家视角：用户ID；用户视角：店铺ID */
    private Long shopId;
    private String shopName;
    private String shopLogo;

    /** 用户视角字段 */
    private Long userId;
    private String userName;

    private String lastMessage;
    private String lastMessageType;
    private LocalDateTime lastSendTime;
    private String lastSenderType;
    private Integer unreadCount;
}
