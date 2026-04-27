package SpringBoot.dto;

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
public class GoodDTO implements Serializable {
    private static final long serialVersionUID = 1L;
	@SchemaProperty(name = "")
	private Long goodId;
	@SchemaProperty(name = "")
	private String goodName;
	@SchemaProperty(name = "")
	private Double price;
	@SchemaProperty(name = "")
	private Long fatherId;
	@SchemaProperty(name = "")
	private String address;
	@SchemaProperty(name = "")
	private String image;
    @SchemaProperty(name = "")
    private String supplier;




}
