package SpringBoot.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 
 *
 * @author Peng sunlightcs@gmail.com
 * @since 1.0.0 2025-10-26
 */
@Data
@TableName("order_item")
public class OrderItemEntity {

    /**
     * 明细ID（自增主键）
     */
	private Long id;
    /**
     * 订单号（外键，关联order_main.order_no）
     */
	private String orderNo;
    /**
     * 商品ID
     */
	private String productId;
    /**
     * 商品名称
     */
	private String productName;
    /**
     * 商品单价（单位：分）
     */
	private Double unitPrice;
    /**
     * 购买数量
     */
	private Integer quantity;
    /**
     * 明细小计金额（单位：分 = 单价 * 数量）
     */
	private Double totalPrice;
}