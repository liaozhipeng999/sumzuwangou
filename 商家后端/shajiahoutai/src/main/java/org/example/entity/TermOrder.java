package org.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("orders")
public class TermOrder {

    public static final int DB_CANCELLED = 0;
    public static final int DB_PENDING = 1;
    public static final int DB_PAID = 2;
    public static final int DB_SHIPPED = 3;
    public static final int DB_COMPLETED = 4;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("order_no")
    private String orderNo;

    @TableField("user_id")
    private Long userId;

    @TableField("merchant_id")
    private Long merchantId;

    @TableField("address_id")
    private Long addressId;

    @TableField("total_amount")
    private BigDecimal totalAmount;

    @TableField("discount_amount")
    private BigDecimal discountAmount;

    @TableField("coupon_amount")
    private BigDecimal couponAmount;

    @TableField("pay_amount")
    private BigDecimal payAmount;

    private Integer status;

    @TableField("pay_time")
    private LocalDateTime payTime;

    @TableField("ship_time")
    private LocalDateTime shipTime;

    @TableField("receive_time")
    private LocalDateTime receiveTime;

    @TableField("cancel_time")
    private LocalDateTime cancelTime;

    @TableField("ship_company")
    private String shipCompany;

    @TableField("ship_no")
    private String shipNo;

    @TableField("ship_remark")
    private String shipRemark;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

    public static int toDbStatus(String fe) {
        if (fe == null) return -1;
        switch (fe) {
            case "pending": return DB_PENDING;
            case "paid": return DB_PAID;
            case "shipped": return DB_SHIPPED;
            case "completed": return DB_COMPLETED;
            case "cancelled": return DB_CANCELLED;
            default: return -1;
        }
    }

    public static String toFeStatus(int db) {
        switch (db) {
            case DB_PENDING: return "pending";
            case DB_PAID: return "paid";
            case DB_SHIPPED: return "shipped";
            case DB_COMPLETED: return "completed";
            case DB_CANCELLED: return "cancelled";
            default: return "pending";
        }
    }
}
