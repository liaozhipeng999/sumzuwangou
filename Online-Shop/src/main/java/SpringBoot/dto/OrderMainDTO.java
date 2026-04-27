package SpringBoot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
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
@Schema(name = "")
public class OrderMainDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@SchemaProperty(name = "订单号（唯一，主键）")
	private String orderNo;

	@SchemaProperty(name = "用户ID")
	private String userId;

	@SchemaProperty(name = "订单总金额（单位：分）")
	private Double totalAmount;

	@SchemaProperty(name = "商品总数量")
	private Integer totalQuantity;

	@SchemaProperty(name = "订单状态（对应OrderStatus枚举：PENDING_PAYMENT/待付款、PENDING_DELIVERY/待发货等）")
	private String status;

	@SchemaProperty(name = "订单创建时间")
	private Date createTime;

	@SchemaProperty(name = "支付时间（待付款→待发货时更新）")
	private Date payTime;

	@SchemaProperty(name = "发货时间（待发货→已发货时更新）")
	private Date shipTime;

	@SchemaProperty(name = "完成时间（已发货→已完成时更新）")
	private Date completeTime;

	@SchemaProperty(name = "取消时间（订单取消时更新）")
	private Date cancelTime;


}
