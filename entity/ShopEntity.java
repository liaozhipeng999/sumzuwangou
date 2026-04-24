package SpringBoot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 
 *
 * @author Peng sunlightcs@gmail.com
 * @since 1.0.0 2025-05-20
 */
@Data
@TableName("boot_shop")
public class ShopEntity {

    /**
     * 
     */
    @TableId(type = IdType.AUTO)
	private Integer id;
    /**
     * 
     */
	private String name;
    /**
     * 
     */
	private String merchant;
    /**
     * 
     */
	private BigDecimal price;
    /**
     * 单位
     */
	private String volume;
    /**
     * 
     */
	private Integer inventory;
    /**
     * 
     */
	private String imgPath;
    /**
     * 
     */
	private Long categoryId;
    /**
     * 
     */
	private String mes;
}