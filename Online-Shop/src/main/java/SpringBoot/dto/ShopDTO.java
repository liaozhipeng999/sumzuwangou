package SpringBoot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import lombok.Data;

import java.io.Serializable;

import java.math.BigDecimal;

/**
 * 
 *
 * @author Peng sunlightcs@gmail.com
 * @since 1.0.0 2025-05-20
 */
@Data
@Schema(name = "")
public class ShopDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@SchemaProperty(name = "")
	private Integer id;

	@SchemaProperty(name = "")
	private String name;

	@SchemaProperty(name = "")
	private String merchant;

	@SchemaProperty(name = "")
	private BigDecimal price;

	@SchemaProperty(name = "单位")
	private String volume;

	@SchemaProperty(name = "")
	private Integer inventory;

	@SchemaProperty(name = "")
	private String imgPath;

	@SchemaProperty(name = "")
	private Long categoryId;

	@SchemaProperty(name = "")
	private String mes;


}
