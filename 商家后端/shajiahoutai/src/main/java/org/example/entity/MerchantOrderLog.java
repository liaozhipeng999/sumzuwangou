package org.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("merchant_order_log")
public class MerchantOrderLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("order_no")
    private String orderNo;

    @TableField("shop_id")
    private Long shopId;

    private String action;

    private String express;

    @TableField("tracking_no")
    private String trackingNo;

    private String remark;

    private String operator;

    @TableField("action_time")
    private LocalDateTime actionTime;
}
