package SpringBoot.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 
 *
 * @author Peng sunlightcs@gmail.com
 * @since 1.0.0 2025-06-11
 */
@Data
@TableName("shop_collect")
public class ShopCollectEntity {

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
}