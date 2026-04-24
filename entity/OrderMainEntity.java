package SpringBoot.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 *
 * @author Peng sunlightcs@gmail.com
 * @since 1.0.0 2025-10-26
 */
@Data
@TableName("order_main")
public class OrderMainEntity implements Serializable {

    /**
     * 订单号（唯一，主键）
     */
	private String orderNo;
    /**
     * 用户ID
     */
	private String userId;
    /**
     * 订单总金额（单位：分）
     */
	private Double totalAmount;
    /**
     * 商品总数量
     */
	private Integer totalQuantity;
    /**
     * 订单状态（对应OrderStatus枚举：PENDING_PAYMENT/待付款、PENDING_DELIVERY/待发货等）
     */
	private String status;
    /**
     * 订单创建时间
     */
	private Date createTime;
    /**
     * 支付时间（待付款→待发货时更新）
     */
	private Date payTime;
    /**
     * 发货时间（待发货→已发货时更新）
     */
	private Date shipTime;
    /**
     * 完成时间（已发货→已完成时更新）
     */
	private Date completeTime;
    /**
     * 取消时间（订单取消时更新）
     */
	private Date cancelTime;
}