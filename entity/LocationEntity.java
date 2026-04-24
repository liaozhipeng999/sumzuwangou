package SpringBoot.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 
 *
 * @author Peng sunlightcs@gmail.com
 * @since 1.0.0 2025-06-15
 */
@Data
@TableName("location")
public class LocationEntity {

    /**
     * 
     */
    @TableId
	private Long id;
    /**
     * 
     */
	private Long useid;
    /**
     * 
     */
	private String phone;
    /**
     * 
     */
	private String name;
    /**
     * 
     */
	private String address;
    /**
     * 
     */
	private String detail;
    /**
     * 
     */
	private String state;
}