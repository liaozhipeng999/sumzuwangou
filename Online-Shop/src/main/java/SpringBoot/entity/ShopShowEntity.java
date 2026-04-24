package SpringBoot.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
/**
 * 
 *
 * @author Peng sunlightcs@gmail.com
 * @since 1.0.0 2025-06-08
 */
@Data
@TableName("shop_show")
public class ShopShowEntity {

    /**
     * 
     */
	private Long id;
    /**
     * 
     */
	private Long goodsId;
    /**
     * 
     */
	private String url;
}