package SpringBoot.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 *
 * @author Peng sunlightcs@gmail.com
 * @since 1.0.0 2025-05-18
 */
@Data
@Schema(name = "")
public class PicsDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@SchemaProperty(name = "")
	private Long goodId;

	@SchemaProperty(name = "")
	@TableId(type = IdType.AUTO)
	private Long picsId;

	@SchemaProperty(name = "")
	private String picsUrl;


}
