package SpringBoot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import lombok.Data;

import java.io.Serializable;


/**
 * 商品分类表
 *
 * @author Peng sunlightcs@gmail.com
 * @since 1.0.0 2025-05-20
 */
@Data
@Schema(name = "商品分类表")
public class CategoryDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@SchemaProperty(name = "id")
	private Long id;

	@SchemaProperty(name = "名称")
	private String name;

	@SchemaProperty(name = "父分类id")
	private Long parentId;

	@SchemaProperty(name = "层级")
	private Integer catLevel;

	@SchemaProperty(name = "0不显示，1显示")
	private Integer isShow;

	@SchemaProperty(name = "排序")
	private Integer sort;

	@SchemaProperty(name = "图标")
	private String icon;

	@SchemaProperty(name = "统计单位")
	private String proUnit;

	@SchemaProperty(name = "商品数量")
	private Integer proCount;


}
