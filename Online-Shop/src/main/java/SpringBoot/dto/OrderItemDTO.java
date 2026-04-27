package SpringBoot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import lombok.Data;

import java.io.Serializable;


/**
 * 
 *
 * @author Peng sunlightcs@gmail.com
 * @since 1.0.0 2025-10-26
 */
@Data
@Schema(name = "")
public class OrderItemDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@SchemaProperty(name = "明细ID（自增主键）")
	private Long id;

	@SchemaProperty(name = "订单号（外键，关联order_main.order_no）")
	private String orderNo;

	@SchemaProperty(name = "商品ID")
	private String productId;

	@SchemaProperty(name = "商品名称")
	private String productName;

	@SchemaProperty(name = "商品单价（单位：分）")
	private Double unitPrice;

	@SchemaProperty(name = "购买数量")
	private Integer quantity;

	@SchemaProperty(name = "明细小计金额（单位：分 = 单价 * 数量）")
	private Double totalPrice;


}
