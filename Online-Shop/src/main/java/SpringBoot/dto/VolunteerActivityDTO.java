package SpringBoot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 志愿活动信息表
 *
 * @author Peng sunlightcs@gmail.com
 * @since 1.0.0 2025-06-19
 */
@Data
@Schema(name = "志愿活动信息表")
public class VolunteerActivityDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@SchemaProperty(name = "活动编号（UUID）")
	private String activityId;

	@SchemaProperty(name = "活动标题")
	private String title;

	@SchemaProperty(name = "活动地点")
	private String location;

	@SchemaProperty(name = "开始时间")
	private Date startTime;

	@SchemaProperty(name = "结束时间")
	private Date endTime;

	@SchemaProperty(name = "招募人数")
	private Integer recruitNumber;

	@SchemaProperty(name = "招募岗位描述")
	private String jobDescription;

	@SchemaProperty(name = "活动组织")
	private String organizer;

	@SchemaProperty(name = "报名截止时间")
	private Date registrationDeadline;

	@SchemaProperty(name = "活动状态(1:未开始,2:进行中,3:已结束)")
	private Integer status;

	@SchemaProperty(name = "发布人ID")
	private Integer publisherId;

	@SchemaProperty(name = "创建时间")
	private Date createTime;

	@SchemaProperty(name = "更新时间")
	private Date updateTime;


}
