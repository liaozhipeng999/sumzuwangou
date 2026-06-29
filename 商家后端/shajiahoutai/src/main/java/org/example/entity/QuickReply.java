package org.example.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("quick_reply")
public class QuickReply {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("shop_id")
    private Long shopId;

    private String content;

    private Integer sort;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
