package SpringBoot.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 
 *
 * @author Peng sunlightcs@gmail.com
 * @since 1.0.0 2025-05-17
 */
@Data
@TableName("boot_user")
public class UserEntity {

    /**
     * 
     */
	private Integer id;
    /**
     * 
     */
	private String name;
    /**
     * 
     */
	private String password;
    /**
     * 
     */
	private String email;
    private String status;
}