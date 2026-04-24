package SpringBoot.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 
 *
 * @author Peng sunlightcs@gmail.com
 * @since 1.0.0 2025-06-12
 */
@Data
@TableName("cart_shop")
public class CartShopEntity {

    /**
     * 
     */
	private Long userid;
    /**
     * 
     */
	private Long goodid;
    /**
     * 
     */
	private Long id;
    /**
     * 
     */
	private Long number;
    private boolean selected;
    @TableField(exist = false)
    private GoodEntity goodEntity;
}