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
@TableName("pics")
public class PicsEntity {

    /**
     * 
     */  @TableId(type = IdType.AUTO)
	private Long goodId;
    /**
     * 
     */
	private Long picsId;
    /**
     * 
     */
	private String picsUrl;
}