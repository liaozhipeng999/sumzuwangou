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
 * @since 1.0.0 2025-05-17
 */
@Data
@Schema(name = "")
public class UserDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@SchemaProperty(name = "")
	private Integer id;

	@SchemaProperty(name = "")
	private String name;

	@SchemaProperty(name = "")
	private String password;

	@SchemaProperty(name = "")
	private String email;
	@SchemaProperty(name = "")
	private String status;


}
