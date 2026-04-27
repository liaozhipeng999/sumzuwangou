package SpringBoot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 
 *
 * @author Peng sunlightcs@gmail.com
 * @since 1.0.0 2025-05-21
 */
@Data
@Schema(name = "")
public class HotprodListDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@SchemaProperty(name = "")
	private Long id;

	@SchemaProperty(name = "")
	private String name;

	@SchemaProperty(name = "")
	private String word;

	@SchemaProperty(name = "")
	private Double price;

	@SchemaProperty(name = "")
	private String imageUrl;

	@SchemaProperty(name = "")
	private String tag;

	@SchemaProperty(name = "")
	private Long trueId;


}
