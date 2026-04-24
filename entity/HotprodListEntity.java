package SpringBoot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 
 *
 * @author Peng sunlightcs@gmail.com
 * @since 1.0.0 2025-05-21
 */
@Data
@TableName("hotprod_list")
public class HotprodListEntity {

    /**
     * 
     */@TableId(type = IdType.AUTO)
	private Long id;
    /**
     * 
     */
	private String name;
    /**
     * 
     */
	private String word;
    /**
     * 
     */
	private Double price;
    /**
     * 
     */
	private String imageUrl;
    /**
     * 
     */
	private String tag;
    /**
     * 
     */
	private Long trueId;
}