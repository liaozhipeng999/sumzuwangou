package SpringBoot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 
 *
 * @author Peng sunlightcs@gmail.com
 * @since 1.0.0 2025-05-18
 */
@Data
@TableName("boot_good")
public class GoodEntity {

    /**
     * 
     */
    @TableId(type = IdType.AUTO)
	private Long goodId;
    /**
     * 
     */
	private String goodName;
    /**
     * 
     */
	private Double price;
    /**
     * 
     */
	private Long fatherId;
    /**
     * 
     */
	private String address;
    private String image;
    private  String supplier;
    private Long stock;
}