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
 * @since 1.0.0 2025-06-15
 */
@Data
@Schema(name = "")
public class LocationDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@SchemaProperty(name = "")
	private Long id;

	@SchemaProperty(name = "")
	private Long useid;

	@SchemaProperty(name = "")
	private String phone;

	@SchemaProperty(name = "")
	private String name;

	@SchemaProperty(name = "")
	private String address;

	@SchemaProperty(name = "")
	private String detail;

	@SchemaProperty(name = "")
	private String state;


}
