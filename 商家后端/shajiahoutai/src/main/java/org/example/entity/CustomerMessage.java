package org.example.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("customer_message")
public class CustomerMessage {

    @TableId(type = IdType.AUTO)
    private Long messageId;

    @TableField("user_id")
    private Long userId;

    @TableField("shop_id")
    private Long shopId;

    @TableField("product_id")
    private Long productId;

    @TableField("sender_type")
    private String senderType;

    private String content;

    @TableField("message_type")
    private String messageType;

    @TableField("send_time")
    private LocalDateTime sendTime;

    @TableField("is_read")
    private Integer isRead;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
