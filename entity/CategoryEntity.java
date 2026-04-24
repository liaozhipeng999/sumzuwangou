package SpringBoot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 商品分类表
 *
 * @author Peng sunlightcs@gmail.com
 * @since 1.0.0 2025-05-20
 */
@Data
@TableName("boot_category")
public class CategoryEntity {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
	private Long id;
    /**
     * 名称
     */
	private String name;
    /**
     * 父分类id
     */
	private Long parentId;
    /**
     * 层级
     */
	private Integer catLevel;
    /**
     * 0不显示，1显示
     */
	private Integer isShow;
    /**
     * 排序
     */
	private Integer sort;
    /**
     * 图标
     */
	private String icon;
    /**
     * 统计单位
     */
	private String proUnit;
    /**
     * 商品数量
     */
	private Integer proCount;
}