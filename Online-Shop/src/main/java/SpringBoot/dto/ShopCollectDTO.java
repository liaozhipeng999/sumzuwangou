package SpringBoot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import lombok.Data;

import java.io.Serializable;


/**
 * 
 *
 * @author Peng sunlightcs@gmail.com
 * @since 1.0.0 2025-06-11
 */
@Data
@Schema(name = "")
public class ShopCollectDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@SchemaProperty(name = "")
	private Long userid;

	@SchemaProperty(name = "")
	private Long goodid;

	@SchemaProperty(name = "")
	private Long id;


}
